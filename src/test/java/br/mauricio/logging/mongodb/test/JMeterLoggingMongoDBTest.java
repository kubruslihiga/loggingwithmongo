package br.mauricio.logging.mongodb.test;

import java.io.File;
import java.io.IOException;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JMeterLoggingMongoDBTest {

	private static Server jettyServer;

	@BeforeClass
	public static void before() {
		jettyServer = new Server(9080);

		WebAppContext context = new WebAppContext();
		context.setParentLoaderPriority(true);
		context.setExtractWAR(true);
		context.setWar("src/test/webapp");
		context.setDescriptor("src/test/webapp/WEB-INF/test-web.xml");
		context.setContextPath("/");

		jettyServer.setHandler(context);
		try {
			jettyServer.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testJMeterPUTService() throws IOException {
		// JMeter Engine
		StandardJMeterEngine jmeter = new StandardJMeterEngine();

		// Initialize Properties, logging, locale, etc.
		JMeterUtils.loadJMeterProperties("/opt/jmeter/bin/jmeter.properties");
		JMeterUtils.setJMeterHome("/opt/jmeter");
		JMeterUtils.initLocale();

		// Initialize JMeter SaveService
		SaveService.loadProperties();

		// Load existing .jmx Test Plan
		File in = new File("src/test/jmeter/mongo.jmx");
		if (in.exists()) {
			HashTree testPlanTree = SaveService.loadTree(in);

			// Run JMeter Test
			jmeter.configure(testPlanTree);
			jmeter.run();
		}
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
