var app = angular.module('Main', ['ngRoute', 'ngResource']);
app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
    when('/start', {
        templateUrl: '/views/start.html',
        controller: 'StartCtrl'
    }).
    when('/test/:passCode', {
        templateUrl: '/views/test.html',
        controller: 'TestCtrl'
    }).
    when('/done/:passCode', {
        templateUrl: '/views/done.html',
        controller: 'PassCtrl'
    }).
    otherwise({
        redirectTo: '/start'
    })
}]);

app.controller('StartCtrl', ['$scope', '$resource', '$http', '$location', '$window', function ($scope, $resource, $http, $location, $window) {
    var Tests = $resource('/data/tests');
    $scope.tests = Tests.query();

    $scope.show = {};
    $scope.data = {username: ""};

    $scope.toggleApply = function (test) {
        $scope.show[test.code] = !$scope.show[test.code];
    };

    $scope.apply = function (test) {
        if ($scope.data.username.trim() == "") {
            $window.alert("Username is required!");
            return;
        }

        $http.post('/data/apply', {
            testCode: test.code,
            userName: $scope.data.username
        }).then(function (result) {
            $location.path('/test/' + result.data.value)
        });
    };
}]);

app.controller('TestCtrl', ['$scope', '$routeParams', '$location', '$http', '$interval', function ($scope, $routeParams, $location, $http, $interval) {
    var passCode = $routeParams.passCode;

    $scope.question = {};
    $scope.selected = {};

    $scope.secondsLeft = 0;

    $http.post('/data/current-question?passCode=' + passCode).then(function (response) {
        $scope.question = response.data;
        $scope.selected = {};
        $scope.secondsLeft = Math.ceil(response.data.questionData.msLeft / 1000);
        console.log(response.data);
    });

    $scope.selectAnswer = function (answer) {
        if ($scope.question.multiVariant) {
            $scope.selected[answer.index] = !$scope.selected[answer.index];
        } else if ($scope.selected[answer.index]) {
            $scope.selected = {};
        } else {
            $scope.selected = {};
            $scope.selected[answer.index] = true;
        }
        return false;
    };

    $scope.isSelected = function (answer) {
        return $scope.selected[answer.index];
    };

    $interval(function() {
        $scope.secondsLeft -= 1;
    }, 1000);
}]);

app.directive('markdown', function () {
    return {
        restrict: 'E',
        scope: {
            'text': '='
        },
        link: function (scope, element) {
            marked.setOptions({
                highlight: function (code) {
                    return code ? hljs.highlightAuto(code).value : code;
                }
            });
            scope.$watch('text', function (text) {
                var htmlText = marked(text);
                element.html(htmlText);
            });
        }
    };

});