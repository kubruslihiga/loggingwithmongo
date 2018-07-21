package br.mauricio.logging.mongodb;

import static br.mauricio.logging.mongodb.LoggingMongoPropertiesKey.COLLECTION_NAME;
import static br.mauricio.logging.mongodb.LoggingMongoPropertiesKey.CONNECTION_URL;
import static br.mauricio.logging.mongodb.LoggingMongoPropertiesKey.DATABASE_NAME;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

public class LoggingMongoService {

	private Map<LoggingMongoPropertiesKey, String> props = LoggingMongoPropertiesLoader.getPropertiesValues();

	private static LoggingMongoService service = new LoggingMongoService();

	private LoggingMongoService() {
	}

	public static final LoggingMongoService getInstance() {
		return service;
	}

	public void insert(String loggingJson) {
		Document doc = Document.parse(loggingJson);
		insert(doc);
	}

	public ObjectId insertGridFS(String base64String) {
		byte[] decoded = Base64.getDecoder().decode(base64String);
		return insertGridFS(decoded);
	}

	public ObjectId insertGridFS(byte[] decoded) {
		MongoClient mongoClient = getMongoClient();
		MongoDatabase database = getMongoDatabase(mongoClient);
		GridFSBucket gridFSBucket = GridFSBuckets.create(database, "loggingfs");
		GridFSUploadOptions options = new GridFSUploadOptions().chunkSizeBytes(1024 * 1024).metadata(new Document("type", "presentation"));
		ObjectId ret = gridFSBucket.uploadFromStream("mongodb-file", new ByteArrayInputStream(decoded), options);
		return ret;
	}

	private void insert(Document document) {
		MongoClient mongoClient = getMongoClient();
		MongoDatabase database = getMongoDatabase(mongoClient);
		MongoCollection<Document> collection = getMongoCollection(database);
		collection.insertOne(document);
		mongoClient.close();
	}
	
	private MongoCollection<Document> getMongoCollection(MongoDatabase database) {
		String collectionName = "loggingcol";
		if (props.get(COLLECTION_NAME) != null) {
			collectionName = props.get(COLLECTION_NAME);
		}
		MongoCollection<Document> collection = database.getCollection(collectionName);
		return collection;
	}

	private MongoDatabase getMongoDatabase(MongoClient mongoClient) {
		String mongoDatabaseName = "loggingdb";
		if (props.get(DATABASE_NAME) != null) {
			mongoDatabaseName = props.get(DATABASE_NAME);
		}
		MongoDatabase database = mongoClient.getDatabase(mongoDatabaseName);
		return database;
	}

	private MongoClient getMongoClient() {
		String urlConnection = "mongodb://localhost:27017";
		if (props.get(CONNECTION_URL) != null) {
			urlConnection = props.get(CONNECTION_URL);
		}
		MongoClient mongoClient = MongoClients.create(urlConnection);
		return mongoClient;
	}

}
