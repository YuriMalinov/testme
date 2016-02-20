package ru.smarty.testme.model

class Views {
    class Detailed
    open class Public
    open class Admin : Public()
    class FullAdmin : Admin()
}