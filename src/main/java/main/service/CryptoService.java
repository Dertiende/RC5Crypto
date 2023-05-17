package main.service;

import main.GUI.service.AlertService;
import main.domain.RC5Data;
import main.model.RequestInfo;
import main.utils.CryptoProviderRC5;
import main.repository.RC5Repository;
import main.utils.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static main.utils.Constants.ENCRYPTION;

@Service
public class CryptoService {

	@Autowired
	RC5Repository rc5Repository;

	@Autowired
	AuthService authService;
	@Autowired
	AlertService alertService;

	public void doCryptography(RequestInfo info) throws IOException {
		CryptoProviderRC5 rc5 = new CryptoProviderRC5((RC5Data) info.requestData());
		if (info.mode().equals(ENCRYPTION)) {
			encryptFile(rc5, info);
		} else {
			decryptFile(rc5, info);
		}
	}

	private void encryptFile(CryptoProviderRC5 rc5, RequestInfo info) throws IOException {
		Long elapsedTime = rc5.encryptFile(info);
		((RC5Data) info.requestData()).setHash(CryptoUtils.getCRC32(info.inputFilePath()));
		rc5Repository.findRC5DataByUserAndHash(authService.getCurrentUser(), ((RC5Data) info.requestData()).getHash())
				.ifPresent(existed -> rc5Repository.delete(existed));
		rc5Repository.save((RC5Data) info.requestData());
		System.out.println("Encode time: " + (int) (elapsedTime / 1000) + " sec");

	}

	private void decryptFile(CryptoProviderRC5 rc5, RequestInfo info) throws IOException {
		Long elapsedTime = rc5.decryptFile(info);
		CryptoUtils.isHashCorrect(CryptoUtils.getCRC32(info.outputFilePath()), ((RC5Data) info.requestData()).getHash());
		System.out.println("Decode time: " + (int) (elapsedTime / 1000) + " sec");
	}

}
