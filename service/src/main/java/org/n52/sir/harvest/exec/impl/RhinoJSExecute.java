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
package org.n52.sir.harvest.exec.impl;

import java.io.File;
import java.io.FileReader;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;
import org.n52.sir.harvest.exec.IJSExecute;

public class RhinoJSExecute implements IJSExecute {
    
	public RhinoJSExecute() {
	    //
	}

	public RhinoJSExecute(String s) {
	    // FIXME implement method?!
	}

	@Override
	public String execute(String s) {
		Context cn = Context.enter();
		try {
			Global global = new Global();
			global.init(cn);
			Scriptable scope = cn.initStandardObjects(global);
			cn.setOptimizationLevel(-1);
			Object result = cn.evaluateString(scope, s, "<cmd>", 1, null);
			return Context.toString(result);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			Context.exit();
		}
	}

	@Override
	public String execute(File f) {
		Context cn = ContextFactory.getGlobal().enter();
		try {
			Global global = new Global();
			global.init(cn);
			Scriptable scope = cn.initStandardObjects(global);
			cn.setOptimizationLevel(-1);
			FileReader reader = new FileReader(f);
			Object result = cn.evaluateReader(scope, reader, "<cmd>", 1, null);
			reader.close();
			return Context.toString(result);
			
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			return null;
		} finally {
			Context.exit();
		}
	}

	private void setClassShutter(Context c) {
		c.setClassShutter(new ClassShutter() {

			@Override
			public boolean visibleToScripts(String arg0) {
				if (arg0.startsWith("org.n52")) {
					for (String s : RhinoConstants.allowed) {
						if (arg0.contains(s))
							return true;
					}
					return false;
					// TODO modify the security to be more aware of suspicious
					// methods and classes
				} else
					return true;
			}
		});

	}
}
