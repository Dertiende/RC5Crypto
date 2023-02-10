package main;

import lombok.SneakyThrows;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;

import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_AAC;
import static org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_H264;

public class ConverterService {
	@SneakyThrows
	public static void convertVideo(File file) {
		FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file.getAbsolutePath());
		String fileFullPathName = null;
		Frame captured_frame = null;
		FFmpegFrameRecorder recorder = null;
		frameGrabber.start();
		// Get the video name after the transcoding
		String name = "D:\\"+file.getName();
		fileFullPathName = name.replace(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf(".")), ".mp4");
		recorder = new FFmpegFrameRecorder(fileFullPathName, frameGrabber.getImageWidth(), frameGrabber.getImageHeight(), frameGrabber.getAudioChannels());
		recorder.setVideoCodec(AV_CODEC_ID_H264);
		recorder.setFormat("mp4");
		recorder.setFrameRate(frameGrabber.getFrameRate());
		recorder.setVideoBitrate(frameGrabber.getVideoBitrate());
		recorder.setAudioBitrate(192000);
		recorder.setAudioOptions(frameGrabber.getAudioOptions());
		recorder.setAudioQuality(0);
		recorder.setSampleRate(44100);
		recorder.setAudioCodec(AV_CODEC_ID_AAC);
		recorder.start();
		while (true) {
			try {
				captured_frame = frameGrabber.grabFrame();

				if (captured_frame == null) {
//						System.out.println("!!! Failed cvQueryFrame");
					break;
				}
				recorder.record(captured_frame);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		recorder.stop();
		recorder.release();
		frameGrabber.stop();
		frameGrabber.release();
		recorder.close();
		frameGrabber.close();
	}
}
