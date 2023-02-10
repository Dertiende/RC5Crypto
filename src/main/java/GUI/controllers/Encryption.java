package GUI.controllers;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import main.Longs;
import main.rc5;
import main.rc5Obj;
import main.utils;
import sqlite.sqliteDB;

public class Encryption {

	private main.rc5Obj obj;
	private sqliteDB db;
	private main.keyGenerator keyGen = new main.keyGenerator();
	private final ObservableList<String> bsize = FXCollections.observableArrayList("16","32");
	private final ObservableList<String> modes = FXCollections.observableArrayList("Encryption", "Decryption");
	private final Desktop desktop = Desktop.getDesktop();
	private final SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 255, 1);
	@FXML private ResourceBundle resources;
	@FXML private URL location;
	@FXML private Label modeField;
	@FXML private ComboBox<String> blockSizeSelect;
	@FXML private ComboBox<String> encryptMode;
	@FXML private ToggleButton changeModeButton;
	@FXML private Button encryptInputFBtn;
	@FXML private TextField encryptInputFField;
	@FXML private TextArea logField;
	@FXML private Button encryptOutputFBtn;
	@FXML private TextField encryptOutputFField;
	@FXML private Spinner<Integer> roundsSelect;
	@FXML private Button startButton;
	@FXML private Button encryptStartBtn;
	@FXML private AnchorPane encryptionPage;
	@FXML private AnchorPane converterPage;
	@FXML private ImageView encryptionToggle;
	@FXML private ImageView converterToggle;
	@FXML private ProgressBar encryptProgressBar;
	@FXML private Label encryptPBLabel;
	@FXML private TextField cvrOutFileName;
	@FXML private TextField cvrInputFField;
	@FXML private TextField cvrOutputFField;
	@FXML private Button cvrOutputFBtn;
	@FXML private Button cvrInputFBtn;

	public class EncodeTask extends Task<Long> {

		private final rc5Obj obj;
		private final boolean isEncode;

		public EncodeTask(rc5Obj obj, boolean isEncode) {
			this.obj = obj;
			this.isEncode = isEncode;
		}

		@Override
		protected Long call() throws Exception {
			updateMessage("    Processing... ");
			rc5 rc5 = new rc5(obj, this);
			if (isEncode) {
				rc5.encryptFile(obj.input, obj.output);
			} else {
				rc5.decryptFile(obj.input, obj.output);
			}
			startButton.setDisable(false);
			updateMessage("    Done.  ");
			return 0L;
		}

		public void update(Long newValue, Long size) {
			updateProgress(size - newValue, size);
		}
	}

	@FXML
	void initialize() {
		encryptOutputFField.setText(System.getProperty("user.dir")+"\\out.enc");
		blockSizeSelect.setItems(bsize);
		encryptMode.setItems(modes);
		encryptPBLabel.setText("");
		blockSizeSelect.setValue("16");
		roundsSelect.setValueFactory(valueFactory);
		cvrOutputFField.setDisable(true);
		cvrOutputFBtn.setOnAction(event -> {
			final DirectoryChooser chooser = new DirectoryChooser();
			File file = chooser.showDialog(cvrOutputFBtn.getScene().getWindow());
			if (file != null) {
				cvrOutputFField.setText(file.getAbsolutePath());
			}
		});
		cvrInputFBtn.setOnAction(event -> {
			selectInputFile();
			isInputFileCorrect(cvrInputFField);
		});
		cvrOutputFField.textProperty().addListener(((observable, oldValue, newValue) -> isOutputFolderCorrect(newValue)));
		cvrInputFField.textProperty().addListener((observableValue, s, t1) -> isInputFileCorrect(cvrInputFField));
		cvrOutFileName.textProperty().addListener((observableValue, s, t1) -> {
			String path = cvrOutputFField.getText().lastIndexOf(s) == -1
              ? cvrOutputFField.getText()
			  : cvrOutputFField.getText().substring(0, cvrOutputFField.getText().lastIndexOf(s));
			if (!path.endsWith("\\")) {
				path += "\\";
			}
			cvrOutputFField.setText(path + (!t1.equals("") ? t1 + ".mp4" : t1));
		});
		encryptStartBtn.setOnAction(actionEvent -> {
			try {
				cryptoStart();
			} catch (NoSuchAlgorithmException | SQLException | IOException e) {
				e.printStackTrace();
			}
		});

//		changeModeButton.setOnAction(actionEvent -> changeMode());
		encryptInputFField.setOnAction(actionEvent -> isInputFileCorrect(encryptInputFField));
		encryptInputFBtn.setOnAction(actionEvent -> {
			selectInputFile();
			isInputFileCorrect(encryptInputFField);
		});
		encryptInputFField.textProperty().addListener((observableValue, s, t1) -> isInputFileCorrect(encryptInputFField));
		encryptOutputFField.textProperty().addListener((observableValue, s, t1) -> isOutputFileCorrect(t1));
		encryptOutputFField.setOnAction(actionEvent -> isOutputFileCorrect(encryptOutputFField.getText()));
		encryptOutputFBtn.setOnAction(actionEvent -> {
			selectOutFile();
			isOutputFileCorrect(encryptOutputFField.getText());
		});
		roundsSelect.valueProperty().addListener((this::handleSpin));
		activateEncryptMode();
	}

	@FXML
	void activateEncryptMode() {
		encryptionPage.setManaged(true);
		encryptionPage.setVisible(true);
		converterPage.setManaged(false);
		converterPage.setVisible(false);
		InputStream encrypt = this.getClass().getResourceAsStream("/images/encrypt.png");
		if (encrypt != null) {
			encryptionToggle.setImage(new Image(encrypt));
		}
		InputStream convert = this.getClass().getResourceAsStream("/images/convertDisabled.png");
		if (convert != null) {
			converterToggle.setImage(new Image(convert));
		}
	}

	@FXML
	void activateConvertMode() {
		converterPage.setManaged(true);
		converterPage.setVisible(true);
		encryptionPage.setManaged(false);
		encryptionPage.setVisible(false);
		InputStream encrypt = this.getClass().getResourceAsStream("/images/encryptDisabled.png");
		if (encrypt != null) {
			encryptionToggle.setImage(new Image(encrypt));
		}
		InputStream convert = this.getClass().getResourceAsStream("/images/convert.png");
		if (convert != null) {
			converterToggle.setImage(new Image(convert));
		}
	}

	private boolean isOutputFolderCorrect(String folder){
		if (!cvrOutFileName.getText().equals("")){
			folder = folder.substring(0, folder.lastIndexOf(cvrOutFileName.getText()));
		}
		try {
			if (!(Files.isDirectory(Path.of(folder)))){
				cvrOutputFField.setStyle("-fx-border-color: red; -fx-border-radius: 3");
				return false;
			}
			else{
				cvrOutputFField.setStyle("");
				return true;
			}
		}
		catch (Exception e){
			cvrOutputFField.setStyle("-fx-border-color: red; -fx-border-radius: 3");
			return false;
		}
	}

	private boolean isOutputFileCorrect(String file){
		String check = file.substring(0,file.lastIndexOf("\\"));
		String fname = file.substring(file.lastIndexOf("\\")+1);
		try{
		if (!(Files.isDirectory(Path.of(check))) || fname.length()== 0){
			encryptOutputFField.setStyle("-fx-border-color: red; -fx-border-radius: 3");
			return false;
		}
		else{
			encryptOutputFField.setStyle("");
			return true;
		}
		}
		catch (Exception e){
			encryptOutputFField.setStyle("-fx-border-color: red; -fx-border-radius: 3");
			return false;
		}
	}

	private boolean isInputFileCorrect(TextField field){
		if (!(new File(field.getText()).isFile())){
			encryptInputFField.setStyle("-fx-border-color: red; -fx-border-radius: 3");
			return false;
		}
		else{
			encryptInputFField.setStyle("");
			return true;
		}
	}

	private void convertStart() {
		if (!isInputFileCorrect(encryptInputFField) || !isOutputFileCorrect(encryptOutputFField.getText())){
			return;
		}
		startButton.setDisable(true);
		File inputFile = new File(encryptInputFField.getText());
		File outputFile = new File(encryptOutputFField.getText());
	}
	private void cryptoStart() throws NoSuchAlgorithmException, SQLException, IOException {
		if (!isInputFileCorrect(encryptInputFField) || !isOutputFileCorrect(encryptOutputFField.getText())){
			return;
		}
		startButton.setDisable(true);
		setObj();
		if (obj.mode.compareToIgnoreCase("encryption") == 0){
			obj.key =  keyGen.getKey();
			rc5.getEncryption(this);
			utils.getEncryption(this);
			sqliteDB.getEncryption(this);
			EncodeTask task = new EncodeTask(obj, true);
			encryptProgressBar.progressProperty().bind(task.progressProperty());
			encryptProgressBar.progressProperty().addListener((observable, oldValue, newValue) -> {
				encryptPBLabel.setText(Math.round(newValue.floatValue() * 100) + "%");
			});
			new Thread(task).start();
			logField.setWrapText(true);
			logField.setText("Encryption started");

		}
		else {
			utils.getDecodeInfo(obj,obj.input);
			sqliteDB.getRC5Data(obj.hash);
			sqliteDB.getEncryption(this);
			rc5.getEncryption(this);
			utils.getEncryption(this);
			EncodeTask task = new EncodeTask(obj, false);
			encryptProgressBar.progressProperty().bind(task.progressProperty());
			new Thread(task).start();
			logField.setWrapText(true);
			logField.setText("Decryption started");

		}
	}

	public void print(String text){
		logField.setText(logField.getText()+"\n"+text);
	}


	private void setObj() throws IOException {

		obj.mode = encryptMode.getValue();
		obj.bsize = blockSizeSelect.getValue();
		obj.rounds = roundsSelect.getValue().toString();
		obj.input = encryptInputFField.getText();
		obj.output = encryptOutputFField.getText();
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
		File file = fileChooser.showOpenDialog(encryptInputFBtn.getScene().getWindow());
		if (file != null) {
			encryptInputFField.setText(file.getAbsolutePath());
		}
	}

	private void selectOutFile(){
		final FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(encryptOutputFBtn.getScene().getWindow());
		if (file != null) {
			encryptOutputFField.setText(file.getAbsolutePath());
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