package logChange;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MergeCSVFilesTest {
	private static Path localFileDirPath;
	private static String context;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		String sourceDirectory = null;

		Class<?> clazz = MergeCSVFiles.class;

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
	void test() throws Exception {
		createTestFile();

		MergeCSVFiles.mergeCSVFiles(localFileDirPath.toString() + "\\",
				localFileDirPath.toString() + "\\merged_output.csv");

		try (BufferedReader reader = new BufferedReader(
				new FileReader(localFileDirPath.toString() + "\\merged_output.csv"))) {
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			context = content.toString();
		}

		String expectedCSVContent = "company no,company name\n" + "1,2\n" + "3,4\n" + "5,6\n" + "7,8\n";

		Assert.assertEquals(expectedCSVContent, context);

		deleteTempFile();
	}

	@Test
	void test_noDirectory() throws Exception {
		// 将 System.out 重定向到 ByteArrayOutputStream
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		MergeCSVFiles.mergeCSVFiles("D:\\kkk" + "\\", localFileDirPath.toString() + "\\merged_output.csv");

		assertEquals("Folder does not exist or is not a directory." + System.lineSeparator(), outputStream.toString());

		System.setOut(System.out);

	}

	@Test
	void test_DirectoryExist_no_matched_File_Name() throws Exception {
		Files.deleteIfExists(localFileDirPath.resolve("money_data_file_20230204_0.csv"));

		Files.createFile(localFileDirPath.resolve("money_data_file_20230204_0.csv"));

		MergeCSVFiles.mergeCSVFiles(localFileDirPath.toString() + "\\",
				localFileDirPath.toString() + "\\merged_output.csv");

	}

	@Test
	void test_DirectoryExist_IsNotFile() throws Exception {
		Files.deleteIfExists(localFileDirPath.resolve("01__output_insert.csv"));
		Files.createFile(localFileDirPath.resolve("01__output_insert.csv"));

		// 新目录的名称
		String newDirectoryName = "newSubdirectory";

		// 在已有路径下创建新的子目录
		Path newDirectoryPath = localFileDirPath.resolve(newDirectoryName);
		Files.createDirectories(newDirectoryPath);

		MergeCSVFiles.mergeCSVFiles(localFileDirPath.toString() + "\\",
				localFileDirPath.toString() + "\\merged_output.csv");

		deleteTempFile();

	}

	@Test
	void test_test_elseIf() throws Exception {
		createTestFile_test_elseIf();

		MergeCSVFiles.mergeCSVFiles(localFileDirPath.toString() + "\\",
				localFileDirPath.toString() + "\\merged_output.csv");

		try (BufferedReader reader = new BufferedReader(
				new FileReader(localFileDirPath.toString() + "\\merged_output.csv"))) {
			StringBuilder content = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
			context = content.toString();
		}

		String expectedCSVContent = "company no,company name\n" + "11,kk\n" + "11,kk\n";

		Assert.assertEquals(expectedCSVContent, context);

		deleteTempFile();
	}

	private void createTestFile() {

		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter(localFileDirPath.toString() + "\\01__output_insert.csv"))) {
			// 写入 CSV 头部
			writer.write("company no,company name");
			writer.newLine();
			writer.write("1,2");
			writer.newLine();
			writer.write("3,4");

			System.out.println("01__output_insert.csv CSV file has been created successfully.");

		} catch (IOException e) {
			e.printStackTrace();
		}

		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter(localFileDirPath.toString() + "\\02__output_insert.csv"))) {
			// 写入 CSV 头部
			writer.write("company no,company name");
			writer.newLine();
			writer.write("5,6");
			writer.newLine();
			writer.write("7,8");

			System.out.println("02__output_insert.csv CSV file has been created successfully.");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void createTestFile_test_elseIf() {

		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter(localFileDirPath.toString() + "\\01__output_insert.csv"))) {
			// 写入 CSV 头部
			writer.write("company no,company name");
			writer.newLine();
			writer.write("company no,company name");
			writer.newLine();
			writer.write("11,kk");

			System.out.println("01__output_insert.csv CSV file has been created successfully.");

		} catch (IOException e) {
			e.printStackTrace();
		}

		try (BufferedWriter writer = new BufferedWriter(
				new FileWriter(localFileDirPath.toString() + "\\02__output_insert.csv"))) {
			// 写入 CSV 头部
//			writer.write("company no,company name");
			writer.newLine();
			writer.write("company no,company name");
			writer.newLine();
			writer.write("11,kk");

			System.out.println("01__output_insert.csv CSV file has been created successfully.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void deleteTempFile() throws IOException {

		if (Files.exists(localFileDirPath)) {
			Files.newDirectoryStream(localFileDirPath).forEach(file -> {
				try {
					Files.delete(file);

				} catch (IOException ioex) {
					ioex.getMessage();
				}
			});
		}

	}

}
