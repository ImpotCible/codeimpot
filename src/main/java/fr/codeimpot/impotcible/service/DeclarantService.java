package fr.codeimpot.impotcible.service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import fr.codeimpot.impotcible.model.Declarant;
import fr.codeimpot.impotcible.service.mapper.DeclarantRowMapper;

@Service
public class DeclarantService {

	private static final Logger logger = LoggerFactory.getLogger(DeclarantService.class);

	@Autowired
	private DataSource dataSource;

	@Autowired
	private CalculetteService calculetteService;

	/**
	 * Récupérer n membres d'un cluster.
	 * 
	 * @param clusterId
	 * @param max
	 * @return
	 */
	public List<Declarant> getMembres(int clusterId, int max) {
		String sql = "SELECT * FROM declarants where cluster = ? limit ?";
		List<Declarant> declarants = new JdbcTemplate(dataSource).query(sql, new DeclarantRowMapper(),
				new Object[] { clusterId, max });
		return declarants;
	}

	/**
	 * Récupérer tous les déclarants présents en base.
	 * 
	 * @return
	 */
	public List<Declarant> getAllDeclarants() {
		String sql = "SELECT * FROM declarants";
		List<Declarant> declarants = new JdbcTemplate(dataSource).query(sql, new DeclarantRowMapper());
		return declarants;

	}

	/**
	 * Calculer le montant de l'IR pour un déclarant.
	 * 
	 * @param declarant
	 */
	public void calculerIr(Declarant declarant) {
		calculetteService.calculerIr(Collections.singletonList(declarant));
	}

	/**
	 * Calculer le montant de l'IR pour tous les déclarants sauvés en base.
	 */
	public void calculerIrDeclarants() {
		List<Declarant> declarants = getAllDeclarants();
		calculetteService.calculerIr(declarants);
		updateMontantIr(declarants);
	}

	/**
	 * Mettre à jour le montant de l'IR en base de données pour les déclarants
	 * passés en paramètre.
	 * 
	 * @param declarants
	 */
	private void updateMontantIr(final List<Declarant> declarants) {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "update declarants set montant_ir = ? where id = ?";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Declarant declarant = declarants.get(i);
				ps.setInt(1, declarant.getMontantIR());
				ps.setLong(2, declarant.getId());
				logger.debug("Declarant = {}, montantIr = {}", declarant.getId(), declarant.getMontantIR());
			}

			public int getBatchSize() {
				return declarants.size();
			}
		});
	}

	/**
	 * Mettre à jour en base de données le numéro de cluster pour les individus
	 * passés en paramètre.
	 * 
	 * @param declarants
	 */
	public void updateCluster(final List<Declarant> declarants) {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		String sql = "update declarants set cluster = ? where id = ?";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Declarant declarant = declarants.get(i);
				ps.setInt(1, declarant.getCluster());
				ps.setLong(2, declarant.getId());
				logger.debug("Declarant = {}, cluster = {}", declarant.getId(), declarant.getCluster());
			}

			public int getBatchSize() {
				return declarants.size();
			}
		});
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
