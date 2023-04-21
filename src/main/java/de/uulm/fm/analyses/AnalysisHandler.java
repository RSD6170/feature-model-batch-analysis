package de.uulm.fm.analyses;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.uulm.fm.analyses.IFMAnalysis.Format;
import de.uulm.fm.util.CnfTranslator;
import de.uulm.fm.util.FMUtils;
import de.uulm.fm.util.FileUtils;

public class AnalysisHandler {
    
    private List<IFMAnalysis> analyses;

    public AnalysisHandler() {
        this.analyses = new ArrayList<>();
    }

    public void registerAnalysis(IFMAnalysis analysis) {
        analyses.add(analysis);
    }


    public String evaluateFmFile(File file, int timeout, String inputPath) {
        IFeatureModel featureModel = FMUtils.readFeatureModel(file.getPath());
        if (featureModel == null) {
            return file.getPath() + getFailRow();
        }
        return file.getPath() + ";" + evaluateFeatureModel(FMUtils.readFeatureModel(file.getPath()), timeout);
    }

    public String evaluateDimacsFile(File file, int timeout, String inputPath) {
        return file.getPath() + ";" + evaluateCNF(CnfTranslator.readDimacs(file.getPath()), timeout);
    }

    private String getCleanName(File file, String inputPath) {
        return FileUtils.getFileNameWithoutExtension(file.getAbsolutePath());
    }

    public String evaluateFeatureModel(IFeatureModel model, int timeout) {
        String csvRow = "";
        FeatureModelFormula formula = new FeatureModelFormula(model);
        for (IFMAnalysis analysis: analyses) {
            csvRow += analysis.getResult(model, formula) + ";";
        }
        return csvRow.substring(0, csvRow.length() - 1) + "\n";
    }

    public String evaluateCNF(Node node, int timeout) {
        String csvRow = "";
        for (IFMAnalysis analysis: analyses) {
            if (analysis.supportsFormat(Format.CNF)) {
                csvRow += analysis.getResult(node) + ";";
            }
        }
        return csvRow.substring(0, csvRow.length() - 1) + "\n";
    }

    public String getCsvHeader() {
        String headerRow = "model;";
        for (IFMAnalysis analysis : analyses) {
            headerRow += analysis.getLabel() + ";";
        }

        return headerRow.substring(0, headerRow.length() - 1) + "\n";
    }

    public String getFailRow() {
        String failRow = "";
        for (int i = 0; i < analyses.size() - 2; i++) {
            failRow = failRow + ";";
        }
        failRow = failRow + "\n";
        return failRow;
    }

}
