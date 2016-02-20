var app = angular.module('Admin', ['ngRoute', 'ngResource']);
app.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/users', {
        templateUrl: '/views/admin/users.html',
        controller: 'UsersCtrl'
    }).when('/test-passes', {
        templateUrl: '/views/admin/test-passes.html',
        controller: 'TestPassesCtrl'
    }).when('/test-pass/:id', {
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
    $scope.testPass = TestPass.get({id: $routeParams.id});
    $scope.selectedCriterias = {};
    $scope.selectedAnswerId = null;

    $scope.toggleCriteria = function (answer, criteriaIndex) {
        var criterias = $scope.selectedCriterias;
        if (criterias[answer.id] === undefined) {
            criterias[answer.id] = {};
        }

        criterias[answer.id][criteriaIndex] = !criterias[answer.id][criteriaIndex];

        var count = 0;
        for (var x in criterias[answer.id]) {
            if (criterias[answer.id][x]) {
                count++;
            }
        }
        answer.mark = answer.question.weight * count / answer.question.criteria.length;
    };

    $scope.toggleAnswer = function (answer) {
        if ($scope.selectedAnswerId === answer.id) {
            $scope.selectedAnswerId = null;
        } else {
            $scope.selectedAnswerId = answer.id;
        }
    };


    $scope.grade = function (answer) {
        if (answer.mark === null) {
            alert('Please select your mark');
            return;
        }

        QuestionAnswer.grade({id: answer.id, mark: answer.mark}, function (a) {
            angular.extend(answer, a);
            $scope.toggleAnswer(answer);
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