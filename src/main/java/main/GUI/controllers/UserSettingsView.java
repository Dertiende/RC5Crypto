package main.GUI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import main.domain.User;
import main.repository.UserRepository;
import main.service.AuthService;
import main.utils.PassUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Arrays;

import static main.utils.Constants.DIFFERENT_PASSES;
import static main.utils.Constants.INVALID_INPUT;
import static main.utils.Constants.OUT_OF_ATTEMPTS;
import static main.utils.Constants.PASS_DONT_MATCH;
import static main.utils.Constants.WEAK_PASS;

public class UserSettingsView {

	@Autowired
	UserRepository userRepository;

	@Autowired
	AuthService authService;
	User user;
	private int attempts = 3;
	public PasswordField newPassF;
	public PasswordField newPassF2;
	public Label errorField;
	public Button saveButton;
	public PasswordField oldPassF;

	@FXML
	void initialize() {
	}

	@PostConstruct
	void init() {
		toggleSaveButton();
		saveButton.setOnMouseClicked(event -> updatePassword());
		newPassF2.textProperty().addListener(observable -> {
			if (attempts > 0) {
				checkNewPass();
				toggleSaveButton();
			}
		});
	}

	void reInitFields() {
		oldPassF.setText("");
		newPassF.setText("");
		newPassF2.setText("");
		if (attempts > 0) {
			errorField.setText("");
		}
	}

	private void checkNewPass() {
		if (newPassF.getText().equals(newPassF2.getText())) {
			processErrors(errorField, "", "", newPassF2);
		} else {
			processErrors(errorField, DIFFERENT_PASSES, INVALID_INPUT, newPassF2);
		}
		toggleSaveButton();
	}

	private void toggleSaveButton() {
		saveButton.setDisable(!errorField.getText().equals("") || oldPassF.getText().equals(""));
	}


	@SneakyThrows
	private void updatePassword() {
		user = authService.getCurrentUser();
		if (PassUtils.isPassRelevant(user.getName(), newPassF2.getText())) {
			if (user.getPass().equals(PassUtils.hashPass(oldPassF.getText()))) {
				processErrors(errorField, "", "", oldPassF, newPassF2);
				user.setPass(PassUtils.hashPass(newPassF.getText()));
				userRepository.save(user);
				((Stage) errorField.getScene().getWindow()).close();
			} else {
				processErrors(errorField, String.format(PASS_DONT_MATCH, --attempts), INVALID_INPUT, oldPassF);
				if (attempts == 0) {
					errorField.setText(errorField.getText() + OUT_OF_ATTEMPTS);
					toggleSaveButton();
				}
			}
		} else {
			processErrors(errorField, WEAK_PASS, INVALID_INPUT, newPassF2);
		}
	}

	private void processErrors(Label label, String errorText, String style, Control... inputs) {
		Arrays.stream(inputs).toList().forEach(input -> input.setStyle(style));
		label.setText(errorText);
	}
}
