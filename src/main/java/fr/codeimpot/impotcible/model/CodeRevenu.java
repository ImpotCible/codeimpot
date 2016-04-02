package fr.codeimpot.impotcible.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Représentation d'un code revenu.
 * 
 * @author smonfort
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeRevenu {

	private String code;
	private int valeur;
	private String libelle;

	public String getCode() {
		return code;
	}

	@JsonProperty("k")
	public void setCode(String code) {
		this.code = code;
	}

	@JsonProperty("v")
	public int getValeur() {
		return valeur;
	}

	public void setValeur(int valeur) {
		this.valeur = valeur;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

}
