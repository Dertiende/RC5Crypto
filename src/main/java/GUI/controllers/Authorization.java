package GUI.controllers;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.keyGenerator;
import main.rc5Obj;
import sqlite.sqliteDB;

public class Authorization {
	private main.rc5Obj obj;
	private sqliteDB db;
	Timer timer = new Timer();

	@FXML
	private Tooltip tooltip;

	@FXML
	private ResourceBundle resources;

	@FXML
	private Label errorMessage;

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

		obj = new rc5Obj();
		db = new sqliteDB(obj);
		//waitSec(4);
		tooltip.setShowDelay(Duration.seconds(2));
		loginButton.setOnAction(actionEvent -> {
			try {
				if(isAuthLegal())
					login();
			} catch (SQLException | NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		});

	}
	private boolean isAuthLegal() throws SQLException, NoSuchAlgorithmException {
		main.keyGenerator keygen = new keyGenerator();
		obj.login = loginField.getText();
		obj.pass = passField.getText();
		if (db.isUserExist(obj.login)){
			if (db.isPassCorrect(obj.login,obj.pass)){
				return true;
			}
			else{
				errorMessage.setText("User exists. Wrong password.");
				errorMessage.setVisible(true);
				return false;
			}
		}
		else{
			if (!keygen.isPassRelevant(loginField.getText(), passField.getText())){
				errorMessage.setText("Password Must be 10+ symbols of Cyrillic,Latin, numbers and arithmetic operands. Mustn't consists only from login's characters.");
				errorMessage.setVisible(true);
				return false;
			}
			db.createUser(obj.login,obj.pass);
		}

		return true;
	}

	public void login(){

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
		Encryption encryption = loader.getController();
		encryption.setData(obj,db);
		loginButton.getScene().getWindow().hide();
		stage.setTitle("RC5Crypto");
		stage.getIcons().add(new Image("vec1.png"));
		stage.showAndWait();
	}


//	public void waitSec(int latency) {
//		TimerTask task;
//		task = new TimerTask() {
//		public void run() {
//			if (seconds < latency) {
//				System.out.println("Seconds = " + seconds);
//				seconds++;
//			} else {
//				timer.cancel();
//				System.out.println("Timer canceled");
//
//			}
//		}};
//		timer.schedule(task, 0, 1000);
//	}
}

