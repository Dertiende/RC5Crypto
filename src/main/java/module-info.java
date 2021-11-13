module kriptotest{
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires jcommander;
    requires com.google.common;
    exports main;
    exports GUI;
    exports GUI.controllers;
    opens GUI.controllers to javafx.fxml;
}