app.controller('declarantCtlr', function($scope, $http, $timeout, Upload) {

	$scope.declarant = {
		dateNaissance : 1970,
		situationFamiliale : "M",
		nombreEnfants: 2,
		netImposable : 20000
	};
	/*
	'1AJ' : 20000,
	'0DA' : 1970,
	'0CF' : 1,*/
	
	// Par défaut, afficher uniquement les différences dans la fenêtre modale
	$scope.differenceSeulement = true;

	$scope.proches = null;

	$scope.file = null;

	$scope.graphe = 'forcelayout';
	
	$scope.declarationType = 'textuelle';

	// Map codeRev -> libellé
	$scope.referenceCodes = null;
	
	// Individu à comparer avec moi
	$scope.individuCompare = null;
	
	// Code de revenu pouvant être comparer
	$scope.codeCompare = ['dateNaissance','situationFamiliale','nombreEnfants','netImposable'];

	$scope.$watch('file', function () {
		if($scope.file != null) {
			$scope.upload($scope.file);
		}
	});
	
	$scope.setIndividuCompare = function(d) {
		$scope.individuCompare = d;

		// Création d'une map code -> {moi, lui, identique}		
		var comparaison = {};
		// Remplir la map avec les valeurs du déclarant
		for(var i in $scope.declarant.codesRev) {
			var codeRev = $scope.declarant.codesRev[i];
			console.log("Ajout du code revenu pour 'moi'" + angular.toJson(codeRev));
			comparaison[codeRev.code] = {'code' : codeRev.code, 'moi':codeRev.valeur, 'lui':null, identique:false};
		}
		// Compléter la map avec la valeur de l'individu comparé
		for(var i in $scope.individuCompare.codesRev) {
			var codeRev = $scope.individuCompare.codesRev;
			if (codeRev.code in comparaison) {
				// Mise à jour du code dans la map
				console.log("Mise à jour du code revenu " + angular.toJson(codeRev));
				codeExistant = comparaison[codeRev.code];				
				comparaison[codeRev.code] = {'code' : codeRev.code, 'moi':codeExistant.valeur, 'lui':codeRev.valeur, identique:(codeRev.valeur == codeExistant.valeur)};
			}
			else {
				console.log("Ajout du code revenu pour 'lui'" + angular.toJson(codeRev));
				// Ajout du code dans la map
				comparaison[codeRev.code] = {'code' : codeRev.code, 'moi': null, 'lui':codeRev.valeur, identique:false};
			}
		}
		
		$scope.comparaison = comparaison;
		
		if($scope.individuCompare != null) {
			$('.js-comparaison-coderev').modal('show');
		}
	}

	$http.get("api/admin/codes").success(function(data, status) {
		$scope.referenceCodes = data;
	});

	$scope.getProches = function() {
    	var data = angular.toJson($scope.declarant);
		$http.post("api/declarant/proches", data).success(function(data, status) {
			$scope.declarant = data.declarant;
			$scope.proches = data.proches;
		});
	};

	$scope.upload = function(file) {
		if (!file.$error) {
			Upload.upload({
				url: 'api/file/arpdf',
				data: {
					file: file  
				}
			}).then(function (resp) {
				$timeout(function() {
					$scope.declarant.codesRev = new Array();
					for(var code in resp.data) {
						$scope.declarant.codesRev.push({
							'code'    : code,
							'libelle' : $scope.referenceCodes[code],
							'valeur'  : resp.data[code]
						});
					}
				});
			}, null, function (evt) {
				var progressPercentage = parseInt(100.0 *
						evt.loaded / evt.total);
				$scope.log = 'progress: ' + progressPercentage + 
				'% ' + evt.config.data.file.name + '\n' + 
				$scope.log;
			});
		}
	}

	$scope.setGraphe = function(graph) {
		$scope.graphe = graph;
	}
	
	$scope.setDeclarationType = function(type) {
		$scope.declarationType = type;
	}

	// Prend en entrée le contenu de la zone de texte et génère un tableau de codeRev
	$scope.updateCodeRevenus = function(codes) {
		console.debug(codes);
		var codesRev = codes.split("#");
		$scope.declarant.codesRev =  new Array();
		for (var i in codesRev) {
			codeRev = codesRev[i];
			console.debug(codeRev);			
			var code = codeRev.substring(0, 3);
			var valeur = codeRev.substring(3, codeRev.length);
			var label = $scope.referenceCodes[code];			
			var codeRevenu = {"code" : code, "valeur" : valeur, "libelle" : label}
			console.log(codeRevenu)
			$scope.declarant.codesRev.push(codeRevenu);
		}
	};    

});
