package fr.codeimpot.impotcible.rest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;

import fr.codeimpot.impotcible.service.DataMiningService;
import fr.codeimpot.impotcible.service.DeclarantService;

/**
 * Service REST d'administration de l'application #CodeImpots
 * 
 * @author smonfort
 *
 */
@Path("/admin")
public class AdministrationRestService {

	@Autowired
	private DataMiningService dataMiningService;

	@Autowired
	private DeclarantService declarantService;

	/**
	 * Reconstruit le cluster des déclarants
	 * 
	 * @param nb
	 *            nombre de clusters à construire.
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/cluster")
	@Produces(MediaType.TEXT_PLAIN)
	public Response cluster(@QueryParam("nb") Integer nb) throws Exception {
		int nombre;
		if (nb == null) {
			nombre = 10;
		} else {
			nombre = nb;
		}
		dataMiningService.calculerCluster(nombre);
		return Response.status(200).entity("OK : " + nombre + " clusters").build();
	}

	/**
	 * Réalise un appel à la calculette pour l'ensemble des déclarants stockés
	 * en base.
	 * 
	 * @return
	 * @throws Exception
	 */
	@GET
	@Path("/calculIr")
	@Produces(MediaType.TEXT_PLAIN)
	public Response calculIr() throws Exception {
		declarantService.calculerIrDeclarants();
		return Response.status(200).entity("Montant IR mis à jour pour tous les déclarants").build();
	}

	@GET
	@Path("/codes")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, String> listCodes() throws Exception {

		Map<String, String> reponse = new HashMap<String, String>();

		Properties prop = new Properties();
		prop.load(getClass().getResourceAsStream("/config/reference_code.properties"));

		Enumeration<Object> keys = prop.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			reponse.put(key, prop.getProperty(key));
		}

		return reponse;
	}

}