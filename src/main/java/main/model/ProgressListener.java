package main.model;

import javafx.concurrent.Task;

public abstract class ProgressListener extends Task<Long> {
	public void update(Long newValue, Long size){};
}
