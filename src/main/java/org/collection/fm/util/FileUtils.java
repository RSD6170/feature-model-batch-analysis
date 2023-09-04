package org.collection.fm.util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtils {

	private FileUtils(){

	}
	
	public static List<File> getFilesInDirectoryAndSubdirectories(String directoryPath) {
		List<File> files = new ArrayList<>();
		addDirectoryFiles(directoryPath, files);
		Comparator<File> cmp = (f1, f2) -> FileUtils.getFileId(f1).toLowerCase().compareTo(FileUtils.getFileId(f2).toLowerCase());
		files.sort(cmp);
		return files;
		
	}

		
	public static List<File> getFilesInDirectoryAndSubdirectoriesWithExtensions(String directoryPath, List<String> extensions) {
		List<File> files = new ArrayList<>();
		addDirectoryFilesWithExtension(directoryPath, files, extensions);
		Comparator<File> cmp = (f1, f2) -> FileUtils.getFileId(f1).toLowerCase().compareTo(FileUtils.getFileId(f2).toLowerCase());
		files.sort(cmp);
		return files;
		
	}
	
	private static void addDirectoryFiles(String directoryPath, List<File> files) {
		File directory = new File(directoryPath);
		File[] fileList = directory.listFiles();
		if (fileList != null) {
	        for (File file : fileList) {      
	            if (file.isFile()) {
	                files.add(file);
	            } else if (file.isDirectory()) {
	                addDirectoryFiles(file.getAbsolutePath(), files);
	            }
	        }
		}
	}


	private static void addDirectoryFilesWithExtension(String directoryPath, List<File> files, List<String> allowedExtensions) {
		File directory = new File(directoryPath);
		File[] fileList = directory.listFiles();
		if (fileList != null) {
	        for (File file : fileList) {      
	            if (file.isFile() && allowedExtensions.contains(getExtension(file.getPath()))) {
	                files.add(file);
	            } else if (file.isDirectory()) {
	                addDirectoryFilesWithExtension(file.getAbsolutePath(), files, allowedExtensions);
	            }
	        }
		}
	}

	public static List<File> getFileList(String inputPath) {
		return getFileListWithExtensions(inputPath, null);
	}


	/**
	 * Returns all files in a directory or a single file depending on whether the input is a directory
	 * @param inputPath
	 * @return
	 */
	public static List<File> getFileListWithExtensions(String inputPath, List<String> extensions) {
		File inputFile = new File(inputPath);
		if (!inputFile.exists()) {
			System.out.println("File not found:" + inputPath);
			return Collections.emptyList();
		}
		if (inputFile.isDirectory()) {
			if (extensions == null) {
				return FileUtils.getFilesInDirectoryAndSubdirectories(inputPath);
			} else {
				return FileUtils.getFilesInDirectoryAndSubdirectoriesWithExtensions(inputPath, extensions);
			}

		} else {
			List<File> file = new ArrayList<>();
			file.add(inputFile);
			return file;
		}

	}
	
	public static void writeContentToFile(String path, String content) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void createDirIfItDoesntExist(String dirName) {
		File dirs = new File(dirName);
		if (!dirs.exists()) dirs.mkdirs();
	}
	
	public static void writeContentToFileAndCreateDirs(String path, String content, boolean overwrite) {
		try {
			if (path.contains(File.separator)) {
				String dirStrings = path.substring(0, path.lastIndexOf(File.separator));
				createDirIfItDoesntExist(dirStrings);
			}
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, !overwrite))) {
				writer.write(content);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static String getExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i+1);
		}

		return extension;

	}
	

	public static String getFileId(File file) {
		String parent = file.getParent();
		return parent.substring(parent.lastIndexOf(File.separator) + 1) + File.separator + file.getName();
	}
	
}
