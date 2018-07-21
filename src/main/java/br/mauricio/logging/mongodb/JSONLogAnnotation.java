package br.mauricio.logging.mongodb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.NameBinding;

@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JSONLogAnnotation {

	Class<? extends ExtractJSONData> extractor() default ExtractJSONDataImpl.class;
}
