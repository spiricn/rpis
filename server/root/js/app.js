var app = angular.module('rpis', ['ngMaterial', 'material.svgAssetsCache', 'ngPicky'])

app.controller('NavBarCtrl', function($scope, $mdSidenav) {
    $scope.showMobileMainHeader = true;
    $scope.openSideNavPanel = function() {
        $mdSidenav('left').open();
    };
    $scope.closeSideNavPanel = function() {
        $mdSidenav('left').close();
    };
})

app.controller('StatusCtrl', function($scope, $http, $timeout) {
    // Function to get the data
    $scope.getData = function(){
      $http.get('rest/status/temperature').
          success(function(data) {
              $scope.temperature = data + " C";
      });
      $http.get('rest/status/upTime').
          success(function(data) {
              $scope.upTime = data;
      });
      $http.get('rest/status/memoryUsage').
          success(function(data) {
              $scope.memoryUsage = 
              data.free + 'B / ' + data.total + 'B ( ' + (data.free/data.total)*100 + '% )'
      });
      $http.get('rest/status/ipAddress').
          success(function(data) {
              $scope.ipAddress = data;
      });
      $http.get('rest/status/cpuUsage').
          success(function(data) {
              $scope.cpuUsage = data + '%';
      });
      $http.get('rest/status/platform').
          success(function(data) {
              $scope.platform = data;
      });
      $http.get('rest/status/devices').
          success(function(data) {
            var res = '';
            for(var i=0; i<data.devices.length; i++){
                res += data.devices[i] + '\n';
            }
            $scope.devices = res;
      });
    };
    
    $scope.getData();
  
    // Function to replicate setInterval using $timeout service.
    $scope.intervalFunction = function(){
        $timeout(function() {
            $scope.getData();
            $scope.intervalFunction();
        }, 3000)
    };
    
    // Kick off the interval
    $scope.intervalFunction();
})

