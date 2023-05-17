package main.GUI.service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

import static main.utils.Constants.ERROR_ABSTRACT;
import static main.utils.Constants.ERROR_HEADER;
import static main.utils.Constants.ERROR_TITLE;

@Service
public class AlertService {

	public void showAlertWithHeaderText(AlertData data) {
		Alert alert = new Alert(data.getType());
		showAlert(alert, data);
	}

	private void showAlert(Alert alert, AlertData data) {
		alert.setTitle(data.getTitle());
		alert.setHeaderText(data.getHeader());
		alert.setContentText(data.getContext());
		alert.setResizable(false);
		Optional<ButtonType> result =  alert.showAndWait();
		result.ifPresent(buttonType -> {
			if (buttonType.getButtonData().equals(ButtonBar.ButtonData.YES)) {
				try {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + data.getOutputPath());
				} catch (IOException ignored) {}
			}
		});
	}

	public void showSuccessfulAlert(AlertData data) {
		ButtonType openFolder = new ButtonType("Open folder", ButtonBar.ButtonData.YES);
		ButtonType ok = new ButtonType("Close", ButtonBar.ButtonData.OK_DONE);
		Alert alert = new Alert(data.getType(),"", openFolder, ok);
		showAlert(alert, data);
	}

	public static AlertData abstractError() {
		return AlertService.AlertData.builder()
           .type(Alert.AlertType.ERROR)
           .title(ERROR_TITLE)
           .header(ERROR_HEADER)
           .context(ERROR_ABSTRACT)
           .build();
	}
	@Data
	@Builder
	@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
	public static class AlertData {
		Alert.AlertType type;
		String title;
		String header;
		String context;
		String outputPath;
	}


}
