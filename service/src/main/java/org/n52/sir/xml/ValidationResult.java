/**
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
package org.n52.sir.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ValidationResult {

    private boolean validated;

    private Collection<String> validationFailures;

    public ValidationResult(boolean validated, Collection<String> validationFailures) {
        super();
        this.validated = validated;
        this.validationFailures = validationFailures;
    }

    public ValidationResult(boolean validated) {
        this(validated, new ArrayList<String>());
    }

    public ValidationResult(boolean validated, Exception e) {
        this(validated, e.getMessage());
    }

    @SuppressWarnings("unchecked")
    public ValidationResult(boolean validated, String message) {
        this(validated, Arrays.asList(new String[] {message}));
    }

    public boolean isValidated() {
        return this.validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public Collection<String> getValidationFailures() {
        return this.validationFailures;
    }

    public void setValidationFailures(Collection<String> validationFailures) {
        this.validationFailures = validationFailures;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ValidationResult [validated=");
        builder.append(this.validated);
        builder.append(", ");
        if (this.validationFailures != null) {
            builder.append("validationFailures=");
            builder.append(this.validationFailures);
        }
        builder.append("]");
        return builder.toString();
    }

    public String getValidationFailuresAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("The document is NOT valid:\n");
        for (String string : this.validationFailures) {
            sb.append(string);
            sb.append("\n");
        }
        return sb.toString();
    }

}
