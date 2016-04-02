package fr.codeimpot.impotcible.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Classe utilitaire qui permet de récupérer les identifiants de connexion à la
 * base de données dans un environnement Cloud Foundry.
 * 
 * @author smonfort
 *
 */
public class DatabaseCredentials {

	public static DatabaseCredentials getCredentials() {
		String envServices = System.getenv("VCAP_SERVICES");

		if (envServices == null) {
			String envServicesFile = System.getenv("VCAP_SERVICES_FILE");
			if (envServicesFile == null) {
				throw new RuntimeException("La variable d'environnement VCAP_SERVICES_FILE doit être définie");
			}
			File fichierLocal = new File(envServicesFile);
			if (!fichierLocal.exists()) {
				throw new RuntimeException("Impossible de trouver le fichier " + envServicesFile);
			}
			try {
				envServices = FileUtils.readFileToString(fichierLocal);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		JSONParser parser = new JSONParser();

		Object obj;
		try {
			obj = parser.parse(envServices);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray vcapArray = (JSONArray) jsonObject.get("elephantsql");
		JSONObject vcap = (JSONObject) vcapArray.get(0);
		JSONObject credentials = (JSONObject) vcap.get("credentials");
		String uriPg = credentials.get("uri").toString();

		DatabaseCredentials cred = new DatabaseCredentials();
		cred.setConnectionString("jdbc:postgresql://" + StringUtils.substringAfter(uriPg, "@"));
		cred.setUsername(StringUtils.substringBetween(uriPg, "//", ":"));
		cred.setPassword(StringUtils.substringBetween(StringUtils.substringAfter(uriPg, "//"), ":", "@"));

		return cred;
	}

	public static DriverManagerDataSource createDataSource() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.postgresql.Driver");
		DatabaseCredentials cred = getCredentials();
		ds.setUrl(cred.getConnectionString());
		ds.setUsername(cred.getUsername());
		ds.setPassword(cred.getPassword());
		return ds;
	}

	private String username;
	private String password;
	private String connectionString;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	@Override
	public String toString() {
		return "Credentials [username=" + username + ", password=" + password + ", connectionString=" + connectionString
				+ "]";
	}

}
