package logChange;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CSVSplitterTest {

	private static Path localFileDirPath;
	private static final int dataCount = 99998;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {

		String sourceDirectory = null;

		Class<?> clazz = CSVSplitter.class;

		// 获取类的 URI
		URL url = clazz.getResource(clazz.getSimpleName() + ".class");
		URI uri = URI.create(url.toString());

		// 尝试获取源代码路径的上一级路径
		if ("file".equals(uri.getScheme())) {
			Path path = Paths.get(uri).toAbsolutePath();
			String classPath = path.toString();

			// 获取包结构之前的路径
			int index = classPath.lastIndexOf("bin");
			if (index != -1) {
				sourceDirectory = classPath.substring(0, index) + "\\testfolder";
				System.out.println("Source Code Directory: " + sourceDirectory);
			} else {
				System.out.println("Unable to determine source code directory.");
			}
		} else {
			System.out.println("Unable to determine source code directory.");
		}

		localFileDirPath = Files.createDirectories(FileSystems.getDefault().getPath(sourceDirectory));

		System.out.println("localFileDirPath = " + localFileDirPath.toString());

	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {

		FileUtils.deleteDirectory(localFileDirPath.toFile());

	}

	@BeforeEach
	void setUp() throws Exception {

	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void test() throws IOException {

		createTestFile();
		CSVSplitter.main(localFileDirPath.toString() + "\\inputTest.csv", localFileDirPath.toString() + "\\");

		String directoryPath = localFileDirPath.toString();
		String regexPattern = "\\d{2}_output_insert\\.csv";

		int fileCount = countMatchingFiles(directoryPath, regexPattern);

		// assert 10 files
		assertEquals(dataCount / 10000 + 1, fileCount);

		try (BufferedReader reader = new BufferedReader(
				new FileReader(localFileDirPath.toString() + "\\" + "01_output_insert.csv"))) {

			int totalLines = 0;
			while (reader.readLine() != null) {
				totalLines++;
			}
			assertEquals(10001, totalLines);

		}

	}

	private void createTestFile() {

		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter(localFileDirPath.toString() + "\\inputTest.csv"))) {
			// 写入 CSV 头部
			writer.write("company no,company name");
			writer.newLine();

			// 写入 CSV 数据
			for (int i = 1; i <= dataCount; i++) {
				String line = i + ",kk-" + i;
				writer.write(line);
				writer.newLine();
			}

			System.out.println("CSV file has been created successfully.");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static int countMatchingFiles(String directoryPath, String regexPattern) {
		Path dir = Paths.get(directoryPath);
		int count = 0;

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (Path entry : stream) {
				if (Files.isRegularFile(entry)) {
					String fileName = entry.getFileName().toString();
					if (Pattern.matches(regexPattern, fileName)) {
						count++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return count;
	}

}
