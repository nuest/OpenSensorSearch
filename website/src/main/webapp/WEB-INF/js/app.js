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
var ossApp = angular.module("oss.app", [ "ngRoute", "ui.bootstrap",
		"oss.controllers", "ngSanitize", "angucomplete-alt" ]);

ossApp.constant("typeaheadEndpoint", ossApiEndpoint + "/suggest");
ossApp.constant("apiEndpoint", ossApiEndpoint + "/search");
ossApp.constant("apiEndpoint_sensors", ossApiEndpoint + "/sensors");
// ossApp.constant("geolabelEndpoint",
// "http://geoviqua.dev.52north.org/glbservice/api/v1/svg");
ossApp.constant("geolabelEndpoint",
		"http://localhost:8080/glbservice/api/v1/svg");
ossApp.constant("feedbackServerEndpoint",
		"http://geoviqua.stcorp.nl/devel/api/v1/feedback/collections/search");
ossApp.constant("feedbackSubmitEndpoint",
		"https://geoviqua.stcorp.nl/devel/submit_feedback.html");
ossApp.constant("targetCodespace", "http://opensensorsearch.net/");

ossApp.config([ "$routeProvider", function($routeProvider) {
	$routeProvider.when("/conversion", {
		templateUrl : "resources/partials/conversion.html",
		controller : "oss.conversionControl"
	}).when("/discoveryProfile", {
		templateUrl : "resources/partials/discoveryProfile.html",
		controller : "oss.profileControl"
	}).when("/harvesting/developers", {
		templateUrl : "resources/partials/harvesting-developers.html",
		controller : "oss.harvestControl"
	}).when("/harvesting/script", {
		templateUrl : "resources/partials/harvesting-script.html",
		controller : "oss.harvestControl"
	}).when("/harvesting/oss", {
		templateUrl : "resources/partials/harvesting-oss.html",
		controller : "oss.harvestControl"
	}).when("/harvesting/ows", {
		templateUrl : "resources/partials/harvesting-ows.html",
		controller : "oss.harvestControl"
	}).when("/api", {
		templateUrl : "resources/partials/api.html",
		controller : "oss.apiControl"
	}).when("/about", {
		templateUrl : "resources/partials/about.html"
	}).when("/contact", {
		templateUrl : "resources/partials/about.html"
	}).when("/", {
		templateUrl : "resources/partials/search.html",
		controller : "oss.searchControl"
	}).otherwise({
		redirectTo : "/"
	});
} ]);