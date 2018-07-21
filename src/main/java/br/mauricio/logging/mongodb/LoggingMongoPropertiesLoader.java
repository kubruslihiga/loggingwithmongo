package br.mauricio.logging.mongodb;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class LoggingMongoPropertiesLoader {

	private LoggingMongoPropertiesLoader() {

	}

	public static Map<LoggingMongoPropertiesKey, String> getPropertiesValues() {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("mongodb-connection.properties");
		if (in == null) {
			return new HashMap<>();
		}
		Properties props = new Properties();
		try {
			props.load(in);
			Map<LoggingMongoPropertiesKey, String> ret = new HashMap<>();
			for (LoggingMongoPropertiesKey key : LoggingMongoPropertiesKey.values()) {
				ret.put(key, props.getProperty(key.getStringKey()));
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

enum LoggingMongoPropertiesKey {

	CONNECTION_URL("mongodb.connection.url"), DATABASE_NAME("mongodb.database.name"), COLLECTION_NAME("mongodb.database.collection");

	String key;
	
	private LoggingMongoPropertiesKey(String key) {
		this.key = key;
	}
	
	public String getStringKey() {
		return key;
	}
}