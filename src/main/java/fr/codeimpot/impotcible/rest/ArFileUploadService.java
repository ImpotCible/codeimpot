package fr.codeimpot.impotcible.rest;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import fr.codeimpot.impotcible.service.PdfArReader;


@Path("/file")
@Produces(MediaType.APPLICATION_JSON)
public class ArFileUploadService {

	
	private PdfArReader reader;
	
	
	@POST
	@Path("/arpdf")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Map<String, String> postFile(@Multipart( type = "application/*", required = false) Attachment file){
		System.out.println("Yaooooo "+file);
		try {
			File fic  =File.createTempFile("upload", "arpdf");
			file.transferTo(fic);
			return reader.convertARToKeyMap(fic);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return new HashMap<>();
	}


	public PdfArReader getReader() {
		return reader;
	}


	public void setReader(PdfArReader reader) {
		this.reader = reader;
	}
}
