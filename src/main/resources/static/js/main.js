var app = angular.module('Main', ['ngRoute', 'ngResource']);
app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
    when('/start', {
        templateUrl: '/views/start.html',
        controller: 'StartCtrl'
    }).
    when('/apply/:testCode', {
        templateUrl: '/views/apply.html',
        controller: 'ApplyCtrl'
    }).
    when('/test/:passId', {
        templateUrl: '/views/test.html',
        controller: 'TestCtrl'
    }).
    when('/done/:passId', {
        templateUrl: '/views/donw.html',
        controller: 'PassCtrl'
    }).
    otherwise({
        redirectTo: '/start'
    })
}]);

app.controller('StartCtrl', ['$scope', '$resource', function ($scope, $resource) {
    var Tests = $resource('/data/tests');
    $scope.tests = Tests.query();
    $scope.apply = function (test) {

    }
}]);

app.directive('markdown', function () {
    return {
        restrict: 'E',
        scope: {
            'text': '='
        },
        link: function (scope, element) {
            marked.setOptions({
               highlight: function(code) {
                   return hljs.highlightAuto(code).value;
               }
            });
            scope.$watch('text', function (text) {
                var htmlText = marked(text);
                element.html(htmlText);
            });
        }
    };

});