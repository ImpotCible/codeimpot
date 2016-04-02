package fr.codeimpot.impotcible.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fr.codeimpot.impotcible.config.DatabaseCredentials;
import fr.codeimpot.impotcible.model.Declarant;
import weka.clusterers.Clusterer;
import weka.clusterers.FilteredClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.unsupervised.attribute.Remove;

@Service
public class DataMiningService {

	private static final Logger logger = LoggerFactory.getLogger(DataMiningService.class);

	@Autowired
	private DeclarantService declarantService;

	private Clusterer clusterer;

	private Instances instances;

	@Value("${cluster.nb}")
	private int nombreClusters;

	/**
	 * Répartit les déclarants présents en base dans n clusters.
	 * 
	 * @param nbClusters
	 * @throws Exception
	 */
	public void calculerCluster(int nbClusters) throws Exception {
		buildSimpleKMeans(nbClusters);
		List<Declarant> declarants = new ArrayList<Declarant>();

		@SuppressWarnings("unchecked")
		Enumeration<Instance> enumeration = instances.enumerateInstances();
		while (enumeration.hasMoreElements()) {
			Instance declarant = enumeration.nextElement();
			int cluster = clusterer.clusterInstance(declarant);
			double id = declarant.value(0);
			Declarant dec = new Declarant();
			dec.setId((long) id);
			dec.setCluster(cluster);
			declarants.add(dec);
		}

		declarantService.updateCluster(declarants);
	}

	/**
	 * Retourne le cluster auquel appartient le déclarant
	 * 
	 * @param declarant
	 * @return
	 */
	public int classifierDeclarant(Declarant declarant) {
		// date_naissance, sit_fam, nombre_enfants, net_imposable
		Instance instance = new Instance(instances.lastInstance());
		instance.setDataset(instances);
		instance.setValue(0, 1);
		instance.setValue(1, declarant.getDateNaissance());
		instance.setValue(2, declarant.getSituationFamiliale());
		instance.setValue(3, declarant.getNombreEnfants());
		instance.setValue(4, declarant.getSalaire() + declarant.getSalaireConjoint());

		try {
			return clusterer.clusterInstance(instance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Retourne n déclarants qui appartiennent au même cluster que le déclarant
	 * passé en paramètre.
	 * 
	 * @param declarant
	 * @param nb
	 * @return
	 */
	public List<Declarant> findProches(Declarant declarant, int nb) {
		int cluster = classifierDeclarant(declarant);
		logger.info("Le déclarant appartient au cluster {}", cluster);
		List<Declarant> reponse = declarantService.getMembres(cluster, nb);
		miseAJourDistance(declarant, reponse);
		return reponse;
	}

	private void miseAJourDistance(Declarant utilisateur, List<Declarant> declarants) {
		Instance instance = creerInstance(utilisateur);

		long distanceMax = 0;
		for (Declarant dec : declarants) {
			Instance decInstance = creerInstance(dec);
			long distance = Math.round(1000 * new EuclideanDistance(instances).distance(instance, decInstance));
			if (distance > distanceMax) {
				distanceMax = distance;
			}
			dec.setDistance(new Double(distance));
		}

		// Normalisation des distances
		for (Declarant dec : declarants) {
			dec.setDistance(new Long(Math.round(100 * dec.getDistance() / distanceMax)).doubleValue());
		}

	}

	private Instance creerInstance(Declarant utilisateur) {
		System.out.println(utilisateur);
		Instance instance = (Instance) instances.lastInstance().copy();
		instance.setValue(0, 0);
		instance.setValue(1, utilisateur.getDateNaissance());
		instance.setValue(2, utilisateur.getSituationFamiliale());
		instance.setValue(3, utilisateur.getNombreEnfants());
		double salaires = 0;
		if (utilisateur.getSalaires() != null) {
			salaires = utilisateur.getSalaires();
		} else if (utilisateur.getSalaire() != null || utilisateur.getSalaireConjoint() != null) {
			salaires = utilisateur.getSalaire() + utilisateur.getSalaire();
		}
		instance.setValue(4, salaires);
		return instance;
	}

	/**
	 * Construit le clusterer SimpleKMean Weka qui permettra de clusteriser les
	 * déclarants présents en base.
	 * 
	 * @param nbCluster
	 * @throws Exception
	 */
	protected void buildSimpleKMeans(int nbCluster) throws Exception {

		FilteredClusterer fclr = new FilteredClusterer();
		SimpleKMeans clusterAlg = new SimpleKMeans();
		clusterAlg.setMaxIterations(10);
		clusterAlg.setNumClusters(nbCluster);

		DatabaseCredentials cred = DatabaseCredentials.getCredentials();

		InstanceQuery query = new InstanceQuery();
		query.setDatabaseURL(cred.getConnectionString());
		query.setUsername(cred.getUsername());
		query.setPassword(cred.getPassword());
		query.setQuery("select id, date_naissance, sit_fam, nombre_enfants, salaires from declarants");

		instances = query.retrieveInstances();

		String[] options = new String[2];
		options[0] = "-R"; // "range"
		options[1] = "1"; // Ignorer l'id du déclarant
		Remove remove = new Remove();
		remove.setOptions(options);
		remove.setInputFormat(instances);
		fclr.setFilter(remove);
		fclr.setClusterer(clusterAlg);

		clusterer = fclr;

		clusterAlg.getDistanceFunction();

		clusterer.buildClusterer(instances);
	}

}
