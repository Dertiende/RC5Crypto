package main.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_FLAC;
import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264;
import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MP3;
import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MPEG4;

public class Constants {
	public static final String ENCRYPTION = "Encryption";
	public static final String DECRYPTION = "Decryption";
	public static final String AUDIO = "Audio";
	public static final String VIDEO = "Video";
	public static final String MP3 = "mp3";
	public static final String FLAC = "flac";
	public static final String MP4 = "mp4";
	public static final String MKV = "mkv";

	public static final List<String> CRYPTO_MODES = Arrays.asList(ENCRYPTION, DECRYPTION);
	public static final List<String> CONVERT_MODES = Arrays.asList(VIDEO, AUDIO);
	public static final List<String> AUDIO_FORMATS = Arrays.asList(MP3, FLAC);
	public static final List<String> VIDEO_FORMATS = Arrays.asList(MP4, MKV);
	public static final Map<String, Integer> codecMap;
	static {
		codecMap = new HashMap<>();
		codecMap.put(MP3, AV_CODEC_ID_MP3);
		codecMap.put(FLAC, AV_CODEC_ID_FLAC);
		codecMap.put(MP4, AV_CODEC_ID_MPEG4);
		codecMap.put(MKV, AV_CODEC_ID_H264);
	}

	//Errors
	public static final String ERROR_TITLE = "Error";
	public static final String ERROR_HEADER = "An error has occurred";
	public static final String ERROR_ABSTRACT = "Please contact the administrator";
	public static final String ERROR_WRONG_AUTH = "User Exists. Wrong password.";

	//Warnings
	public static final String WARNING_TITLE = "Warning";
	public static final String WARNING_HEADER = "Please pay attention";
	public static final String WARNING_WEAK_PASS = "Password Must be 10+ symbols of Cyrillic and Latin alphabets, numbers and arithmetic operands. Mustn't consists only from login's characters.";

	//Confirm
	public static final String CONFIRM_TITLE = "Successful operation";
	public static final String CONFIRM_HEADER = "The operation was successfully performed";

