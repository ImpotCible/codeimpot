var app = angular.module('codeImpots', ['ngFileUpload', 'ngResource', 'ngGrid', 'ui.bootstrap']);

app.controller('monCtrl', function($scope, $http) {
	$scope.page = "declarant";
	
	$scope.setPage = function(page) {
		if(page == '') page = 'declarant';
		$scope.page = page;
	}
	
	$scope.ready = 'true';
	

});