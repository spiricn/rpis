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

