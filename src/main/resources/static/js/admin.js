var app = angular.module('Admin', ['ngRoute', 'ngResource']);
app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/users', {
        templateUrl: '/views/admin/users.html',
        controller: 'UsersCtrl'
    }).when('/test-passes', {
        templateUrl: '/views/admin/test-passes.html',
        controller: 'TestPassesCtrl'
    }).when('/test-pass/:passCode', {
        templateUrl: '/views/admin/test-pass.html',
        controller: 'TestPassCtrl'
    }).otherwise({
        redirectTo: '/test-passes'
    })
}]);

app.factory('UserData', function ($http) {
    var user = {user: {}};
    $http.get('/data/user').then(function (res) {
        angular.extend(user.user, res.data);
    });

    return user;
});

app.factory('TestPass', function ($resource) {
    return $resource('/admin/test-pass/:id', {id: '@id'});
});

app.factory('QuestionAnswer', function ($resource) {
    return $resource('/admin/question-answer/:id', {id: '@id'}, {
        grade: {method: 'POST', params: {grade: true}}
    });
});

app.controller('TestPassesCtrl', ['$scope', '$location', 'TestPass', function ($scope, $location, TestPass) {
    $scope.testPasses = TestPass.query();

    $scope.openTestPass = function (testPass) {
        $location.path('/test-pass/' + testPass.id);
    };
}]);

app.controller('TestPassCtrl', ['$scope', '$routeParams', 'TestPass', 'QuestionAnswer', function ($scope, $routeParams, TestPass, QuestionAnswer) {
    $scope.testPass = TestPass.get($routeParams.id);
    $scope.marks = {};
    $scope.selectedCriterias = {};

    $scope.toggleCriteria = function (answer, criteria) {
        var criterias = $scope.selectedCriterias;
        if (criterias[answer.id] === undefined) {
            criterias[answer.id] = {};
        }

        criterias[answer.id][criteria.id] = !criterias[answer.id][criteria.id];

        var count = 0;
        for (var x in criterias[answer.id]) {
            if (criterias[answer.id][x]) {
                count++;
            }
        }

        $scope.marks[answer.id] = answer.question.weight * count / answer.question.criteria.length;
    };


    $scope.grade = function (answer) {
        if ($scope.marks[answer.id] === undefined) {
            alert('Please select your mark');
            return;
        }

        QuestionAnswer.grade({id: answer.id, mark: $scope.marks[answer.id]}, function(answer) {
            $scope.marks[answer.id] = answer.mark;
        });
    };
}]);

app.controller('NavBarCtrl', ['$scope', 'UserData', function ($scope, UserData) {
    $scope.user = UserData.user;
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