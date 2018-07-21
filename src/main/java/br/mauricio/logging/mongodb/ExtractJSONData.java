package br.mauricio.logging.mongodb;

public interface ExtractJSONData {

	default String extractData(String json) {
		return json;
	}
}
