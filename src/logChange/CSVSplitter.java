package logChange;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVSplitter {

	static String strHeader = null;

	public static void main(String inputFilePath, String outputDirectoryPath) {
		String outputFilePrefix = "_output_insert"; // 修改文件前缀
		int batchSize = 10000;

		try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
			// Skip the header
			strHeader = reader.readLine();

			// Count total lines in the file
			int totalLines = 0;
			while (reader.readLine() != null) {
				totalLines++;
			}

			// Reset the reader
			reader.close();
			BufferedReader newReader = new BufferedReader(new FileReader(inputFilePath));
			newReader.readLine(); // Skip the header again

			int batchCounter = 1;
			int linesWritten = 0;
			List<String> linesToWrite = new ArrayList<>();
			String line;

			while ((line = newReader.readLine()) != null) {
				linesToWrite.add(line);
				linesWritten++;

				if (linesWritten >= batchSize || (linesWritten + (batchCounter - 1) * batchSize) >= totalLines) {
					// Create a new file
					FileWriter writer = new FileWriter(outputDirectoryPath + String.format("%02d", batchCounter) + outputFilePrefix + ".csv");

					// Write header
					writer.write(strHeader + System.lineSeparator());
					// Write lines
					for (String lineToWrite : linesToWrite) {
						writer.write(lineToWrite + "\n");
					}

					// Close the file
					writer.close();

					// Reset counters and list
					linesToWrite.clear();
					linesWritten = 0;
					batchCounter++;
				}
			}
			newReader.close();
			System.out.println("CSV file has been splitted successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		main("D:\\test_files\\input.csv", "D:\\test_files\\work231215\\");
	}
}
