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
