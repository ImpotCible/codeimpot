package fr.codeimpot.impotcible.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;

import fr.codeimpot.impotcible.model.Declarant;
import fr.codeimpot.impotcible.model.ProchesReponse;
import fr.codeimpot.impotcible.service.DataMiningService;
import fr.codeimpot.impotcible.service.DeclarantService;

/**
 * Service REST utilisé par le FrontEnd #CodeImpots
 * 
 * @author smonfort
 *
 */
@Path("/declarant")
public class DeclarantRestService {

	@Autowired
	private DeclarantService declarantService;

	@Autowired
	private DataMiningService dataMiningService;

	/**
	 * Calcule l'IR pour le déclarant passé en paramètre et retourne les
	 * déclarants qui sont "proches" du déclarant passé en paramètre.
	 * 
	 * @param declarant
	 * @param nb
	 * @return
	 */
	@POST
	@Path("proches")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ProchesReponse getProches(Declarant declarant, @QueryParam("nb") Integer nb) {
		int nombre;
		if (nb == null) {
			nombre = 10;
		} else {
			nombre = nb;
		}
		// Calculer l'IR pour le déclarant passé en paramètre à l'aide de la
		// calculette
		declarantService.calculerIr(declarant);
		ProchesReponse reponse = new ProchesReponse();
		reponse.setDeclarant(declarant);
		// Récupérer les proches
		reponse.setProches(dataMiningService.findProches(declarant, nombre));

		return reponse;
	}

}