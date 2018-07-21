package br.mauricio.logging.mongodb.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.bson.Document;

import br.mauricio.logging.mongodb.ExtractJSONData;
import br.mauricio.logging.mongodb.JSONLogAnnotation;
import br.mauricio.logging.mongodb.LoggingMongoService;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class ContainerLoggingFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	@Context
	private HttpServletRequest request;

	@Context
	private UriInfo uriInfo;

	public void filter(ContainerRequestContext requestContext) throws IOException {
		Method resourceMethod = resourceInfo.getResourceMethod();
		JSONLogAnnotation annotation = resourceMethod.getAnnotation(JSONLogAnnotation.class);
		ExtractJSONData instance;
		try {
			LoggingMongoService service = LoggingMongoService.getInstance();
			Document document = new Document();
			document.put("url", uriInfo.getAbsolutePath().toString());
			document.put("method", requestContext.getMethod());
			document.put("mediaType", requestContext.getMediaType().toString());
			document.put("executionDate", new Date());
			document.put("ip", request.getRemoteAddr());
			
			InputStream entityStream = requestContext.getEntityStream();
			String json = IOUtils.toString(entityStream, StandardCharsets.UTF_8);
			InputStream in = IOUtils.toInputStream(json, StandardCharsets.UTF_8);
            requestContext.setEntityStream(in);

            String jsonToPersist = null;
            if (annotation != null) {
            	instance = annotation.extractor().newInstance();
    			jsonToPersist = instance.extractData(json);
            }

			if (jsonToPersist != null && !jsonToPersist.isEmpty()) {
				Document documentJSON = checkSizeValues(jsonToPersist);
				document.put("messageBody", documentJSON);
//				document.put("messageBodyCompressed", Boolean.FALSE);
//				if (jsonToPersist.getBytes().length > 3145728) {
//					byte[] compressed = compress(jsonToPersist);
//					if (compressed.length > 16777215) {
//						ObjectId objectId = service.insertGridFS(compressed);
//						document.put("messageBody", objectId);
//					} else {
//						document.put("messageBody", compressed);
//					}
//					document.put("messageBodyCompressed", Boolean.TRUE);
//				} else {
//					document.put("messageBody", Document.parse(jsonToPersist));
//				}
			}
			service.insert(document.toJson());
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Document checkSizeValues(String jsonToPersist) throws IOException {
		Document parse = Document.parse(jsonToPersist);
		return checkSizeValues(parse);
	}

	private Document checkSizeValues(Document parse) throws IOException {
		Set<String> keySet = parse.keySet();
		for (String key : keySet) {
			Object object = parse.get(key);
			if (object instanceof Document) {
				return checkSizeValues((Document) object);
			} else if (object instanceof Collection<?>) {
				Collection<?> collection = (Collection<?>) object;
				for (Iterator<?> i = collection.iterator(); i.hasNext();) {
					Object obj = (Object) i.next();
					if (obj instanceof Document) {
						return checkSizeValues((Document) obj);
					} else {
						if (sizeObjectGreaterThanMega(key, obj)) {
							parse.put(key, "File greater than 1M.");
						}
					}
				}
			} else {
				if (sizeObjectGreaterThanMega(key, object)) {
					parse.put(key, "File greater than 1M.");
				}
			}
		}
		return parse;
	}

	private boolean sizeObjectGreaterThanMega(String key, Object object) throws IOException {
		if (object != null && object instanceof String) {
			try (ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(byteArrayOutput);) {
				out.writeObject(object);
				out.flush();
				byte[] bytes = byteArrayOutput.toByteArray();
				if (bytes.length > 1048576) {
					return true;
				}
			}
		}
		return false;
	}

	public static byte[] compress(String str) {
		if (str == null || str.length() == 0) {
			return null;
		}
		try (ByteArrayOutputStream obj = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(obj)) {
			gzip.write(str.getBytes(StandardCharsets.UTF_8.name()));
			gzip.close();
			return obj.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String decompress(byte[] str) {
		if (str == null) {
			return null;
		}

		try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str))) {
			BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.UTF_8.name()));
			String outStr = "";
			String line;
			while ((line = bf.readLine()) != null) {
				outStr += line;
			}
			return outStr;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}