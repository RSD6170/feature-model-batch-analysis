package org.collection.fm.analyses;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.collection.fm.util.BinaryRunner;
import org.collection.fm.util.BinaryRunner.*;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

/**
 * 
 */
public class NumberOfValidConfigurationsLog implements IFMAnalysis {


    private static final String LABEL = "NumberOfValidConfigurationsLog";





    private static final String UNSAT_FLAG = "s 0";


    private final static String BINARY_PATH = "solver" + File.separator + "d4";

    @Override
    public String getLabel() {
        return LABEL;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula, int timeout) {
		BinaryResult result = executeSolver(timeout, formula);
		if (result.status == Status.TIMEOUT) {
			return "?";
		}
		if (result.status == Status.SOLVED) {
			return String.valueOf(parseResult(result.stdout).length());
		}
		return "?";
    }
	

    
    public BinaryResult executeSolver(long timeout, FeatureModelFormula formula) {
		return BinaryRunner.runSolverWithDir(this.buildCommand(), timeout, formula);
	}
    
    private Function<Path, String[]> buildCommand() {
		return (dimacsPath -> new String[]{BINARY_PATH,"-i",dimacsPath.toString(),"-m", "counting"});
    }
    

    public String parseResult(String output) {
		if (isUNSAT(output)) {
			return "0";
		}
		final Pattern pattern = Pattern.compile("^s \\d*", Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(output);
		String result = "";
		if (matcher.find()) {
			result = matcher.group();
		} else {
			return "?";
		}
		final String[] split = result.split(" ");
		return split[split.length - 1];
    }
    

    private boolean isUNSAT(String output) {
		return output.contains(UNSAT_FLAG);
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
