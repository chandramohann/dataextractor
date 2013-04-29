/**
 * 
 */
package org.eclipse.birt.report.engine.dataextraction.mongodb;

import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.IDataExtractionOption;
import org.eclipse.birt.report.engine.api.IDataIterator;
import org.eclipse.birt.report.engine.api.IExtractionResults;
import org.eclipse.birt.report.engine.api.script.IReportContext;
import org.eclipse.birt.report.engine.dataextraction.impl.CommonDataExtractionImpl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * @author chandru
 * 
 */

public class MongoDataExtractionImpl extends CommonDataExtractionImpl {
	public static final String PLUGIN_ID = "org.eclipse.birt.report.engine.dataextraction.mongo"; //$NON-NLS-1$
	public static final String DEFAULT_ENCODING = Charset.defaultCharset()
			.name();
	private BasicDBObject basicDBObject;
	private OutputStream outputStream;
	private String[] selectedColumnNames;
	private String reportName;
	private String userParameters;
	private String mongoDBUrl = "localhost";// defaulted to localhost
	private String mongoDB; // reportname is the default mongodb name
	private Integer mongoDBPort = 27017;
	private String resultSetName;// will be the collection name in mongodb

	public void initialize(IReportContext context, IDataExtractionOption options)
			throws BirtException {

		super.initialize(context, options);
		context.getDesignHandle();
		String mongoUrl = (String) context.getPageVariable("mongoDBUrl");
		if (mongoUrl != null && !"".equals(mongoUrl.trim())) {
			mongoDBUrl = mongoUrl;
		}
		String port = (String) context.getPageVariable("mongoDBPort");
		if (port != null && !"".equals(port.trim())) {
			mongoDBPort = Integer.valueOf(mongoDBPort);
		}
		mongoDB = (String) context.getPageVariable("mongoDB");
		initMongoOptions(options);
	}

	private void initMongoOptions(IDataExtractionOption options) {

		this.outputStream = options.getOutputStream();
		IMongoDataExtractionOption mongoOption;
		if (options instanceof IMongoDataExtractionOption) {
			mongoOption = (IMongoDataExtractionOption) options;
		} else {
			mongoOption = new MongoDataExtractionOption(options.getOptions());
		}

		Map pUserParameters = mongoOption.getUserParameters();
		userParameters = getUserParameters(pUserParameters);
		getReportNameandResultSetName(pUserParameters);
	}

	private String getUserParameters(Map pUserParameters) {
		Map uParameters = pUserParameters;
		String pParams = "";
		// Iterate over the keys, values in the map
		if (uParameters != null) {
			java.util.Iterator it = null;

			for (it = (java.util.Iterator) uParameters.keySet().iterator(); ((java.util.Iterator) it)
					.hasNext();) {
				Object key = it.next();
				Object value = uParameters.get(key);
				pParams = pParams + key + "=" + value + ",";
			}
		}
		String stripParams = "";
		if (pParams.length() == 0)
			stripParams = "";
		else
			stripParams = pParams.substring(0, pParams.lastIndexOf(","));
		return stripParams;
	}

	private void getReportNameandResultSetName(Map pUserParameters) {
		Map uParameters = pUserParameters;
		// Iterate over the keys, values in the map
		if (uParameters != null) {
			java.util.Iterator it = null;

			for (it = (java.util.Iterator) uParameters.keySet().iterator(); ((java.util.Iterator) it)
					.hasNext();) {
				Object key = it.next();
				Object value = uParameters.get(key);

				if (key.toString().equalsIgnoreCase("__report")) {
					reportName = value.toString();
				}
				if (key.toString().equalsIgnoreCase("__resultsetname")) {
					resultSetName = value.toString();
				}
			}
		}
	}

	public void output(IExtractionResults results) throws BirtException {
		String[] columnNames = (String[]) selectedColumnNames;
		DBCollection dBCollection = null;
		LinkedList<DBObject> basicDBObjects = null;
		if (columnNames == null || columnNames.length <= 0) {
			int count = results.getResultMetaData().getColumnCount();
			columnNames = new String[count];
			for (int i = 0; i < count; i++) {
				String colName = results.getResultMetaData().getColumnName(i);
				columnNames[i] = colName;
			}
		}

		IDataIterator iData = null;
		if (results != null) {
			iData = results.nextResultIterator();
			if (iData != null && columnNames.length > 0) {
				String[] values = new String[columnNames.length];
				basicDBObjects = new LinkedList<DBObject>();
				while (iData.next()) {
					basicDBObject = new BasicDBObject();
					for (int i = 0; i < columnNames.length; i++) {
						String columnName = results.getResultMetaData()
								.getColumnName(i);
						if (iData.getValue(columnNames[i]) != null) {
							values[i] = iData.getValue(columnNames[i])
									.toString();
						} else {
							values[i] = "";
						}
						basicDBObject.append(columnName, values[i]);
					}
					basicDBObjects.add(basicDBObject);
				}
			}
		}

		/*
		 * if the mongoDB page variable is null then reportname will be the db
		 * name
		 */
		int endIndex = reportName.lastIndexOf('.');
		int beginIndex = reportName.lastIndexOf('\\') + 1;

		if (mongoDB == null && beginIndex > 0 && endIndex > 0) {
			mongoDB = reportName.substring(beginIndex, endIndex).toLowerCase();
		}		
		String outputString = getOutputStream(basicDBObjects);
		
		/*
		 * insert to mongodb only if basicDBObjects is not null
		 * *.json file would be generated to so that it can be imported into
		 * monogodb manually
		 */

		if (!basicDBObjects.isEmpty()) {
			try {
				MongoDataExtractionUtil.connect(mongoDBUrl, mongoDBPort);
				dBCollection = MongoDataExtractionUtil.createCollection(mongoDB,
						resultSetName);
				MongoDataExtractionUtil.insertCollection(dBCollection,
						basicDBObjects);
				MongoDataExtractionUtil.disconnect();
			} catch (UnknownHostException e) {				
				e.printStackTrace();				
			}
		}

		/*
		 * Write *.json file to import into monogodb manually
		 */

		try {
			outputStream.write(outputString.getBytes());
		} catch (IOException e) {
			throw new BirtException(e.getMessage());
		}
	}

	/**
	 * @param basicDBObjects
	 * @return formated outputString for outstream
	 */
	private String getOutputStream(LinkedList<DBObject> basicDBObjects) {
		//System.out.println(basicDBObjects);
		String outputString = basicDBObjects.toString();
		outputString = outputString.replace(", {", "\r\n{");
		outputString = outputString.replace("[{", "{");
		outputString = outputString.replace("}]", "}");
		return outputString;
	}

}
