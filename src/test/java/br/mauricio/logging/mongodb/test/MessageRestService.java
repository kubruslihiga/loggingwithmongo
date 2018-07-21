package br.mauricio.logging.mongodb.test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.mauricio.logging.mongodb.JSONLogAnnotation;

@Path("/message")
public class MessageRestService {

	@GET
	@Path("/{param}")
	@JSONLogAnnotation
	public Response printMessage(@PathParam("param") String msg) {
		String result = "Restful example : " + msg;
		return Response.status(200).entity(result).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@JSONLogAnnotation
	public Response printPutMessage(String json) {
	//public Response printPutMessage(MessageBean bean) {
		String result = "{\"returnMessage\":\"" + json + "\"}";
		return Response.status(200).entity(result).build();
	}
}