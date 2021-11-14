package GUI.controllers;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.google.common.primitives.Longs;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import main.rc5;
import main.utils;
import sqlite.sqliteDB;

import static main.utils.vector;

public class Encryption {

	private main.rc5Obj obj;
	private sqliteDB db;
	private main.keyGenerator keyGen = new main.keyGenerator();
	private final ObservableList<String> bsize = FXCollections.observableArrayList("16","32");
	private final Desktop desktop = Desktop.getDesktop();
	private final SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 255, 1);
	private String fileName = "^[\\w\\-. ]+$";
	Pattern patern = Pattern.compile(fileName);
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Label modeField;

	@FXML
	private ComboBox<String> blockSizeSelect;

	@FXML
	private ToggleButton changeModeButton;

	@FXML
	private Button inputFileButton;

	@FXML
	private TextField inputFileField;

	@FXML
	private TextArea logField;

	@FXML
	private Button outputFileButton;

	@FXML
	private TextField outputFileField;

	@FXML
	private Spinner<Integer> roundsSelect;

	@FXML
	private Button startButton;

	@FXML
	void initialize() {
		outputFileField.setText(System.getProperty("user.dir")+"\\out.enc");
		blockSizeSelect.setItems(bsize);
		blockSizeSelect.setValue("16");
		roundsSelect.setValueFactory(valueFactory);
		startButton.setOnAction(actionEvent -> {
			try {
				start();
			} catch (NoSuchAlgorithmException | SQLException | IOException e) {
				e.printStackTrace();
			}
		});
		changeModeButton.setOnAction(actionEvent -> changeMode());
		inputFileField.setOnAction(actionEvent -> isInputFileCorrect(inputFileField.getText()));
		inputFileButton.setOnAction(actionEvent -> {
			selectInputFile();
			isInputFileCorrect(inputFileField.getText());
		});
		inputFileField.textProperty().addListener((observableValue, s, t1) -> isInputFileCorrect(t1));
		outputFileField.textProperty().addListener((observableValue, s, t1) -> isOutputFileCorrect(t1));
		outputFileField.setOnAction(actionEvent -> isOutputFileCorrect(outputFileField.getText()));
		outputFileButton.setOnAction(actionEvent -> {
			selectOutFile();
			isOutputFileCorrect(outputFileField.getText());
		});
		roundsSelect.valueProperty().addListener((this::handleSpin));


	}

	private boolean isOutputFileCorrect(String file){
		String check = file.substring(0,file.lastIndexOf("\\"));
		String fname = file.substring(file.lastIndexOf("\\")+1);
		try{
		if (!(Files.isDirectory(Path.of(check))) || fname.length()== 0){
			outputFileField.setStyle("-fx-border-color: red; -fx-border-radius: 3");
			return false;
		}
		else{
			outputFileField.setStyle("");
			return true;
		}
		}
		catch (Exception e){
			outputFileField.setStyle("-fx-border-color: red; -fx-border-radius: 3");
			return false;
		}
	}

	private boolean isInputFileCorrect(String file){
		if (!(new File(file).isFile())){
			inputFileField.setStyle("-fx-border-color: red; -fx-border-radius: 3");
			return false;
		}
		else{
			inputFileField.setStyle("");
			return true;
		}
	}

	private void start() throws NoSuchAlgorithmException, SQLException, IOException {
		if (!isInputFileCorrect(inputFileField.getText()) ||
				    !isOutputFileCorrect(outputFileField.getText())){
			return;
		}
		else{
			startButton.setDisable(true);
			setObj();
			if (obj.mode.compareToIgnoreCase("encryption") == 0){
				obj.key =  keyGen.getKey();
				rc5.getEncryption(this);
				utils.getEncryption(this);
				sqliteDB.getEncryption(this);
				rc5 rc5 = new rc5(obj);
				Runnable r = ()->{

					try {
						rc5.encryptFile(obj.input,obj.output);
						startButton.setDisable(false);
					} catch (IOException | SQLException e) {
						e.printStackTrace();
					}
				};
				Thread rc5Thread = new Thread(r,"rc5Thread");
				rc5Thread.start();
				//rc5 rc5 = new rc5(obj);

				logField.setWrapText(true);
				logField.setText("Encryption started");

			}
			else {

				utils.getDecodeInfo(obj,obj.input);
				sqliteDB.getRC5Data(obj.hash);
				sqliteDB.getEncryption(this);
				rc5.getEncryption(this);
				utils.getEncryption(this);
				rc5 rc5 = new rc5(obj);
				Runnable r = ()->{

					try {
						rc5.decryptFile(obj.input,obj.output);
						startButton.setDisable(false);
					} catch (IOException e) {
						e.printStackTrace();
					}
				};
				Thread rc5Thread = new Thread(r,"rc5Thread");
				rc5Thread.start();


				logField.setWrapText(true);
				logField.setText("Decryption started");

			}

		}
	}

	public void print(String text){
		logField.setText(logField.getText()+"\n"+text);
	}


	private void setObj() throws IOException {

		obj.mode = modeField.getText();
		obj.bsize = blockSizeSelect.getValue();
		obj.rounds = roundsSelect.getValue().toString();
		obj.input = inputFileField.getText();
		obj.output = outputFileField.getText();
		byte[] vectorB = utils.vector(Integer.parseInt(obj.bsize)/4);
		if (vectorB.length<8){
			obj.vector = String.valueOf(utils.fromBytes(vectorB[0],vectorB[1],vectorB[2],vectorB[3]));
		}
		else{
			obj.vector = String.valueOf(Longs.fromByteArray(vectorB));
		}
		obj.size = String.valueOf(Files.size(Paths.get(obj.input)));
	}

	private void changeMode(){
		if (changeModeButton.isSelected()){
			modeField.setText("Decryption");
			blockSizeSelect.setDisable(true);
			roundsSelect.setDisable(true);
		}
		else{
			modeField.setText("Encryption");
			blockSizeSelect.setDisable(false);
			roundsSelect.setDisable(false);
		}
	}

	private void selectInputFile(){
		final FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(inputFileButton.getScene().getWindow());
		if (file != null) {
			inputFileField.setText(file.getAbsolutePath());
		}
	}

	private void selectOutFile(){
		final FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(outputFileButton.getScene().getWindow());
		if (file != null) {
			outputFileField.setText(file.getAbsolutePath());
		}
	}

	private void handleSpin(ObservableValue<?> observableValue, Number oldValue, Number newValue) {
		try {
			if (newValue == null) {
				roundsSelect.getValueFactory().setValue((int)oldValue);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void setData(main.rc5Obj obj, sqliteDB db){
		this.obj = obj;
		this.db = db;
	}

}