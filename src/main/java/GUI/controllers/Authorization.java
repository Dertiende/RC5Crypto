package GUI.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Authorization {

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button loginButton;

	@FXML
	private TextField loginField;

	@FXML
	private PasswordField passField;

	@FXML
	void initialize() {
		loginButton.setOnAction(actionEvent -> {
			loginButton.getScene().getWindow().hide();
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/fxml/encryption.fxml"));

			try {
				loader.load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Parent root = loader.getRoot();
			Stage stage = new Stage();
			stage.setScene(new Scene(root));
			stage.setResizable(false);
			stage.showAndWait();
		});

	}

}

