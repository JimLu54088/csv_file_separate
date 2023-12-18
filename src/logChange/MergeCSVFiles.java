package logChange;
//String folderPath = "D:\\test_files\\work231215\\";

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MergeCSVFiles {
	public static void main(String[] args) {
		String folderPath = "D:\\test_files\\work231215\\";
		String outputPath = "D:\\test_files\\work231215\\output_merged\\merged_output.csv";
		mergeCSVFiles(folderPath, outputPath);
	}

	public static void mergeCSVFiles(String folderPath, String outputPath) {
		File folder = new File(folderPath);
		File[] files = folder.listFiles();

		if (files == null) {
			System.out.println("Folder does not exist or is not a directory.");
			return;
		}

		// 定义正则表达式，匹配数字_output_insert.csv格式的文件名
		Pattern pattern = Pattern.compile("(\\d+)__output_insert\\.csv");

		// 用于存储合并后的数据
		StringBuilder mergedData = new StringBuilder();

		// 标志，用于判断是否已经写入了CSV文件头
		boolean headerWritten = false;

		// 用于存储第一个CSV文件的标题行
		String header = null;

		for (File file : files) {
			if (file.isFile()) {
				Matcher matcher = pattern.matcher(file.getName());

				// 判断文件名是否匹配正则表达式
				if (matcher.matches()) {
					try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
						String line;

						// 读取每行数据
						while ((line = reader.readLine()) != null) {
							if (!headerWritten) {
								// 如果还没有写入CSV文件头，则保存第一个文件的标题行
								header = line.trim();
								mergedData.append(header).append("\n");
								headerWritten = true;
							} else if (!line.trim().isEmpty() && !line.trim().equals(header)) {
								// 从第二行开始写入数据，且不包括标题行

								mergedData.append(line).append("\n");

							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// 将合并后的数据写入新的CSV文件
		try (FileWriter writer = new FileWriter(outputPath)) {
			writer.write(mergedData.toString());
			System.out.println("CSV files merged successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
