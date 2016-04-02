package fr.codeimpot.impotcible.model;

import java.util.List;

/**
 * Représentation de la réponse de l'appel de service pour récupérer les
 * proches.
 * 
 * @author smonfort
 *
 */
public class ProchesReponse {

	private Declarant declarant;
	private List<Declarant> proches;

	public Declarant getDeclarant() {
		return declarant;
	}

	public void setDeclarant(Declarant declarant) {
		this.declarant = declarant;
	}

	public List<Declarant> getProches() {
		return proches;
	}

	public void setProches(List<Declarant> proches) {
		this.proches = proches;
	}

}
