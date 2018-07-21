package br.mauricio.logging.mongodb.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoggingMongoDBTest {

	private static Server jettyServer;

	@BeforeClass
	public static void before() {
		jettyServer = new Server(8080);

		WebAppContext context = new WebAppContext();
		context.setParentLoaderPriority(true);
		context.setExtractWAR(true);
		context.setWar("src/main/webapp");
		context.setDescriptor("src/main/webapp/WEB-INF/test-web.xml");
		context.setContextPath("/");

		jettyServer.setHandler(context);
		try {
			jettyServer.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void logFirstData() {
		HttpURLConnection http;
		try {
			http = getHttp();
			OutputStreamWriter out = new OutputStreamWriter(http.getOutputStream());
			out.write("{ \"message\": \"Minha mensagem Maurício\" }");
			out.flush();
			out.close();
			http.connect();
			Assert.assertEquals("Response Code", HttpStatus.OK_200, http.getResponseCode());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testGETMethod() {
		HttpURLConnection http;
		try {
			http = (HttpURLConnection) new URL("http://localhost:8080/rest/message/mensagem").openConnection();
			http.setDoOutput(true);
			http.setRequestMethod("GET");
			http.setRequestProperty("Content-Type", "application/json");
			http.setRequestProperty("Accept", "application/json");
			http.connect();
			Assert.assertEquals("Response Code", HttpStatus.OK_200, http.getResponseCode());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testHugeJSON() {
		HttpURLConnection http;
		try {
			http = getHttp();
			OutputStreamWriter out = new OutputStreamWriter(http.getOutputStream());
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("smart-git.gz");
			String encodeToString = Base64.getEncoder().encodeToString(IOUtils.toByteArray(in));
			out.write("{ \"message\": \"Minha mensagem Maurício\", \"dadosArquivo\" : { \"conteudo\" : \"" + encodeToString + "\"} }");
			out.flush();
			out.close();
			http.connect();
			Assert.assertEquals("Response Code", HttpStatus.OK_200, http.getResponseCode());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testJSONFile() {
		HttpURLConnection http;
		try {
			http = getHttp();
			OutputStreamWriter out = new OutputStreamWriter(http.getOutputStream());
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("json_arquivo.json");
			try (Scanner scanner = new Scanner(in);) {
				StringBuilder sb = new StringBuilder();
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					sb.append(line.trim());
				}
				out.write(sb.toString());
				out.flush();
				out.close();
			}
			http.connect();
			Assert.assertEquals("Response Code", HttpStatus.OK_200, http.getResponseCode());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}

	private HttpURLConnection getHttp() throws IOException, MalformedURLException, ProtocolException {
		HttpURLConnection http;
		http = (HttpURLConnection) new URL("http://localhost:8080/rest/message").openConnection();
		http.setDoOutput(true);
		http.setRequestMethod("PUT");
		http.setRequestProperty("Content-Type", "application/json");
		http.setRequestProperty("Accept", "application/json");
		return http;
	}

	@AfterClass
	public static void stop() {
		try {
			jettyServer.stop();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
