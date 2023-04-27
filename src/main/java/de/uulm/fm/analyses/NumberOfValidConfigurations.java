package de.uulm.fm.analyses;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.dimacs.DimacsWriter;
import de.uulm.fm.util.BinaryRunner;
import de.uulm.fm.util.BinaryRunner.*;

import de.uulm.fm.util.FileUtils;

/**
 * 
 */
public class NumberOfValidConfigurations implements IFMAnalysis {


    private static final String LABEL = "NumberOfValidConfigurations";


    private static final String TEMPORARY_DIMACS_PATH = "temp.dimacs";


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
    public String getResult(IFeatureModel featureModel, FeatureModelFormula formula) {
		createTemporaryDimacs(formula);
		BinaryResult result = null;
		result = executeSolver(TEMPORARY_DIMACS_PATH, 30);
		if (result.status == Status.TIMEOUT) {
			return "-1";
		}
		if (result.status == Status.SOLVED) {
			return parseResult(result.stdout);
		}
		return "-2";
    }
	
	public static void createTemporaryDimacs(FeatureModelFormula formula) {
		final DimacsWriter dWriter = new DimacsWriter(formula.getCNF());
		final String dimacsContent = dWriter.write();
		FileUtils.writeContentToFile(TEMPORARY_DIMACS_PATH, dimacsContent);
    }
    
    public BinaryResult executeSolver(String dimacsPath, long timeout) {
		String command = buildCommand(dimacsPath);
		return BinaryRunner.runBinaryStatic(command, timeout);
	}
    
    private String buildCommand(String dimacsPath) {
		return BINARY_PATH + " -i " + dimacsPath + " -m counting";
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
			return "-2";
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
