package fr.codeimpot.impotcible.model;

import java.util.List;

/**
 * Représentation d'un déclarant.
 * 
 * @author smonfort
 *
 */
public class Declarant {

	private Long id;
	private int dateNaissance;
	private int codePostal;
	private String situationFamiliale;
	private int nombreEnfants;
	private int netImposable;
	private int cluster;
	private int montantIR;
	private double distance;

	private List<CodeRevenu> codesRev;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(int dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public int getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(int codePostal) {
		this.codePostal = codePostal;
	}

	public String getSituationFamiliale() {
		return situationFamiliale;
	}

	public void setSituationFamiliale(String situationFamiliale) {
		this.situationFamiliale = situationFamiliale;
	}

	public int getNombreEnfants() {
		return nombreEnfants;
	}

	public void setNombreEnfants(int nombreEnfants) {
		this.nombreEnfants = nombreEnfants;
	}

	public int getNetImposable() {
		return netImposable;
	}

	public void setNetImposable(int netImposable) {
		this.netImposable = netImposable;
	}

	public List<CodeRevenu> getCodesRev() {
		return codesRev;
	}

	public void setCodesRev(List<CodeRevenu> codes) {
		this.codesRev = codes;
	}

	public int getCluster() {
		return cluster;
	}

	public void setCluster(int cluster) {
		this.cluster = cluster;
	}

	public int getMontantIR() {
		return montantIR;
	}

	public void setMontantIR(int montantIR) {
		this.montantIR = montantIR;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public String toString() {
		return "Declarant [id=" + id + ", dateNaissance=" + dateNaissance + ", codePostal=" + codePostal
				+ ", situationFamiliale=" + situationFamiliale + ", nombreEnfants=" + nombreEnfants + ", netImposable="
				+ netImposable + ", cluster=" + cluster + ", montantIR=" + montantIR + ", codesRev=" + codesRev + "]";
	}

}
