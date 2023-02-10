module kriptotest{
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    //requires jcommander;
    requires jakarta.xml.bind;
    requires org.bytedeco.javacv;
    requires org.bytedeco.ffmpeg;
    requires lombok;
    exports main;
    exports sqlite;
    exports GUI;
    exports GUI.controllers;
    opens GUI.controllers to javafx.fxml;
}