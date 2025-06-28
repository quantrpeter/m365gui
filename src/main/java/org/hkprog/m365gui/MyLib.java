package org.hkprog.m365gui;

import hk.quantr.setting.library.QuantrSettingLibrary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author peter
 */
public class MyLib {

	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(MyLib.class);

	private static List<String> splitCommand(String command) {
		List<String> tokens = new ArrayList<>();
		boolean inQuotes = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < command.length(); i++) {
			char c = command.charAt(i);
			if (c == '"') {
				inQuotes = !inQuotes;
			} else if (c == ' ' && !inQuotes) {
				if (sb.length() > 0) {
					String arg = sb.toString();
					if (arg.startsWith("\"") && arg.endsWith("\"")) {
						arg = arg.substring(1, arg.length() - 1);
					}
					tokens.add(arg);
					sb.setLength(0);
				}
			} else {
				sb.append(c);
			}
		}
		if (sb.length() > 0) {
			String arg = sb.toString();
			if (arg.startsWith("\"") && arg.endsWith("\"")) {
				arg = arg.substring(1, arg.length() - 1);
			}
			tokens.add(arg);
		}
		return tokens;
	}

	public static String run(String command) {
		try {
			Setting setting = new Setting();
			QuantrSettingLibrary.load("m365gui", setting);
			List<String> cmdList = splitCommand(command);
			ProcessBuilder processBuilder = new ProcessBuilder(cmdList);
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

	public static void exportTableToExcel(JComponent parent, JTable mainTable, String filename, String sheetName) {
		if (mainTable.getRowCount() == 0) {
			JOptionPane.showMessageDialog(parent, "No data to export!", "Export Error", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Create file chooser
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Excel File");
		fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx"));

		// Generate default filename
		fileChooser.setSelectedFile(new File(filename));

		int userSelection = fileChooser.showSaveDialog(parent);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			// Ensure file has .xlsx extension
			final File fileToSave;
			if (!selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
				fileToSave = new File(selectedFile.getParentFile(), selectedFile.getName() + ".xlsx");
			} else {
				fileToSave = selectedFile;
			}

			// Export in background thread
			SwingWorker<Void, Void> exportWorker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					try (Workbook workbook = new XSSFWorkbook()) {
						Sheet sheet = workbook.createSheet(sheetName);

						// Create header row
						Row headerRow = sheet.createRow(0);
						CellStyle headerStyle = workbook.createCellStyle();
						Font headerFont = workbook.createFont();
						headerFont.setBold(true);
						headerFont.setColor(IndexedColors.WHITE.getIndex());
						headerStyle.setFont(headerFont);
						headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
						headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
						headerStyle.setBorderBottom(BorderStyle.THIN);
						headerStyle.setBorderTop(BorderStyle.THIN);
						headerStyle.setBorderRight(BorderStyle.THIN);
						headerStyle.setBorderLeft(BorderStyle.THIN);

						// Add column headers
						for (int col = 0; col < mainTable.getColumnCount(); col++) {
							Cell cell = headerRow.createCell(col);
							cell.setCellValue(mainTable.getColumnName(col));
							cell.setCellStyle(headerStyle);
						}

						// Create data style
						CellStyle dataStyle = workbook.createCellStyle();
						dataStyle.setBorderBottom(BorderStyle.THIN);
						dataStyle.setBorderTop(BorderStyle.THIN);
						dataStyle.setBorderRight(BorderStyle.THIN);
						dataStyle.setBorderLeft(BorderStyle.THIN);

						// Add data rows
						for (int row = 0; row < mainTable.getRowCount(); row++) {
							Row dataRow = sheet.createRow(row + 1);
							for (int col = 0; col < mainTable.getColumnCount(); col++) {
								Cell cell = dataRow.createCell(col);
								Object value = mainTable.getValueAt(row, col);

								if (value != null) {
									if (value instanceof Number) {
										cell.setCellValue(((Number) value).doubleValue());
									} else if (value instanceof Boolean) {
										cell.setCellValue((Boolean) value);
									} else {
										cell.setCellValue(value.toString());
									}
								}
								cell.setCellStyle(dataStyle);
							}
						}

						// Auto-size columns
						for (int col = 0; col < mainTable.getColumnCount(); col++) {
							sheet.autoSizeColumn(col);
							// Limit column width to prevent extremely wide columns
							if (sheet.getColumnWidth(col) > 15000) {
								sheet.setColumnWidth(col, 15000);
							}
						}

						// Write to file
						try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
							workbook.write(fileOut);
						}

					} catch (IOException e) {
						javax.swing.SwingUtilities.invokeLater(() -> {
							JOptionPane.showMessageDialog(parent,
									"Error exporting to Excel: " + e.getMessage(),
									"Export Error",
									JOptionPane.ERROR_MESSAGE);
						});
					}
					return null;
				}

				@Override
				protected void done() {
					JOptionPane.showMessageDialog(parent,
							"Data exported successfully to: " + fileToSave.getAbsolutePath(),
							"Export Complete",
							JOptionPane.INFORMATION_MESSAGE);
				}
			};
			exportWorker.execute();
		}
	}
}
