package de.uulm.fm.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileUtils {

	public static String readFile(String path) {
		try {
			return new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static String getFileNameWithoutExtension(String filePath) {
		if (filePath.contains("/"))  {
			String[] split = filePath.split("/");
			filePath = split[split.length - 1];
		}
		if (filePath.contains(".")) filePath = filePath.substring(0, filePath.lastIndexOf('.'));
		return filePath;
	}
	
	public static void deleteFile(String filePath) {
		try {
			Files.deleteIfExists(new File(filePath).toPath());
		} catch (IOException e) {
			System.out.println(filePath + " could not be deleted.");
		}
	}
	
	public static List<File> getFilesInDirectoryAndSubdirectories(String directoryPath) {
		List<File> files = new ArrayList<File>();
		addDirectoryFiles(directoryPath, files);
		Comparator<File> cmp = new Comparator<File>() {
			public int compare(File f1, File f2) {
				return FileUtils.getFileId(f1).toLowerCase().compareTo(FileUtils.getFileId(f2).toLowerCase());
	        }
		};
	       
		Collections.sort(files, cmp);
		return files;
		
	}

		
	public static List<File> getFilesInDirectoryAndSubdirectoriesWithExtensions(String directoryPath, List<String> extensions) {
		List<File> files = new ArrayList<File>();
		addDirectoryFilesWithExtension(directoryPath, files, extensions);
		Comparator<File> cmp = new Comparator<File>() {
			public int compare(File f1, File f2) {
				return FileUtils.getFileId(f1).toLowerCase().compareTo(FileUtils.getFileId(f2).toLowerCase());
	        }
		};
	       
		Collections.sort(files, cmp);
		return files;
		
	}
	
	public static List<String> getFileNames(List<File> files) {
		return files.stream().map(f -> f.getPath()).collect(Collectors.toList());
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

	/**
	 * 
	 * @param inputPath
	 * @return
	 */
	public static List<File> getFileList(String inputPath) {
		return getFileListWithExtensions(inputPath, null);
	}

	public static List<File> getFileListWithExtension(String inputPath, String extension) {
		List<String> extensions = new ArrayList<>();
		extensions.add(extension);
		return getFileListWithExtensions(inputPath, extensions);
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
			return null;
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
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			writer.write(content);
			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public static void appendContentToFile(String path, String content) {
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
			writer.write(content);
			writer.close();
		} catch (final IOException e) {
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
			final BufferedWriter writer = new BufferedWriter(new FileWriter(path, !overwrite));
			writer.write(content);
			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getContentOfFile(String path) {
		String content = "";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String line = "";
			while ((line = reader.readLine()) != null) {
				content += line + "\n";
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
	
	public static List<String[]> readCsvFile(String path, String delimiter) throws FileNotFoundException {
        BufferedReader reader;
        List<String[]> rows = new ArrayList<>();
		reader = new BufferedReader(new FileReader(path));
        String currLine;
		try {
			while ((currLine = reader.readLine()) != null) {
				rows.add(currLine.split(delimiter));
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rows;
	}


	public static String getExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i+1);
		}

		return extension;

	}
	
	
	public static void createCsvFromMap(Map<String, String> map, String path) {
		String content = "";
		for (String key : map.keySet()) {
			content += key + ";" + map.get(key) +  "\n";
		}
		content = content.substring(0, content.length() - 1);
		FileUtils.writeContentToFile(path, content);
	}
	
	
	public static String mergeIterableToString(Iterable<String> it, String delimiter) {
		String result = "";
		for (String ele : it) {
			result += ele + delimiter;
		}
		
		return result.substring(0, result.length() -1);	
	}
	
	public static String mergeDoubleIterableToString(Iterable<Double> it, String delimiter) {
		String result = "";
		for (Double ele : it) {
			if (ele % 1 == 0) {
				result += Math.round(ele) + delimiter;
			} else {
				result += ele + delimiter;
			}

		}
		
		return result.substring(0, result.length() -1);	
	}
	
	public static String getFileId(File file) {
		String parent = file.getParent();
		return parent.substring(parent.lastIndexOf(File.separator) + 1) + File.separator + file.getName();
	}
	
}
