module kriptolab.main {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens GUI;
    exports main;
}