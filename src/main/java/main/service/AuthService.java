package main.service;

import javafx.scene.control.Alert;
import main.GUI.service.AlertService;
import main.utils.PassUtils;
import main.domain.User;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static main.utils.Constants.ERROR_HEADER;
import static main.utils.Constants.ERROR_TITLE;
import static main.utils.Constants.ERROR_WRONG_AUTH;
import static main.utils.Constants.WARNING_HEADER;
import static main.utils.Constants.WARNING_TITLE;
import static main.utils.Constants.WARNING_WEAK_PASS;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AlertService alertService;

	private User user;

	public boolean isAuthLegal(String login, String pass) throws NoSuchAlgorithmException {
		Optional<User> optionalUser = userRepository.findUserByName(login);
		String hash = PassUtils.hashPass(pass);
		if (optionalUser.isPresent()){
			user = optionalUser.get();
			if (hash.equals(user.getPass())) {
				return true;
			} else {
				AlertService.AlertData alertData = AlertService.AlertData.builder()
                   .type(Alert.AlertType.ERROR)
	               .title(ERROR_TITLE)
	               .header(ERROR_HEADER)
	               .context(ERROR_WRONG_AUTH)
	               .build();
				alertService.showAlertWithHeaderText(alertData);
				return false;
			}
		} else{
			if (!PassUtils.isPassRelevant(login, pass)){
				AlertService.AlertData alertData = AlertService.AlertData.builder()
	               .type(Alert.AlertType.WARNING)
	               .title(WARNING_TITLE)
	               .header(WARNING_HEADER)
	               .context(WARNING_WEAK_PASS)
	               .build();
				alertService.showAlertWithHeaderText(alertData);
				return false;
			}
			user = userRepository.save(new User(login, hash));
		}
		return true;
	}

	public User getCurrentUser(){
		if (user == null) {
			alertService.showAlertWithHeaderText(AlertService.abstractError());
		}
		return user;
	}
}
