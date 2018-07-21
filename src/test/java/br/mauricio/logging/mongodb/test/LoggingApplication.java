package br.mauricio.logging.mongodb.test;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/rest")
public class LoggingApplication extends Application {
	private Set<Object> singletons = new HashSet<Object>();

	public LoggingApplication() {
		singletons.add(new MessageRestService());
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
	
	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> classes = new HashSet<>();
        classes.add(ContainerLoggingFilter.class);
        return classes;
	}
}