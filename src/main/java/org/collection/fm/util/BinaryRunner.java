package org.collection.fm.util;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.io.dimacs.DimacsWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


public class BinaryRunner {

	
	public static final String TIMEOUT_REACHED = "TIMEOUT!!";
	public static final  String MONITORING_PREFIX = "/usr/bin/time -v ";
	public static final String SCRIPT_PREFIX = "/bin/sh";
	public static final String MEMORY_ULIMIT_PREFIX = "ulimit -v ";

	private static final String TEMPORARY_DIMACS_PATH = "temp.dimacs";

	public enum Status {
		SOLVED, MEMORY_LIMIT_REACHED, TIMEOUT, UNEXPECTED
	}

	public static class BinaryResult {
		public String stdout;
		public Status status;
		
		public BinaryResult(String stdout, Status status) {
			this.stdout = stdout;
			this.status = status;
		}
	}


	public static BinaryResult runBinaryStatic(String[] commands, long timeout, File workingDir) throws InterruptedException {
		Process ps = null;
        try {
            ps = new ProcessBuilder(commands).redirectErrorStream(true).directory(workingDir).start();
            long pid = ps.pid();
            if (!ps.waitFor(timeout, TimeUnit.SECONDS)) {
                killProcesses(ps.toHandle());

                return new BinaryResult("", Status.TIMEOUT);
            }

            StringBuilder val = new StringBuilder();
            String line;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()))) {
                while ((line = in.readLine()) != null) {
                    val.append(line).append("\n");
                }
            }
            return new BinaryResult(val.toString(), Status.SOLVED);
        } catch (IOException e) {
			if (ps != null)	killProcesses(ps.toHandle());
            return new BinaryResult("", Status.UNEXPECTED);
        } catch (InterruptedException e) {
			if (ps != null)	killProcesses(ps.toHandle());
            throw new InterruptedException();
        }
    }

	private static void killProcesses(ProcessHandle ps)  {
        ps.descendants().forEach(BinaryRunner::killProcesses);
		ps.destroy();
	}

	public static BinaryResult runSolverWithDir(BiFunction<Path, Path, String[]> commands, long timeout, FeatureModelFormula formula, Path solverRelativePath) throws InterruptedException {
		return runSolverWithDir(commands, timeout, formula, solverRelativePath, new File("."));
	}

	public static BinaryResult runSolverWithDir(BiFunction<Path, Path, String[]> commands, long timeout, FeatureModelFormula formula, Path solverRelativePath, File workingDir) throws InterruptedException {
		Path dir = null;
		try {
			dir = createTemporaryDimacs(formula);
			BinaryResult result = runBinaryStatic(commands.apply(solverRelativePath, dir.resolve(TEMPORARY_DIMACS_PATH)), timeout, workingDir);
			cleanUpTemp(dir);
			return result;
		} catch (IOException e) {
			return new BinaryResult("", Status.UNEXPECTED);
		} catch (InterruptedException e) {
			if (dir != null) cleanUpTemp(dir);
			throw new InterruptedException();
        }
    }

	private static Path createTemporaryDimacs(FeatureModelFormula formula) throws IOException {
		Path tempDir = Files.createTempDirectory("sat");
		String cnfPath = tempDir.resolve(TEMPORARY_DIMACS_PATH).toString();
		final DimacsWriter dWriter = new DimacsWriter(formula.getCNF());
		final String dimacsContent = dWriter.write();
		FileUtils.writeContentToFile(cnfPath, dimacsContent);

		return tempDir;
	}

	private static void cleanUpTemp(Path path){
		File[] files = path.toFile().listFiles();
		if (files != null) Arrays.stream(files).forEach(File::delete);
		path.toFile().deleteOnExit();
	}
}
