/**
 * Copyright 2013 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n52.sir.xml.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Named;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.opengis.sensorML.x101.SensorMLDocument;
import net.sf.saxon.Transform;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

import com.google.inject.Inject;

/**
 * @author Daniel Nüst
 * 
 */
public class SensorML4DiscoveryValidatorImpl implements IProfileValidator {

    private class SchematronResultHandler extends DefaultHandler {

        // protected Logger logger = LoggerFactory.getLogger(SchematronResultHandler.class);

        private String failTmp;
        private boolean insideFail = false;
        private Locator locator;
        private String patternTmp;
        private String ruleTmp;

        public SchematronResultHandler() {
            super();
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (this.insideFail) {
                String s = new String(ch, start, length).trim();
                this.failTmp += s;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.endsWith(QNAME_FAILED_ASSERT)) {
                getAssertionFailures().add(this.failTmp);
                this.failTmp = null;
                this.insideFail = false;
            }
            else if (qName.endsWith(QNAME_FIRED_RULE)) {
                getFiredRules().add(this.ruleTmp);
                // this.logger.debug("Finished with rule {}", this.ruleTmp);
                this.ruleTmp = null;
            }
            else if (qName.endsWith(QNAME_ACTIVE_PATTERN)) {
                getActivatedPatterns().add(this.patternTmp);
                this.patternTmp = null;
            }
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.endsWith(QNAME_FAILED_ASSERT)) {
                this.failTmp = "[line " + this.locator.getLineNumber() + ", col " + this.locator.getColumnNumber()
                        + "]\tAssertion error at \"" + attributes.getValue(ATTRIBUTE_NAME_TEST) + "\""
                        + " (location: \"" + attributes.getValue(ATTRIBUTE_NAME_LOCATION) + "\"): ";
                this.insideFail = true;
            }
            else if (qName.endsWith(QNAME_FIRED_RULE)) {
                this.ruleTmp = "Fired rule in context \"" + attributes.getValue(ATTRIBUTE_NAME_CONTEXT) + "\" (line "
                        + this.locator.getLineNumber() + ").";
            }
            else if (qName.endsWith(QNAME_ACTIVE_PATTERN)) {
                this.patternTmp = "Active pattern id: \"" + attributes.getValue(ATTRIBUTE_NAME_ID) + " -- name: \""
                        + attributes.getValue(ATTRIBUTE_NAME_NAME) + "\" (line " + this.locator.getLineNumber() + ").";
            }
        }

    }

    protected static final String ATTRIBUTE_NAME_CONTEXT = "context";

    protected static final String ATTRIBUTE_NAME_ID = "id";

    protected static final String ATTRIBUTE_NAME_LOCATION = "location";

    protected static final String ATTRIBUTE_NAME_NAME = "name";

    protected static final String ATTRIBUTE_NAME_TEST = "test";

    protected static Logger log = LoggerFactory.getLogger(SensorML4DiscoveryValidatorImpl.class);

    protected static final String QNAME_ACTIVE_PATTERN = "active-pattern";

    protected static final String QNAME_FAILED_ASSERT = "failed-assert";

    protected static final String QNAME_FIRED_RULE = "fired-rule";

    protected File tempXSLFile = null;

    private static TransformerFactory tFactory = TransformerFactory.newInstance();

    private List<String> activatedPatterns = new ArrayList<>();

    private List<String> assertionFailures = new ArrayList<>();

    private List<String> firedRules = new ArrayList<>();

    private Future<Transformer> transformerFuture;

    @Inject
    public SensorML4DiscoveryValidatorImpl(@Named("oss.sir.validation.profile.sml.discovery")
    String profilePath, @Named("oss.sir.validation.svrlSchema")
    String svrlSchemaPath) throws URISyntaxException {
        initialize(profilePath, svrlSchemaPath);

        log.debug("NEW {}", this);
    }

    private synchronized ValidationResult actualValidate(SensorMLDocument smlDoc) throws IOException {
        log.debug("Validating SensorMLDocument against Discovery Profile...");
        reset();

        // encapsulate input document in a Source
        Source input = new DOMSource(smlDoc.getDomNode());

        // create output string
        try (StringWriter sw = new StringWriter();) {
            StreamResult output = new StreamResult(sw);

            // do the transformation
            try {
                Transformer t = this.transformerFuture.get();
                log.debug("Starting transformation from {} to {} using {}", input, output, t);
                t.transform(input, output);

                String outputString = output.getWriter().toString();

                processSVRL(new InputSource(new StringReader(outputString)));
            }
            catch (TransformerException | SAXException | IOException e) {
                log.error("Error transforming SensorML for validation against profile for discovery!", e);
                return new ValidationResult(false, e);
            }
            catch (ParserConfigurationException e) {
                log.error("Error processing SVRL output!", e);
                return new ValidationResult(false, e);
            }
            catch (InterruptedException | ExecutionException e) {
                log.error("Error with getting transformer from Future.", e);
                return new ValidationResult(false, e);
            }
        }

        log.debug("Validation result: {} failures, {} activated patterns, and {} fired rules.",
                  this.assertionFailures.size(),
                  this.activatedPatterns.size(),
                  this.firedRules.size());

        boolean validated = (this.getAssertionFailures().size() == 0) ? true : false;
        ValidationResult result = new ValidationResult(validated, this.assertionFailures);

        log.debug("Validation finished: {}", result);
        return result;
    }

    protected synchronized void reset() {
        log.debug("Reset!");

        this.activatedPatterns = new ArrayList<>();
        this.assertionFailures = new ArrayList<>();
        this.firedRules = new ArrayList<>();
    }

    public List<String> getActivatedPatterns() {
        return this.activatedPatterns;
    }

    public List<String> getAssertionFailures() {
        return this.assertionFailures;
    }

    public List<String> getFiredRules() {
        return this.firedRules;
    }

    private synchronized void initialize(final String profilePath, final String svrlSchemaPath) throws URISyntaxException {
        final File discoveryFile = new File(getClass().getResource(profilePath).toURI());
        final File svrlFile = new File(getClass().getResource(svrlSchemaPath).toURI());

        try {
            this.tempXSLFile = File.createTempFile(discoveryFile.getName(), ".xsl");
            // tempXSLFile.canWrite();
        }
        catch (IOException e) {
            log.error("Cannot create temporary schema file for schematron rules", e);
            return;
        }

        log.debug("Initializing validator with Schematron file from {} and SVRL from {} into file {}",
                  discoveryFile,
                  svrlFile,
                  this.tempXSLFile);

        // run this thread if the transformed file does not exist or if the transformer future is not yet
        // created
        if ( !this.tempXSLFile.exists() || this.transformerFuture == null) {
            log.debug("XSL for schematron not found, create it.");

            this.transformerFuture = Executors.newSingleThreadExecutor().submit(new Callable<Transformer>() {

                @Override
                public Transformer call() throws Exception {
                    log.debug("Creating XSL from schematron now...");

                    // http://blog.eight02.com/2011/05/validating-xml-with-iso-schematron-on.html
                    String[] arguments = new String[] {"-x:org.apache.xerces.parsers.SAXParser",
                                                       // "-w1",
                                                       "-o:"
                                                               + SensorML4DiscoveryValidatorImpl.this.tempXSLFile.getAbsolutePath(),
                                                       "-s:" + discoveryFile.getAbsolutePath(),
                                                       svrlFile.getAbsolutePath() // "docs/iso_svrl_for_xslt2.xsl",
                    // "generate-paths=yes"
                    };
                    log.debug("Transformation arguments: {}", Arrays.toString(arguments));

                    // transform the schematron to XSL,
                    // http://www.saxonica.com/documentation/index.html#!using-xsl/commandline
                    Transform trans = new Transform();

                    trans.doTransform(arguments, "java net.sf.saxon.Transform");

                    log.info("Created XSL file for validation: {}", SensorML4DiscoveryValidatorImpl.this.tempXSLFile);

                    log.debug("Creating transformer...");
                    StreamSource source = new StreamSource(SensorML4DiscoveryValidatorImpl.this.tempXSLFile);
                    Transformer t = tFactory.newTransformer(source);
                    log.debug("Created {}", t);

                    return t;
                }
            });

        }
        else
            log.debug("Reusing existing XSL file {} for {}.", this.tempXSLFile, profilePath);
    }

    private void processSVRL(InputSource inputSource) throws SAXException, IOException, ParserConfigurationException {
        log.debug("Processing SVRL now: {}", inputSource);

        DefaultHandler handler = new SchematronResultHandler();
        LocatorImpl locator = new LocatorImpl();
        handler.setDocumentLocator(locator);

        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        parser.parse(inputSource, handler);
        handler = null;
        locator = null;
        parser = null;
    }

    public void setActivatedPatterns(List<String> patterns) {
        this.activatedPatterns = patterns;
    }

    protected void setAssertionFailures(List<String> assertionFailures) {
        this.assertionFailures = assertionFailures;
    }

    public void setFiredRules(List<String> firedRules) {
        this.firedRules = firedRules;
    }

    @Override
    public ValidationResult validate(File file) throws OwsExceptionReport {
        try {
            SensorMLDocument smlDoc = SensorMLDocument.Factory.parse(file);
            return validate(smlDoc);
        }
        catch (XmlException e) {
            log.error("XmlException when parsing SensorMLDocument from file.", e);
            throw new OwsExceptionReport("Could not test given file for compliance with profile for discovery!", e);
        }
        catch (IOException e) {
            log.error("IOException when parsing SensorMLDocument from file.", e);
            throw new OwsExceptionReport("Could not test given file for compliance with profile for discovery!", e);
        }
    }

    @Override
    public ValidationResult validate(SensorMLDocument smlDoc) throws IOException {
        return this.actualValidate(smlDoc);
    }

    @Override
    public ValidationResult validate(XmlObject xml) throws IOException {
        if (xml instanceof SensorMLDocument) {
            SensorMLDocument smlDoc = (SensorMLDocument) xml;
            return validate(smlDoc);
        }
        log.error("The given XmlObject could was not a SensorMLDocument!");

        return new ValidationResult(false);
    }

    @Override
    public boolean validates(ValidatableFormatAndProfile profile) {
        return IProfileValidator.ValidatableFormatAndProfile.SML_DISCOVERY.equals(profile);
    }

}
