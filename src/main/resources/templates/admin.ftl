<html ng-app="Admin">
<head>
    <title>Test me! - Admin</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/css/bootstrap.css">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/admin.css">
    <link rel="stylesheet" href="/external/highlightjs/styles/default.min.css">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="/external/html5shiv/dist/html5shiv.min.js"></script>
    <script src="/external/respond/dest/respond.min.js"></script>
    <![endif]-->
</head>
<body>
<nav class="navbar navbar-default" ng-controller="NavBarCtrl">
    <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="/">TestMe</a>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav" ng-cloak>
                <li><a href="/">Мои тесты</a></li>
                <li><a href="/admin#all-tests">Все тесты</a></li>
                <li><a href="/admin#test-passes">Прохождения</a></li>
                <li><a href="/admin#users">Пользователи</a></li>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="#">{{ user.userName }}</a></li>
            </ul>
        </div><!-- /.navbar-collapse -->
    </div>
</nav>
<div class="container">
    <div ng-view></div>
</div>
<script src="/external/jquery/dist/jquery.min.js"></script>
<script src="/external/bootstrap/dist/js/bootstrap.min.js"></script>
<script src="/external/angular/angular.min.js"></script>
<script src="/external/angular-route/angular-route.min.js"></script>
<script src="/external/angular-resource/angular-resource.min.js"></script>
<script src="/external/marked/lib/marked.js"></script>
<script src="/external/highlightjs/highlight.pack.min.js"></script>
<script src="/js/admin.js"></script>
</body>
</html>