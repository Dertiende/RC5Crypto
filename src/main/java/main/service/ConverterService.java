package main.service;

import main.model.RequestInfo;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.springframework.stereotype.Service;

import java.io.File;

import static main.utils.Constants.AUDIO;
import static main.utils.Constants.codecMap;

@Service
public class ConverterService {

	private static final Integer bitrate = 256000;
	private static final Integer samplingRate = 44100;

	public void doConversion(RequestInfo info) {
		boolean isAudio = info.mode().equals(AUDIO);
		File inputFile;
		File outputFile;
		try {
			inputFile = new File(info.inputFilePath());
			outputFile = new File(info.outputFilePath());
		} catch (Exception e) {
			//TODO: обработка ошибок
			return;
		}
		long frameCount = getFramesCount(inputFile, isAudio);
		try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputFile)) {
			frameGrabber.start();
			try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, frameGrabber.getAudioChannels())) {
				recorder.setFormat((String) info.requestData());
				if (isAudio) {
					setAudioFields(recorder, (String) info.requestData());
				} else {
					setVideoFields(recorder, (String) info.requestData(), frameGrabber);
				}
				recorder.start();
				Frame frame;
				long counter = 0;

				if (isAudio) {
					while ((frame = frameGrabber.grabSamples()) != null) {
						recorder.record(frame);
						info.progressListener().update(++counter, frameCount);
					}
				} else {
					while ((frame = frameGrabber.grabFrame()) != null) {
						recorder.record(frame);
						info.progressListener().update(++counter, frameCount);
					}
				}
				recorder.stop();
				recorder.release();
				frameGrabber.stop();
				frameGrabber.release();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}
	}

	private void setVideoFields(FFmpegFrameRecorder recorder, String format, FFmpegFrameGrabber grabber) {
		recorder.setVideoCodec(codecMap.get(format));
		recorder.setImageHeight(grabber.getImageHeight());
		recorder.setImageWidth(grabber.getImageWidth());
		recorder.setFrameRate(grabber.getFrameRate());
		recorder.setVideoBitrate(grabber.getVideoBitrate());
		recorder.setAudioOptions(grabber.getAudioOptions());
	}

	private void setAudioFields(FFmpegFrameRecorder recorder, String format) {
		recorder.setSampleRate(samplingRate);
		recorder.setAudioCodec(codecMap.get(format));
	}


	private long getFramesCount(File inputFile, boolean isAudio) {
		long frameCount = 0;
		try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(inputFile)) {
			frameGrabber.start();
			if (isAudio) {
				while (frameGrabber.grabSamples() != null) {
					frameCount++;
				}
			} else {
				while (frameGrabber.grabFrame() != null) {
					frameCount++;
				}
			}
			frameGrabber.stop();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new RuntimeException();
		}
		return frameCount;
	}
}
