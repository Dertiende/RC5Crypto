package main.config;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import lombok.AllArgsConstructor;
import lombok.Data;
import main.GUI.controllers.Authorization;
import main.GUI.controllers.MainView;
import main.GUI.controllers.UserSettingsView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class ControllersConfiguration {

	@Bean(name = "mainView")
	public ViewHolder getMainView() throws IOException {
		return loadView("fxml/main_view.fxml");
	}

	@Bean(name = "authView")
	public ViewHolder getAuthView() throws IOException {
		return loadView("fxml/authorization.fxml");
	}

	@Bean(name = "settingsView")
	public ViewHolder getSettingsView() throws IOException {
		return loadView("fxml/userSettings.fxml");
	}

	/**
	 * Именно благодаря этому методу мы добавили контроллер в контекст спринга,
	 * и заставили его произвести все необходимые инъекции.
	 */
	@Bean
	public MainView getEncryption() throws IOException {
		return (MainView) getMainView().getController();
	}

	@Bean
	public Authorization getAuth() throws IOException {
		return (Authorization) getAuthView().getController();
	}

	@Bean
	public UserSettingsView getSettings() throws IOException {
		return (UserSettingsView) getSettingsView().getController();
	}

	/**
	 * Самый обыкновенный способ использовать FXML загрузчик.
	 * Как раз-таки на этом этапе будет создан объект-контроллер,
	 * произведены все FXML инъекции и вызван метод инициализации контроллера.
	 */
	protected ViewHolder loadView(String url) throws IOException {
		try (InputStream fxmlStream = getClass().getClassLoader().getResourceAsStream(url)) {
			FXMLLoader loader = new FXMLLoader();
			loader.load(fxmlStream);
			return new ViewHolder(loader.getRoot(), loader.getController());
		}
	}

	/**
	 * Класс - оболочка: контроллер мы обязаны указать в качестве бина,
	 * а view - представление, нам предстоит использовать в точке входа {@link Application}.
	 */
	@Data
	@AllArgsConstructor
	public static class ViewHolder {
		private Parent view;
		private Object controller;

	}

}
