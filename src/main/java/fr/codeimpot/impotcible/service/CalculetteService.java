package fr.codeimpot.impotcible.service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.codeimpot.impotcible.model.CodeRevenu;
import fr.codeimpot.impotcible.model.Declarant;

/**
 * Service qui permet d'appeler l'API REST qui encapsule la calculette.
 * 
 * @author smonfort
 *
 */
@Service
public class CalculetteService {

	private static final String CALCULATE_RESULTS = "calculate_results";

	private static final String IRN = "IRN";

	private static final Logger logger = LoggerFactory.getLogger(CalculetteService.class);

	@Value("${calculette.endpoint}")
	private String endpoint;

	@Value("${calculette.path}")
	private String path;

	/**
	 * Appelle la calculette pour l'ensemble des déclarants passés en paramètre.
	 * L'attribut montantIr du déclarant est mis à jour avec le retour de la
	 * calculette.
	 * 
	 * @param declarants
	 */
	public void calculerIr(List<Declarant> declarants) {
		for (Declarant dec : declarants) {
			dec.setMontantIR(getMontantIR(dec));
		}
	}

	private int getMontantIR(Declarant declarant) {
		HttpClient client = new HttpClient();
		int montant = 0;
		JSONObject saisies = new JSONObject();
		saisies.put("V_ANREV", 2014); // Année de revenus
		saisies.put("V_0DA", declarant.getDateNaissance()); // Age du déclarant

		saisies.put("0CF", declarant.getNombreEnfants()); // Nombre d'enfants
															// mineurs ou
															// handicapés

		switch (declarant.getSituationFamiliale()) {
		case "C":
			saisies.put("0AC", 1);
			break;
		case "M":
			saisies.put("0AM", 1);
			break;
		case "D":
			saisies.put("0AD", 1);
			break;
		case "V":
			saisies.put("0AV", 1);
			break;
		default:
			break;
		}

		if (declarant.getCodesRev() != null) {
			for (CodeRevenu codeRev : declarant.getCodesRev()) {
				logger.info("Paramètre calculette : {} = {}", codeRev.getCode(), codeRev.getValeur());
				saisies.put(codeRev.getCode(), codeRev.getValeur());
			}
		}

		logger.info("Saisies = {}", saisies.toJSONString());

		String url = endpoint + path + "?calculee=" + IRN + "&saisies=" + URLEncoder.encode(saisies.toJSONString());

		logger.info("URL = {}", url);

		HttpMethod method = new GetMethod(url);

		try {
			client.executeMethod(method);
			String reponse = method.getResponseBodyAsString();

			logger.info("Reponse = {}", reponse);

			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(reponse);
			JSONObject resultats = (JSONObject) jsonObject.get(CALCULATE_RESULTS);
			Long montantLong = (Long) resultats.get(IRN);
			if (montantLong != null) {
				montant = Math.toIntExact(montantLong);
			}
			logger.info("Montant IR {}", montant);

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}

		return montant;
	}

}
