var app = angular.module('Main', ['ngRoute', 'ngResource']);
app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/start', {
        templateUrl: '/views/start.html',
        controller: 'StartCtrl'
    }).when('/test/:passCode', {
        templateUrl: '/views/test.html',
        controller: 'TestCtrl'
    }).when('/done/:passCode', {
        templateUrl: '/views/done.html',
        controller: 'DoneCtrl'
    }).otherwise({
        redirectTo: '/start'
    })
}]);

app.factory('UserData', function ($http) {
    var user = {user: {}};
    $http.get('/data/user').then(function (res) {
        angular.extend(user.user, res.data);
    });

    return user;
});

app.controller('StartCtrl', ['$scope', '$resource', '$http', '$location', '$window', 'UserData', function ($scope, $resource, $http, $location, $window, UserData) {
    var Tests = $resource('/data/tests');
    $scope.tests = Tests.query();
    $scope.user = UserData.user;

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
    function fetchQuestion() {
        $http.post('/data/current-question?passCode=' + passCode).then(function (response) {
            $scope.question = response.data;
            $scope.selected = {};
            $scope.secondsLeft = Math.ceil(response.data.questionData.msLeft / 1000);
            console.log(response.data);
        });
    }

    fetchQuestion();

    $scope.selectAnswer = function (answer) {
        if ($scope.question.questionData.multiAnswer) {
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

    $scope.answer = function () {
        var answers = [];
        for (var i in $scope.selected) {
            if ($scope.selected[i]) {
                answers.push(i);
            }
        }
        $http.post('/data/submit-answer', {passCode: passCode, answers: answers})
            .then(function (result) {
                if (result.data) {
                    $location.path('/done/' + passCode);
                } else {
                    fetchQuestion();
                }
            });
    };

    $interval(function () {
        $scope.secondsLeft -= 1;
    }, 1000);
}]);

app.controller('DoneCtrl', ['$scope', '$routeParams', '$http', function ($scope, $routeParams, $http) {
    var passCode = $routeParams.passCode;

    $http.get('/data/score?passCode=' + passCode)
        .then(function (response) {
            $scope.score = response.data;
        });
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
                var htmlText = text ? marked(text) : '';
                element.html(htmlText);
            });
        }
    };

});