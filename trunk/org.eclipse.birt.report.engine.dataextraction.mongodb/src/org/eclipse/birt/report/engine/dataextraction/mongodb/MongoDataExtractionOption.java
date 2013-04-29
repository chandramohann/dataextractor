/*
 * Copyright (c) 2013 Chandramohan N. All rights reserved. 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: Chandramohan N
 */
package org.eclipse.birt.report.engine.dataextraction.mongodb;

import java.util.Map;

import org.eclipse.birt.report.engine.dataextraction.CommonDataExtractionOption;

public class MongoDataExtractionOption extends CommonDataExtractionOption
		implements IMongoDataExtractionOption {
	public MongoDataExtractionOption() {
		super();
	}

	public MongoDataExtractionOption(Map options) {
		super(options);
	}

	public String getSeparator() {
		return null;
	}

	public void setSeparator(String sep) {

	}

}
