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
/** @author Yakoub
 */

package org.n52.oss.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadForm {

    private static final Logger log = LoggerFactory.getLogger(UploadForm.class);

    private String name;

    private CommonsMultipartFile file;

    private String license;

    public UploadForm() {
        // added to allow spring to instantiate
        log.info("NEW {}", this);
    }

    public CommonsMultipartFile getFile() {
        return this.file;
    }

    public String getLicense() {
        return this.license;
    }

    public String getName() {
        return this.name;
    }

    public void setFile(CommonsMultipartFile file) {
        this.file = file;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setName(String name) {
        this.name = name;
    }
}
