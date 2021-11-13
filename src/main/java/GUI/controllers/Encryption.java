package GUI.controllers;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

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

public class Encryption {

	private final ObservableList<String> bsize = FXCollections.observableArrayList("16","32");
	private final Desktop desktop = Desktop.getDesktop();
	private final SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 255, 1);
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
		changeModeButton.setOnAction(actionEvent -> {
			changeMode();
		});

		inputFileButton.setOnAction(actionEvent -> {
			selectInputFile();
		});

		outputFileButton.setOnAction(actionEvent -> {
			selectOutFile();
		});
		roundsSelect.valueProperty().addListener((this::handleSpin));


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

}