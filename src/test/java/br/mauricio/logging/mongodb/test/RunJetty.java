package br.mauricio.logging.mongodb.test;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class RunJetty {

	public static void main(String[] args) throws Exception {
		Server jettyServer = new Server(8080);

		WebAppContext context = new WebAppContext();
		context.setParentLoaderPriority(true);
		context.setExtractWAR(true);
		context.setWar("src/test/webapp");
		context.setDescriptor("src/test/webapp/WEB-INF/test-web.xml");
		context.setContextPath("/");

		jettyServer.setHandler(context);
		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
			jettyServer.destroy();
		}
	}
}
