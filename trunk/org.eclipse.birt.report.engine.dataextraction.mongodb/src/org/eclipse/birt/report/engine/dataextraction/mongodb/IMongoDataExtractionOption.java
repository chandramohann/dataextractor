/*
 * Copyright (c) 2013 Chandramohan N. All rights reserved. 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: Chandramohan N
 */

package org.eclipse.birt.report.engine.dataextraction.mongodb;

import org.eclipse.birt.report.engine.dataextraction.ICommonDataExtractionOption;

/**
 * Extends Data Extraction options for json format
 * 
 */
public interface IMongoDataExtractionOption extends ICommonDataExtractionOption {

	/**
	 * the separator
	 */
	public static final String OUTPUT_SEPARATOR = "Separator"; //$NON-NLS-1$

	/**
	 * Sets the output separator
	 * 
	 * @param sep
	 */
	void setSeparator(String sep);

	/**
	 * Returns the output separator
	 * 
	 * @return String
	 */
	String getSeparator();
}