	public final static String CONVERTER_ICON = "M64,0c-8.8,0-17.2,1.8-24.9,5S24.6,13,18.8,18.8S8.3,31.5,5,39.1S0,55.2,0,64v96v96v96v96c0,8.8,1.8,17.2,5,24.9\n" +
			                                            "\ts7.9,14.6,13.7,20.3s12.7,10.5,20.3,13.7s16.1,5,24.9,5h64h64h64h64c8.8,0,17.2-1.8,24.9-5s14.6-7.9,20.3-13.7\n" +
			                                            "\tc5.8-5.8,10.5-12.7,13.7-20.3s5-16.1,5-24.9v-72v-72v-72v-72h-32h-32h-32h-32c-4.4,0-8.6-0.9-12.5-2.5c-3.8-1.6-7.3-4-10.2-6.9\n" +
			                                            "\ts-5.2-6.3-6.9-10.2c-1.6-3.8-2.5-8-2.5-12.5V96V64V32V0h-40h-40h-40H64z M256,0v32v32v32v32h32h32h32h32l-32-32l-32-32l-32-32L256,0\n" +
			                                            "\tz M159.5,215c5.6,3.4,10.7,7.4,15.3,12.1c4.6,4.6,8.5,9.8,11.8,15.5c3.3,5.7,5.8,11.8,7.6,18.2c1.8,6.5,2.7,13.3,2.7,20.3\n" +
			                                            "\ts-0.9,13.8-2.7,20.3c-1.8,6.5-4.3,12.6-7.6,18.2c-3.3,5.7-7.2,10.9-11.8,15.5c-4.6,4.6-9.7,8.7-15.3,12.1c-1.1,0.7-2.4,1.1-3.6,1.3\n" +
			                                            "\tc-1.2,0.2-2.5,0.1-3.7-0.2s-2.3-0.8-3.4-1.6s-1.9-1.7-2.6-2.8c-0.7-1.1-1.1-2.4-1.3-3.6c-0.2-1.2-0.1-2.5,0.2-3.7\n" +
			                                            "\tc0.3-1.2,0.8-2.3,1.6-3.4c0.7-1,1.7-1.9,2.8-2.6c4.2-2.5,8.1-5.6,11.5-9.1c3.4-3.5,6.4-7.4,8.9-11.6c2.5-4.3,4.4-8.8,5.7-13.7\n" +
			                                            "\tc1.3-4.8,2-9.9,2-15.2s-0.7-10.4-2-15.2s-3.2-9.4-5.7-13.7c-2.5-4.3-5.4-8.2-8.8-11.6c-3.4-3.5-7.3-6.5-11.5-9.1\n" +
			                                            "\tc-1.1-0.7-2.1-1.6-2.8-2.6c-0.7-1-1.3-2.2-1.6-3.4c-0.3-1.2-0.4-2.5-0.2-3.7c0.2-1.2,0.6-2.5,1.3-3.6c0.7-1.1,1.6-2.1,2.6-2.8\n" +
			                                            "\tc1-0.7,2.2-1.3,3.4-1.6c1.2-0.3,2.5-0.4,3.7-0.2S158.4,214.3,159.5,215L159.5,215L159.5,215L159.5,215L159.5,215z M104.1,233.6\n" +
			                                            "\tc0.9,0.4,1.7,0.9,2.5,1.5c0.7,0.6,1.4,1.3,1.9,2.1c0.5,0.8,0.9,1.6,1.2,2.5c0.3,0.9,0.4,1.9,0.4,2.8v19.3v19.3v19.3v19.3\n" +
			                                            "\tc0,1-0.1,1.9-0.4,2.8c-0.3,0.9-0.7,1.8-1.2,2.5c-0.5,0.8-1.2,1.5-1.9,2.1s-1.6,1.1-2.5,1.5c-0.9,0.4-1.8,0.6-2.8,0.7s-1.9,0-2.8-0.1\n" +
			                                            "\tc-0.9-0.2-1.8-0.5-2.6-0.9s-1.6-1-2.3-1.7l-5.3-5.3l-5.3-5.3l-5.3-5.3l-5.3-5.3h-5h-5h-5h-5c-1.3,0-2.6-0.3-3.7-0.8\n" +
			                                            "\tc-1.2-0.5-2.2-1.2-3.1-2.1c-0.9-0.9-1.6-1.9-2.1-3.1c-0.5-1.2-0.8-2.4-0.8-3.7v-7.2v-7.2v-7.2v-7.2c0-1.3,0.3-2.6,0.8-3.7\n" +
			                                            "\tc0.5-1.2,1.2-2.2,2.1-3.1c0.9-0.9,1.9-1.6,3.1-2.1c1.2-0.5,2.4-0.8,3.7-0.8h5h5h5h5l5.3-5.3l5.3-5.3l5.3-5.3l5.3-5.3\n" +
			                                            "\tc0.7-0.7,1.5-1.3,2.3-1.7c0.8-0.4,1.7-0.8,2.6-0.9s1.9-0.2,2.8-0.1S103.2,233.3,104.1,233.6L104.1,233.6L104.1,233.6L104.1,233.6\n" +
			                                            "\tL104.1,233.6z M134.8,254.6c1-0.9,2.1-1.5,3.3-1.9c1.2-0.4,2.4-0.6,3.7-0.5s2.5,0.4,3.6,0.9c1.1,0.5,2.2,1.3,3.1,2.3\n" +
			                                            "\tc1.5,1.7,2.9,3.5,4.1,5.5c1.2,2,2.2,4,3.1,6.2c0.8,2.2,1.5,4.4,2,6.8c0.4,2.3,0.7,4.8,0.7,7.2s-0.2,4.9-0.7,7.2\n" +
			                                            "\tc-0.4,2.3-1.1,4.6-2,6.8c-0.8,2.2-1.9,4.3-3.1,6.2s-2.6,3.8-4.1,5.5c-0.9,1-1.9,1.8-3.1,2.3c-1.1,0.5-2.4,0.8-3.6,0.9\n" +
			                                            "\tc-1.2,0.1-2.5-0.1-3.7-0.5c-1.2-0.4-2.3-1-3.3-1.9c-1-0.9-1.8-1.9-2.3-3c-0.5-1.1-0.8-2.4-0.9-3.6s0.1-2.5,0.5-3.7s1-2.3,1.9-3.3\n" +
			                                            "\tc0.8-0.9,1.5-1.8,2.1-2.8s1.1-2,1.5-3.1s0.7-2.2,1-3.4c0.2-1.2,0.3-2.4,0.3-3.6s-0.1-2.4-0.3-3.6c-0.2-1.2-0.5-2.3-1-3.4\n" +
			                                            "\tc-0.4-1.1-0.9-2.1-1.5-3.1s-1.3-1.9-2-2.7c-0.9-1-1.5-2.1-1.9-3.3s-0.6-2.4-0.5-3.7c0.1-1.2,0.4-2.5,0.9-3.6\n" +
			                                            "\tC133,256.6,133.8,255.6,134.8,254.6L134.8,254.6L134.8,254.6L134.8,254.6L134.8,254.6z M186.8,358.9c0-11.1,8.9-20,20-20h60\n" +
			                                            "\tc11.1,0,20,8.9,20,20v60c0,11.1-8.9,20-20,20h-60c-11.1,0-20-8.9-20-20V358.9z M334.9,427.6l-28.1-18.7v-40l28.1-18.7\n" +
			                                            "\tc1.2-0.8,2.8-1.3,4.2-1.3c4.2,0,7.7,3.4,7.7,7.7v64.6c0,4.2-3.4,7.7-7.7,7.7C337.6,428.9,336.1,428.5,334.9,427.6z";
	public final static String CRYPTO_ICON = "M64,0C28.7,0,0,28.7,0,64v384c0,35.3,28.7,64,64,64h256c35.3,0,64-28.7,64-64V160H256c-17.7,0-32-14.3-32-32V0H64z M256,0\n" +
			                                         "\tv128h128L256,0z M185.3,226.3c4.3-1.7,9.1-1.7,13.3,0l90,36c6.8,2.8,11.3,9.4,11.3,16.7c0,47.5-19.4,126.6-101.1,160.6\n" +
			                                         "\tc-4.4,1.9-9.4,1.9-13.9,0C103.4,405.6,84,326.5,84,279c0-7.3,4.5-13.9,11.3-16.7L185.3,226.3L185.3,226.3z M263.5,291L192,262.4\n" +
			                                         "\tv140.9C243.1,378.5,260.6,329,263.5,291z";
	public final static String USER_ICON_PATH = "M610.5 373.3c2.6-14.1 2.6-28.5 0-42.6l25.8-14.9c3-1.7 4.3-5.2 3.3-8.5-6.7-21.6-18.2-41.2-33.2-57.4-2.3-2.5-6-3.1-9-1.4l-25.8 14.9c-10.9-9.3-23.4-16.5-36.9-21.3v-29.8c0-3.4-2.4-6.4-5.7-7.1-22.3-5-45-4.8-66.2 0-3.3.7-5.7 3.7-5.7 7.1v29.8c-13.5 4.8-26 12-36.9 21.3l-25.8-14.9c-2.9-1.7-6.7-1.1-9 1.4-15 16.2-26.5 35.8-33.2 57.4-1 3.3.4 6.8 3.3 8.5l25.8 14.9c-2.6 14.1-2.6 28.5 0 42.6l-25.8 14.9c-3 1.7-4.3 5.2-3.3 8.5 6.7 21.6 18.2 41.1 33.2 57.4 2.3 2.5 6 3.1 9 1.4l25.8-14.9c10.9 9.3 23.4 16.5 36.9 21.3v29.8c0 3.4 2.4 6.4 5.7 7.1 22.3 5 45 4.8 66.2 0 3.3-.7 5.7-3.7 5.7-7.1v-29.8c13.5-4.8 26-12 36.9-21.3l25.8 14.9c2.9 1.7 6.7 1.1 9-1.4 15-16.2 26.5-35.8 33.2-57.4 1-3.3-.4-6.8-3.3-8.5l-25.8-14.9zM496 400.5c-26.8 0-48.5-21.8-48.5-48.5s21.8-48.5 48.5-48.5 48.5 21.8 48.5 48.5-21.7 48.5-48.5 48.5zM224 256c70.7 0 128-57.3 128-128S294.7 0 224 0 96 57.3 96 128s57.3 128 128 128zm201.2 226.5c-2.3-1.2-4.6-2.6-6.8-3.9l-7.9 4.6c-6 3.4-12.8 5.3-19.6 5.3-10.9 0-21.4-4.6-28.9-12.6-18.3-19.8-32.3-43.9-40.2-69.6-5.5-17.7 1.9-36.4 17.9-45.7l7.9-4.6c-.1-2.6-.1-5.2 0-7.8l-7.9-4.6c-16-9.2-23.4-28-17.9-45.7.9-2.9 2.2-5.8 3.2-8.7-3.8-.3-7.5-1.2-11.4-1.2h-16.7c-22.2 10.2-46.9 16-72.9 16s-50.6-5.8-72.9-16h-16.7C60.2 288 0 348.2 0 422.4V464c0 26.5 21.5 48 48 48h352c10.1 0 19.5-3.2 27.2-8.5-1.2-3.8-2-7.7-2-11.8v-9.2z";

	public final static String WEAK_PASS = "Password Must be 10+ symbols of Cyrillic, Latin, numbers and arithmetic operands. Mustn't consists only from login's characters";
	public final static String PASS_DONT_MATCH = "Current password is wrong. Attempts left: %d.";
	public final static String DIFFERENT_PASSES = "Passwords don't match";
	public final static String OUT_OF_ATTEMPTS = " To change the password, you will need to log in again";
	public final static String INVALID_INPUT = "-fx-border-color: red; -fx-border-radius: 3";

}
