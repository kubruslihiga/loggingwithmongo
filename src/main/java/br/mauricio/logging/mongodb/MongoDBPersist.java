package br.mauricio.logging.mongodb;

import java.util.Arrays;

import org.bson.Document;
import org.bson.internal.Base64;

import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class MongoDBPersist {

	public void persist() {
		MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
		MongoDatabase database = mongoClient.getDatabase("mydb");
		MongoCollection<Document> collection = database.getCollection("d");
		String jsonToAudit = "{\"atributo\" : \"valor\"}";
		String base64Encoded = Base64.encode(jsonToAudit.getBytes());
		Document doc = new Document("name", "MongoDB").append("type", "database").append("count", 1)
				.append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
				.append("info", new Document("x", 203).append("y", 102)).append("jsonAudit", base64Encoded);
		collection.insertOne(doc);
		System.out.println(collection.countDocuments());
		Document myDoc = collection.find().first();
		System.out.println(myDoc.toJson());
		Document doc2 = Document.parse("{\"nome\" : \"Mauricio\", \"idade\": 12, \"nacionalidade\" : \"BR\" }");
		collection.insertOne(doc2);
		Block<Document> printBlock = new Block<Document>() {
			public void apply(final Document document) {
				System.out.println(document.size());
				System.out.println(document.toJson());
			}
		};
		collection.find(Filters.eq("name", "MongoDB")).forEach(printBlock);
		mongoClient.close();
	}
}
