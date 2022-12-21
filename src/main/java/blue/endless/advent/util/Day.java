package blue.endless.advent.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class Day {
	public abstract void partOne(List<String> input);
	public abstract void partTwo(List<String> input);
	
	protected abstract String getRawSampleData();
	protected String getDataFileName() {
		return this.getClass().getSimpleName().toLowerCase()+".txt";
	}
	
	public List<String> getSampleData() {
		String[] lineArray = getRawSampleData().split("\\n");
		List<String> result = new ArrayList<>();
		for(String line : lineArray) result.add(line);
		return result;
	}
	
	public List<String> getFileData() {
		try {
			return Files.readAllLines(Path.of("data", getDataFileName()));
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public void partOne(boolean debug) {
		if (debug) {
			partOne(getSampleData());
		} else {
			partOne(getFileData());
		}
	}
	
	public void partTwo(boolean debug) {
		if (debug) {
			partTwo(getSampleData());
		} else {
			partTwo(getFileData());
		}
	}
}
