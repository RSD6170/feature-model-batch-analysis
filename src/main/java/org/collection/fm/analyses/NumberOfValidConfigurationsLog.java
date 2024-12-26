package org.collection.fm.analyses;

import java.io.File;
import java.nio.file.Path;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.collection.fm.util.BinaryRunner;
import org.collection.fm.util.BinaryRunner.*;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

/**
 * 
 */
public class NumberOfValidConfigurationsLog implements IFMAnalysis {

	private static final Logger LOGGER = LogManager.getLogger();

    private static final String LABEL = "NumberOfValidConfigurationsLog";
	private static final String BINARY_PATH = "./sharpSAT";

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout, Path solverRelativePath) throws InterruptedException {
		BinaryResult result = executeSolver(timeout, formula, solverRelativePath);
		if (result.status == Status.TIMEOUT) {
			return "?";
		}
		if (result.status == Status.SOLVED) {
			return String.valueOf(parseResult(result.stdout));
		}
		return "?";
    }
	

    
    public BinaryResult executeSolver(int timeout, FeatureModelFormula formula, Path solverRelativePath) throws InterruptedException {
		int memoryMB = 4000;
		try {
			String slurmMem = System.getenv("SLURM_MEM_PER_NODE");
			if (slurmMem != null) memoryMB = Integer.parseInt(slurmMem);
		} catch (Exception e) {
			LOGGER.warn("FMBA Valid Count: Error in SLURM-specific memory reading", e);
		}
		memoryMB = memoryMB / 2 - 500;
		LOGGER.info("Running Model Counter with {} MB memory", memoryMB);
		return BinaryRunner.runSolverWithDir(this.buildCommand(timeout, memoryMB), timeout, formula, solverRelativePath, solverRelativePath.resolve("solver").toFile());
	}
    
    private BiFunction<Path, Path, String[]> buildCommand(int timeout, int memoryMB) {
		return ((solverRelativePath, dimacsPath) -> new String[]{
				BINARY_PATH,
				"-decot",
				String.valueOf(timeout / 30),
				"-decow",
				"100",
				"-tmpdir",
				System.getProperty("java.io.tmpdir"),
				"-cs",
				String.valueOf(memoryMB),
				dimacsPath.toString()
		});
    }
    

    public String parseResult(String output) {
		try {
			if (output.contains("s SATISFIABLE")) {
				return output.lines().filter(e -> e.startsWith("c s log10-estimate")).map(e -> e.split(" ", 4)[3]).findAny().orElseThrow();
			} else if (output.contains("s UNSATISFIABLE")) {
				return "-inf";
			} else throw new IllegalStateException("Unsupported output: " + output);
		} catch (Exception e) {
			LOGGER.warn("Unknown output of model counter: {}", output, e);
			return "?";
		}
    }


	@Override
	public String getResult(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportsFormat(Format format) {
		// TODO Auto-generated method stub
		return false;
	}
}
