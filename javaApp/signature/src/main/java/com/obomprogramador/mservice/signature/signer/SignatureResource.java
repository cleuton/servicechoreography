package com.obomprogramador.mservice.signature.signer;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;

@Path("/signature")
public class SignatureResource {
	private String keystorePath;
	private String alias;
	private String keystorePassword;
	
    @POST
    @Timed
    @Consumes(MediaType.APPLICATION_JSON)
    public Response verify(SampleDocument document) {
    	boolean resultado = false;
    	String mensagem = "";
    	String status = "";
    	int httpStatus = 200;
    	try {
			resultado = VerifySignature.verify(document.getHexSignature(), document.getTexto(),
					this.keystorePath, this.alias, this.keystorePassword);
	    	if (!resultado) {
	    		status = "fail";
	    		mensagem = "assinatura incorreta";
	    	}
	    	else {
	    		status = "success";
	    		mensagem = "assinatura ok!";    		
	    	}
		} catch (Exception e) {
			status = "error";
			mensagem = e.getClass().getName() + ": " + e.getLocalizedMessage();
			httpStatus = 500;
		} 

    	String saidaJSON = "{ \"status\": \"" + status + "\","
    					 + "\"data\": {"
    					 + "\"mensagem\": \"" + mensagem + "\"}}"; 
        return Response.status(httpStatus).entity(saidaJSON).build();
    }

	public SignatureResource(String keystorePath, String alias,
			String keystorePassword) {
		super();
		this.keystorePath = keystorePath;
		this.alias = alias;
		this.keystorePassword = keystorePassword;
	}	
    
    
}
