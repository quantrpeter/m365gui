package org.hkprog.m365gui;

import hk.quantr.setting.library.QuantrSettingLibrary;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author peter
 */
public class MyLib {

	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MyLib.class);

	public static String run(String command) {
		try {
			Setting setting = new Setting();
			QuantrSettingLibrary.load("m365gui", setting);
			ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
			processBuilder.environment().put("PATH", new File(setting.m365Path).getParent());
			Process process = processBuilder.start();
			// Capture output
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			StringBuilder output = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line).append("\n");
			}

			// Capture errors
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			StringBuilder errorOutput = new StringBuilder();
			while ((line = errorReader.readLine()) != null) {
				errorOutput.append(line).append("\n");
			}
			// Wait for process to complete
			int exitCode = process.waitFor();
//			logger.info("Exit Code: " + exitCode);
//			logger.info("Output:\n" + output.toString());
			if (errorOutput.length() > 0) {
				System.err.println("Errors:\n" + errorOutput.toString());
			}
			return output.toString();
		} catch (IOException | IllegalAccessException | IllegalArgumentException | InterruptedException ex) {
			logger.error("Error executing command", ex);
		}
		return null;
	}
}
