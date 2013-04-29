/*
 * Copyright (c) 2013 Megha Nidhi Dahal. All rights reserved. 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: Chandramohan N
 */
package org.eclipse.birt.report.engine.dataextraction.mongodb;

import java.net.UnknownHostException;
import java.util.LinkedList;

import org.eclipse.birt.core.exception.BirtException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

public class MongoDataExtractionUtil {

	private static MongoClient client;
	private static DB db;

	public static void insertCollection(DBCollection collection,
			LinkedList<DBObject> basicDBList) throws BirtException {
		try {
			collection.insert(basicDBList);
		} catch (Exception e) {
			client.close();
			throw new BirtException(e.getMessage());
		}
	}

	public static void connect(String mongoDbUrl, Integer port)
			throws UnknownHostException {
		try {			
			 client = new MongoClient(mongoDbUrl, port);			 
			 client.getDatabaseNames();//check for mongod server status
		} catch (MongoException e) {
			throw new UnknownHostException(e.getMessage());
		}catch (UnknownHostException  me) {
			throw new UnknownHostException(me.getMessage());
		}
	}

	public static void disconnect() throws BirtException {
		try {
			client.close();
		} catch (Exception e) {
			throw new BirtException(e.getMessage());
		}
	}

	/**
	 * @param mongoDbUrl
	 * @param mongodb
	 *            url mongoDbport mongodb port default 27017 mongoDb
	 * @param mongodb
	 *            to connect to dBCollection defaulted to reportname
	 * 
	 * @return DBCollection
	 * @throws BirtException
	 */

	/*
	 * Enhancements: Pass the opcode paramater to perform truncate & load or
	 * Delta load
	 */
	public static DBCollection createCollection(String dB ,String dBCollection)
			throws BirtException {
		DBCollection cursor = null;
		try {
			db = client.getDB(dB);
			cursor = db.getCollection(dBCollection);
		} catch (Exception e) {
			throw new BirtException(e.getMessage());
		}
		/*
		 * Truncate and load the data remove the below line to make it as delta
		 * load to the collection
		 */
		cursor.drop();
		return cursor;
	}
}
