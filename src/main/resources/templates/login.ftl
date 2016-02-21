<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
<head>
    <title>TestMe - Login</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/css/bootstrap.css">
    <link rel="stylesheet" href="/css/login.css">
</head>
<body>
<nav class="navbar navbar-inverse" ng-controller="NavBarCtrl">
    <div class="container">
        <!-- Brand and toggle get grouped for better mobile display -->
        <div class="navbar-header">
            <a class="navbar-brand" href="/">TestMe</a>
        </div>
    </div>
</nav>

<div class="wrapper">
    <form class="form-signin" action="/login" method="post">
        <h2 class="form-signin-heading">Назовись!</h2>
        <#if springMacroRequestContext.queryString?? && springMacroRequestContext.queryString == "error">
            <div class="alert alert-danger">
                Что-то не то, не совпадает пользователь.
            </div>
        </#if>
        <#if springMacroRequestContext.queryString?? && springMacroRequestContext.queryString == "logout">
            <div class="alert alert-warning">
                Ну всё, ушёл. <a href="/login">Войти снова?</a>
            </div>
        </#if>
        <input type="text" class="form-control" name="username" placeholder="логин" required="" autofocus=""/>
        <input type="password" class="form-control" name="password" placeholder="пароль" required=""/>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Войти</button>
    </form>
</div>
</body>
</html>