package fr.codeimpot.impotcible.service.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.jdbc.core.RowMapper;

import fr.codeimpot.impotcible.model.CodeRevenu;
import fr.codeimpot.impotcible.model.Declarant;

public class DeclarantRowMapper implements RowMapper<Declarant> {

	public Declarant mapRow(ResultSet rs, int rowNum) throws SQLException {
		Declarant declarant = new Declarant();
		declarant.setId(rs.getLong("id"));
		declarant.setCluster(rs.getInt("cluster"));
		declarant.setDateNaissance(rs.getInt("date_naissance"));
		declarant.setCodePostal(rs.getInt("code_postal"));
		declarant.setSituationFamiliale(rs.getString("sit_fam"));
		declarant.setNombreEnfants(rs.getInt("nombre_enfants"));
		declarant.setNetImposable(rs.getInt("net_imposable"));
		declarant.setCluster(rs.getInt("cluster"));
		declarant.setMontantIR(rs.getInt("montant_ir"));
		List<CodeRevenu> codesRev = new ArrayList<CodeRevenu>();
		String codesRevBase = rs.getString("codes_revenu");

		if (StringUtils.isNotEmpty(codesRevBase)) {
			String[] revenus = StringUtils.split(codesRevBase, "#");
			for (int i = 0; i < revenus.length; i++) {
				String code = StringUtils.substring(revenus[i], 0, 3);
				String valeur = StringUtils.substring(revenus[i], 3, revenus[i].length());
				if (NumberUtils.isNumber(valeur)) {
					CodeRevenu codeRevenu = new CodeRevenu();
					codeRevenu.setCode(code);
					codeRevenu.setValeur(Integer.valueOf(valeur));
					codesRev.add(codeRevenu);
				}
			}
		}

		declarant.setCodesRev(codesRev);
		return declarant;
	}

}