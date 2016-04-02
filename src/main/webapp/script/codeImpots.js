app.controller('declarantCtlr', function($scope, $http, $timeout, Upload) {

	var updateViewCodes = function() {
		if($scope.declarationType != 'codes') {
			$scope.codes = "";
			for(var i in $scope.declarant.codesRev) {
				if($scope.declarant.codesRev[i].valeur != null && $scope.declarant.codesRev[i].valeur != "") {
					$scope.codes = $scope.codes + $scope.declarant.codesRev[i].code + $scope.declarant.codesRev[i].valeur + '#';
				}
			}
		};
	}
	
	$scope.declarant = {
		dateNaissance : 1970,
		situationFamiliale : "M",
		nombreEnfants: 2,
		netImposable : 20000,
		netImposableConjoint : 25000,
		codesRev : []
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
	
	$scope.declarationType = 'textuel';

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
	
	$scope.$watch('declarant.dateNaissance', function() {
		$scope.setCodeRevenu('ODA', $scope.declarant.dateNaissance);
	});
	
	$scope.$watch('declarant.situationFamiliale', function() {
        var C = "";
        var D = "";
        var M = "";
        
        switch($scope.declarant.situationFamiliale) {
        case 'C':
        	C = 1;
        	break;
        case 'D':
        	C = 1;
        	break;
        case 'M':
        	C = 1;
        	break;
        }
        
		$scope.setCodeRevenu('0AC', C);
		$scope.setCodeRevenu('0AD', D);
		$scope.setCodeRevenu('0AM', M);
	});
	
	$scope.$watch('declarant.nombreEnfants', function() {
		$scope.setCodeRevenu('OCF', $scope.declarant.nombreEnfants);
	});
	
	$scope.$watch('declarant.netImposable', function() {
		$scope.setCodeRevenu('1AJ', $scope.declarant.netImposable);
	});

	$scope.$watch('declarant.netImposableConjoint', function() {
		$scope.setCodeRevenu('1BJ', $scope.declarant.netImposableConjoint);
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
					$scope.declarant.codesRev = [];
					for(var code in resp.data) {
						$scope.setCodeRevenu(code, resp.data[code]);
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
	
	/**
	 * Ajoute un code de revenu utilisateur. Si il existe déjà ce
	 * code, alors il est en premier lieu supprimé puis ajouté
	 * avec la nouvelle valeur.
	 * 
	 * @param Le code à ajouter
	 * @param La valeur associé au code à ajouter.
	 */
	$scope.setCodeRevenu = function(code, valeur) {
		for(var i in $scope.declarant.codesRev) {
			if(code == $scope.declarant.codesRev[i].code) {
				$scope.declarant.codesRev.splice(i, 1);
				break;
			}
		}
		
		$scope.declarant.codesRev.push({
			"code" : code, 
			"valeur" : valeur, 
			"libelle" : ($scope.referenceCodes != null && $scope.referenceCodes[code] !== undefined) 
							? $scope.referenceCodes[code] 
							: ""
		});
		
		updateViewCodes();
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
			$scope.setCodeRevenu(code, valeur);
		}
	};    

});
