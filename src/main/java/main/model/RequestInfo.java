package main.model;

import lombok.Builder;

@Builder
public record RequestInfo(String inputFilePath, String outputFilePath, ProgressListener progressListener, String mode, Object requestData) {
}
