module kriptolab.main {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires jcommander;


    opens GUI;
    exports main;
}