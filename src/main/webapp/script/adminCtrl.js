app.controller('adminCtlr', function($scope, $http) {
    $scope.setClusters = function() {
        $http.get("api/admin/cluster", {nb:100}).success(function(data, status) {
            alert('DONE');
        });
    };
});