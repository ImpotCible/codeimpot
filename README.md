# Application #CodeImpôts

## Compilation des fichiers css avec gulp

	npm install --global gulp-cli
	npm install --global gulp
	npm install --global gulp-sass

Puis, à la racine de ce projet, récupérer les dépendences comme ceci :
	
	npm link gulp
	npm link gulp-sass

Enfin lancer la commande suivante dans un invité de commande :

	gulp

## Lancement de l'application en local

La variable d'environnement `VCAP_SERVICES_FILE` doit être positionnée sur la JVM de Tomcat. Cette variable doit donner le chemin vers le fichier de configuration `dev.json` 

	VCAP_SERVICES_FILE = ${workspace_loc}\codeimpots\src\main\resources\config\dev.json

## Déploiement sur BlueMix

TODO depuis Eclipse 

# Base de données

TODO description

# Services REST

## Administration

Reconstruire le cluster

	HTTP GET http://codeimpots.mybluemix.net/api/admin/cluster?nb=100

Appel calculette pour tous les déclarants référencés en base

	HTTP GET http://codeimpots.mybluemix.net/api/admin/calculIr


## Services à destination du Front-End

Récupérer les individus les plus proches

	HTTP POST http://codeimpots.mybluemix.net/api/declarant/proches

Exemple de payload :

	{"dateNaissance":1975,"codePostal":75017,"situationFamiliale":"M","nombreEnfants":"3","netImposable":"15000"}

## Membres de l'équipe

Yann GLORIAU
Stephane MONFORT
Christophe DE MINGUINE
Vireya NIHM
Jason BOURLARD
Pierre BOURDU
Eric GERMAN
Tiffany HAUMEY
Teo ROBERJOT
Benjamin JEAN