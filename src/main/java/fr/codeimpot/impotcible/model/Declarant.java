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
	private Integer dateNaissance;
	private Integer codePostal;
	private String situationFamiliale;
	private Integer nombreEnfants;
	private Integer salaire;
	private Integer salaireConjoint;
	private Integer salaires;
	private Integer cluster;

	private Integer montantIR;
	private Double distance;

	private List<CodeRevenu> codesRev;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(Integer dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public Integer getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(Integer codePostal) {
		this.codePostal = codePostal;
	}

	public String getSituationFamiliale() {
		return situationFamiliale;
	}

	public void setSituationFamiliale(String situationFamiliale) {
		this.situationFamiliale = situationFamiliale;
	}

	public Integer getNombreEnfants() {
		return nombreEnfants;
	}

	public void setNombreEnfants(Integer nombreEnfants) {
		this.nombreEnfants = nombreEnfants;
	}

	public Integer getSalaire() {
		return salaire;
	}

	public void setSalaire(Integer salaire) {
		this.salaire = salaire;
	}

	public Integer getSalaireConjoint() {
		return salaireConjoint;
	}

	public void setSalaireConjoint(Integer salaireConjoint) {
		this.salaireConjoint = salaireConjoint;
	}

	public Integer getSalaires() {
		return salaires;
	}

	public void setSalaires(Integer salaires) {
		this.salaires = salaires;
	}

	public Integer getCluster() {
		return cluster;
	}

	public void setCluster(Integer cluster) {
		this.cluster = cluster;
	}

	public Integer getMontantIR() {
		return montantIR;
	}

	public void setMontantIR(Integer montantIR) {
		this.montantIR = montantIR;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public List<CodeRevenu> getCodesRev() {
		return codesRev;
	}

	public void setCodesRev(List<CodeRevenu> codesRev) {
		this.codesRev = codesRev;
	}

}
