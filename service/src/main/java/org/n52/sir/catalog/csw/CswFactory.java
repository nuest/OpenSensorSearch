/*
 * Copyright (C) 2013 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * License version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */
package org.n52.sir.catalog.csw;

import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oss.sir.ows.OwsExceptionReport;
import org.n52.sir.catalog.CatalogConnectionImpl;
import org.n52.sir.catalog.ICatalog;
import org.n52.sir.catalog.ICatalogConnection;
import org.n52.sir.catalog.ICatalogFactory;
import org.n52.sir.ds.ISearchSensorDAO;
import org.n52.sir.xml.IProfileValidator;
import org.n52.sir.xml.ITransformer;
import org.n52.sir.xml.ITransformer.TransformableFormat;
import org.n52.sir.xml.TransformerModule;
import org.n52.sir.xml.ValidatorModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author <a href="mailto:d.nuest@52north.org">Daniel Nüst</a>
 *
 */
public class CswFactory implements ICatalogFactory {

    private static final String CONFIG_FILE_LIST_SEPARATOR = ",";

    private static final Logger log = LoggerFactory.getLogger(CswFactory.class);

    private List<XmlObject> classificationInitDocs;

    private String[] classificationInitFilesArray;

    private XmlObject slotInitDoc;

    private String slotInitFile;

    private ITransformer transformer;

    private ArrayList<URL> doNotCheckCatalogsList;

    private ISearchSensorDAO searchDao;

    private final IProfileValidator validator;

    @Inject
    public CswFactory(@Named("oss.catalogconnection.csw-ebrim.classificationInitFilenames") String classificationInitFilenames, @Named("oss.catalogconnection.csw-ebrim.slotInitFilename") String slotInitFile, @Named("oss.catalogconnection.doNotCheckCatalogs") String doNotCheckCatalogs, Set<ITransformer> transformers, @Named(ISearchSensorDAO.FULL) ISearchSensorDAO searchDao, Set<IProfileValidator> validators, @Named("oss.catalogconnection.logLoadedFiles") boolean logLoadedFiles) throws XmlException, IOException {
        this.slotInitFile = getAbsolutePath(slotInitFile);
        this.searchDao = searchDao;

        try (FileReader reader = new FileReader(this.slotInitFile);) {
            this.slotInitDoc = XmlObject.Factory.parse(reader);

            if (logLoadedFiles) {
                log.debug("Loaded slot init doc: {}", this.slotInitDoc);
            }
        }

        this.transformer = TransformerModule.getFirstMatchFor(transformers,
                TransformableFormat.SML,
                TransformableFormat.EBRIM);

        XmlObject initDoc;
        this.classificationInitDocs = new ArrayList<>();
        this.classificationInitFilesArray = classificationInitFilenames.split(CONFIG_FILE_LIST_SEPARATOR);
        for (String filename : this.classificationInitFilesArray) {
            String filePath = getAbsolutePath(filename.trim());
            try (FileReader reader = new FileReader(filePath);) {
                initDoc = XmlObject.Factory.parse(reader);
                this.classificationInitDocs.add(initDoc);
            }
        }
        log.debug("Loaded classification files: {}", Arrays.toString(this.classificationInitFilesArray));

        this.doNotCheckCatalogsList = new ArrayList<>();
        String[] splitted = doNotCheckCatalogs.split(CONFIG_FILE_LIST_SEPARATOR);
        if (splitted.length > 0) {
            for (String s : splitted) {
                try {
                    if (!s.isEmpty()) {
                        this.doNotCheckCatalogsList.add(new URL(s.trim()));
                    }
                } catch (MalformedURLException e) {
                    log.error("Could not parse catalog url to 'do not check' list. Catalog will be checked during runtime!",
                            e);
                }
            }
        }
        log.debug("Loaded do-not-check-catalogs: {}", Arrays.toString(this.doNotCheckCatalogsList.toArray()));

        this.validator = ValidatorModule.getFirstMatchFor(validators,
                IProfileValidator.ValidatableFormatAndProfile.SML_DISCOVERY);

        log.info("NEW {}", this);
    }

    private String getAbsolutePath(String file) {
        try {
            URL r = getClass().getResource(file);
            Path p = Paths.get(r.toURI());
            String s = p.toAbsolutePath().toString();
            return s;
        } catch (URISyntaxException e) {
            log.error("Could not load resource " + file, e);
            return file;
        }
    }

    @Override
    public ICatalog getCatalog(URL catalogUrl) throws OwsExceptionReport {
        SimpleSoapCswClient client = new SimpleSoapCswClient(catalogUrl, catalogIsOnDoNotCheckList(catalogUrl));
        CswCatalog catalog = new CswCatalog(this.searchDao,
                client,
                this.classificationInitDocs,
                this.slotInitDoc,
                this.transformer,
                this.validator);
        return catalog;
    }

    private boolean catalogIsOnDoNotCheckList(URL catalogUrl) {
        return this.doNotCheckCatalogsList.contains(catalogUrl);
    }

    @Override
    public ICatalogConnection getCatalogConnection(String connectionID,
            URL url,
            int pushInterval,
            String newConnectionStatus) {
        return new CatalogConnectionImpl(connectionID, url, pushInterval, ICatalogConnection.NEW_CONNECTION_STATUS);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CswFactory [");
        if (this.classificationInitDocs != null) {
            builder.append("classificationInitDocs count=");
            builder.append(this.classificationInitDocs.size());
            builder.append(", ");
        }
        if (this.classificationInitFilesArray != null) {
            builder.append("classificationInitFiles=");
            builder.append(Arrays.toString(this.classificationInitFilesArray));
            builder.append(", ");
        }
        if (this.slotInitDoc != null) {
            builder.append("slotInitDoc=");
            builder.append(this.slotInitDoc.getDomNode());
            builder.append(", ");
        }
        if (this.slotInitFile != null) {
            builder.append("slotInitFile=");
            builder.append(this.slotInitFile);
        }
        builder.append("]");
        return builder.toString();
    }

}
