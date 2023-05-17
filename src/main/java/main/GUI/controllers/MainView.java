package main.GUI.controllers;


import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import main.utils.Constants;
import main.GUI.service.AlertService;
import main.utils.PassUtils;
import main.model.ProgressListener;
import main.domain.RC5Data;
import main.model.RequestInfo;
import main.config.ControllersConfiguration;
import main.repository.RC5Repository;
import main.service.AuthService;
import main.service.ConverterService;
import main.service.CryptoService;
import main.utils.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static main.utils.Constants.CONFIRM_HEADER;
import static main.utils.Constants.CONFIRM_TITLE;
import static main.utils.Constants.CONVERTER_ICON;
import static main.utils.Constants.CRYPTO_ICON;
import static main.utils.Constants.ENCRYPTION;
import static main.utils.Constants.INVALID_INPUT;


public class MainView {

	@Autowired
	private RC5Repository rc5Repository;
	@Autowired
	private AuthService authService;

	@Autowired
	private CryptoService cryptoService;
	@Autowired
	private ConverterService converterService;
	@Autowired
	private AlertService alertService;

	@Autowired
	@Qualifier("settingsView")
	private ControllersConfiguration.ViewHolder settingsView;
	private Stage userSettingStage;

	private final PassUtils passUtils = new PassUtils();
	private final ObservableList<Integer> bsize = FXCollections.observableArrayList(16, 32);
	private final ObservableList<String> CRYPTO_MODES = FXCollections.observableArrayList(Constants.CRYPTO_MODES);
	private final SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 255, 1);
	private final ObservableList<String> CONVERT_MODES = FXCollections.observableArrayList(Constants.CONVERT_MODES);
	private final ObservableList<String> AUDIO_FORMATS = FXCollections.observableArrayList(Constants.AUDIO_FORMATS);
	private final ObservableList<String> VIDEO_FORMATS = FXCollections.observableArrayList(Constants.VIDEO_FORMATS);

	@FXML private ResourceBundle resources;
	@FXML private URL location;
	@FXML private Label modeField;
	@FXML private ComboBox<Integer> blockSizeSelect;
	@FXML private ComboBox<String> encryptMode;
	@FXML private Button cvrtStartBtn;
	@FXML private Button encryptInputFBtn;
	@FXML private TextField encryptInputFField;
	@FXML private Button encryptOutputFBtn;
	@FXML private TextField encryptOutputFField;
	@FXML private Spinner<Integer> roundsSelect;
	@FXML private Button encryptStartBtn;
	@FXML private AnchorPane encryptionPage;
	@FXML private AnchorPane converterPage;
	@FXML private SVGPath userSettingImg;
	@FXML private SVGPath cryptoIcon;
	@FXML private SVGPath converterIcon;
	@FXML private ProgressBar encryptProgressBar;
	@FXML private ProgressBar cvrtProgressBar;
	@FXML private Label encryptPBLabel;
	@FXML private Label cvrtPBLabel;
	@FXML private TextField cvrOutFileName;
	@FXML private TextField cvrInputFField;
	@FXML private TextField cvrOutputFField;
	@FXML private Button cvrOutputFBtn;
	@FXML private Button cvrInputFBtn;
	@FXML private ChoiceBox<String> converterModeToggle;
	@FXML private ChoiceBox<String> converterFormatToggle;

	String input = "D:\\Alex\\Downloads\\Programs\\Adobe.Media.Encoder.2023.v23.0.0.57.exe";
	String output = System.getProperty("user.dir")+"\\out.enc";



	@PostConstruct
	void init() {
		encryptInputFField.setText(input);
		encryptOutputFField.setText(output);
		blockSizeSelect.setItems(bsize);
		encryptMode.setItems(CRYPTO_MODES);
		encryptMode.getSelectionModel().selectFirst();
		converterModeToggle.setItems(CONVERT_MODES);
		converterFormatToggle.valueProperty().addListener((observable, oldValue, newValue) -> {
			cvrtStartBtn.setDisable(newValue == null || newValue.equals(""));
			changeConverterOutput(cvrOutFileName.getText(), cvrOutFileName.getText());
		});
		converterModeToggle.valueProperty().addListener((observable, oldValue, newValue) ->
            converterFormatToggle.setItems(newValue.equals(Constants.VIDEO) ? VIDEO_FORMATS : AUDIO_FORMATS));
		converterModeToggle.getSelectionModel().selectFirst();
		encryptPBLabel.setText("");
		blockSizeSelect.setValue(16);
		roundsSelect.setValueFactory(valueFactory);
		cvrOutputFField.setText(System.getProperty("user.dir"));
		cvrOutputFField.setDisable(true);
		cvrOutputFBtn.setOnAction(event -> {
			final DirectoryChooser chooser = new DirectoryChooser();
			File file = chooser.showDialog(cvrOutputFBtn.getScene().getWindow());
			if (file != null) {
				cvrOutputFField.setText(file.getAbsolutePath());
			}
		});
		cvrInputFBtn.setOnAction(event -> selectInputFile(cvrInputFField));
		cvrOutputFField.textProperty().addListener((obs, oldValue, newValue) -> checkOutputFolderCorrect(newValue));
		cvrInputFField.textProperty().addListener((obs, oldValue, newValue) -> isInputFileCorrect(cvrInputFField));
		cvrOutFileName.textProperty().addListener((obs, oldValue, newValue) -> changeConverterOutput(oldValue, newValue));
		encryptStartBtn.setOnAction(actionEvent -> cryptoStart());
		cvrtStartBtn.setOnAction(event -> convertStart());

//		changeModeButton.setOnAction(actionEvent -> changeMode());
		encryptInputFField.setOnAction(actionEvent -> isInputFileCorrect(encryptInputFField));
		encryptInputFBtn.setOnAction(actionEvent -> selectInputFile(encryptInputFField));
		encryptInputFField.textProperty().addListener((obs, oldValue, newValue) -> isInputFileCorrect(encryptInputFField));
		encryptOutputFField.textProperty().addListener((obs, oldValue, newValue) -> isOutputFileCorrect(encryptOutputFField));
		encryptOutputFField.setOnAction(actionEvent -> isOutputFileCorrect(encryptOutputFField));
		encryptOutputFBtn.setOnAction(actionEvent -> selectOutFile());
		roundsSelect.valueProperty().addListener((this::handleSpin));

		cryptoIcon.setContent(CRYPTO_ICON);
		cryptoIcon.setOnMouseClicked(value -> activateEncryptMode());
		converterIcon.setContent(CONVERTER_ICON);
		converterIcon.setOnMouseClicked(value -> activateConvertMode());
//		userSettingImg.setContent(utils.USER_ICON_PATH);
		userSettingImg.setOnMouseClicked(action -> openUserSettings());
		activateEncryptMode();
	}

	private void changeConverterOutput(String oldValue, String newValue) {
		String path = cvrOutputFField.getText().lastIndexOf(oldValue) == -1
			? cvrOutputFField.getText()
			: cvrOutputFField.getText().substring(0, cvrOutputFField.getText().lastIndexOf(oldValue));
		if (!path.endsWith("\\")) {
			path += "\\";
		}
		final String format = converterFormatToggle.getValue() != null ? converterFormatToggle.getValue() : "";
		cvrOutputFField.setText(path + (!newValue.equals("") ? newValue + "." + format : newValue));
	}

	@FXML
	void initialize() {}

	void openUserSettings() {
		if (userSettingStage == null) {
			userSettingStage = new Stage();
		}
		Scene userScene = settingsView.getView()
          .getScene() != null ? settingsView.getView().getScene() : new Scene(settingsView.getView());
		userSettingStage.setScene(userScene);
		userSettingStage.setResizable(false);
		((UserSettingsView) settingsView.getController()).reInitFields();
		userSettingStage.showAndWait();
		userSettingStage.toFront();
	};

	void switchMode(AnchorPane offPage, AnchorPane onPage, SVGPath offIcon, SVGPath onIcon) {
		offPage.setManaged(false);
		offPage.setVisible(false);
		onPage.setManaged(true);
		onPage.setVisible(true);
		onIcon.setFill(Paint.valueOf("#20cc60"));
		offIcon.setFill(Paint.valueOf("#e77c05"));
	}

	void activateEncryptMode() {
		switchMode(converterPage, encryptionPage, converterIcon, cryptoIcon);
	}

	void activateConvertMode() {
		switchMode(encryptionPage, converterPage, cryptoIcon, converterIcon);
	}

	private void checkOutputFolderCorrect(String folder){
		if (!cvrOutFileName.getText().equals("")){
			folder = folder.substring(0, folder.lastIndexOf(cvrOutFileName.getText()));
		}
		try {
			cvrOutputFField.setStyle(Files.isDirectory(Paths.get(folder)) ?  "" : INVALID_INPUT);
		} catch (Exception e){
			cvrOutputFField.setStyle(INVALID_INPUT);
		}
	}

	private boolean isOutputFileCorrect(TextField field){
		String check = field.getText().substring(0,field.getText().lastIndexOf("\\"));
		String fname = field.getText().substring(field.getText().lastIndexOf("\\")+1);
		try {
			if (!(Files.isDirectory(Paths.get(check))) || fname.length()== 0){
				field.setStyle(INVALID_INPUT);
				return false;
			} else{
				field.setStyle("");
				return true;
			}
		} catch (Exception e){
			field.setStyle(INVALID_INPUT);
			return false;
		}
	}

	private boolean isInputFileCorrect(TextField field){
		if (!(new File(field.getText()).isFile())){
			encryptInputFField.setStyle(INVALID_INPUT);
			return false;
		}
		else{
			encryptInputFField.setStyle("");
			return true;
		}
	}

	private void convertStart() {
		if (!isInputFileCorrect(cvrInputFField) || !isOutputFileCorrect(cvrOutputFField)){
			return;
		}
		ConvertTask task = new ConvertTask();
		cvrtProgressBar.progressProperty().bind(task.progressProperty());
		cvrtProgressBar.progressProperty().addListener(
				(observable, oldValue, newValue) -> cvrtPBLabel.setText(Math.round(newValue.floatValue() * 100) + "%"));
		new Thread(task).start();
	}
	private void cryptoStart() {
		if (!isInputFileCorrect(encryptInputFField) || !isOutputFileCorrect(encryptOutputFField)){
			return;
		}
		encryptStartBtn.setDisable(true);
		EncodeTask task = new EncodeTask();
		encryptProgressBar.progressProperty().bind(task.progressProperty());
		encryptProgressBar.progressProperty().addListener(
			(observable, oldValue, newValue) -> encryptPBLabel.setText(Math.round(newValue.floatValue() * 100) + "%"));
		new Thread(task).start();
	}

	private void selectInputFile(TextField target){
		final FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showOpenDialog(target.getScene().getWindow());
		if (file != null) {
			target.setText(file.getAbsolutePath());
		}
		isInputFileCorrect(target);
	}

	private void selectOutFile(){
		final FileChooser fileChooser = new FileChooser();
		File file = fileChooser.showSaveDialog(encryptOutputFBtn.getScene().getWindow());
		if (file != null) {
			encryptOutputFField.setText(file.getAbsolutePath());
		}
		isOutputFileCorrect(encryptOutputFField);
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


	@RequiredArgsConstructor
	public class EncodeTask extends ProgressListener {
		@Override
		protected Long call() throws Exception {
			RC5Data rc5Data;
			if (encryptMode.getValue().equals(ENCRYPTION)) {
				rc5Data = RC5Data.builder()
		          .size(Files.size(Paths.get(encryptInputFField.getText())))
		          .rounds(roundsSelect.getValue())
		          .bsize(blockSizeSelect.getValue())
		          .user(authService.getCurrentUser())
		          .vector(CryptoUtils.fromBytes(CryptoUtils.vector(blockSizeSelect.getValue() / 4)))
		          .key(passUtils.getKey())
		          .build();
			} else {
				rc5Data = rc5Repository.findRC5DataByUserAndHash(authService.getCurrentUser(), CryptoUtils.getDecodeInfo(encryptInputFField.getText()))
						          .orElse(RC5Data.builder().build()); // TODO: fix
			}
			RequestInfo info = RequestInfo.builder()
				.inputFilePath(encryptInputFField.getText())
				.outputFilePath(encryptOutputFField.getText())
				.progressListener(this)
				.mode(encryptMode.getValue())
				.requestData(rc5Data)
				.build();
			cryptoService.doCryptography(info);
			encryptStartBtn.setDisable(false);
			return 0L;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			alertService.showSuccessfulAlert(AlertService.AlertData.builder()

				.outputPath(String.valueOf(Path.of(encryptOutputFField.getText()).getParent()))
				.type(Alert.AlertType.INFORMATION)
				.header(CONFIRM_HEADER)
				.title(CONFIRM_TITLE)
				.context("")
				.build());
		}

		public void update(Long newValue, Long size) {
			updateProgress(size - newValue, size);
		}
	}

	@RequiredArgsConstructor
	public class ConvertTask extends ProgressListener {
		@Override
		protected Long call() {
			cvrtStartBtn.setDisable(true);
			RequestInfo info = RequestInfo.builder()
               .inputFilePath(cvrInputFField.getText())
               .outputFilePath(cvrOutputFField.getText())
               .progressListener(this)
               .mode(converterModeToggle.getValue())
               .requestData(converterFormatToggle.getValue())
               .build();
			converterService.doConversion(info);
			cvrtStartBtn.setDisable(false);
			return 0L;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			alertService.showSuccessfulAlert(AlertService.AlertData.builder()
				.outputPath(String.valueOf(Path.of(cvrOutputFField.getText()).getParent()))
				.type(Alert.AlertType.INFORMATION)
				.header(CONFIRM_HEADER)
				.title(CONFIRM_TITLE)
				.context("")
				.build());
		}

		public void update(Long newValue, Long size) {
			updateProgress(newValue, size);
		}
	}
}