package org.collection.fm.analyses;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.collection.fm.analyses.IFMAnalysis.Format;
import org.collection.fm.util.CnfTranslator;
import org.collection.fm.util.FMUtils;
import org.collection.fm.util.FileUtils;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;

public class AnalysisHandler {
    
    private final List<IFMAnalysis> analyses;

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
        return file.getPath() + "," + evaluateFeatureModel(FMUtils.readFeatureModel(file.getPath()), timeout);
    }

    public String evaluateDimacsFile(File file, int timeout, String inputPath) {
        return file.getPath() + "," + evaluateCNF(CnfTranslator.readDimacs(file.getPath()), timeout);
    }

    private String getCleanName(File file, String inputPath) {
        return FileUtils.getFileNameWithoutExtension(file.getAbsolutePath());
    }

    public String evaluateFeatureModel(IFeatureModel model, int timeout) {
        try(ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            StringBuilder csvRow = new StringBuilder();
            FeatureModelFormula formula = new FeatureModelFormula(model);
            for (IFMAnalysis analysis : analyses) {
                if (analysis instanceof NumberOfValidConfigurationsLog){
                    csvRow.append(analysis.getResult(model, formula, analysis.getMaxRuntime())).append(",");
                } else {
                    Future<String> result = executorService.submit(() -> analysis.getResult(model, formula, timeout));
                    try {
                        csvRow.append(result.get(analysis.getMaxRuntime(), TimeUnit.SECONDS)).append(",");
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException | TimeoutException e) {
                        result.cancel(true);
                        csvRow.append("?" + ",");
                        System.out.println(analysis.getLabel() + " ran out of time!");
                    }
                }
            }
            return csvRow.substring(0, csvRow.length() - 1) + "\n";
        }
    }

    public String evaluateCNF(Node node, int timeout) {
        StringBuilder csvRow = new StringBuilder();
        for (IFMAnalysis analysis: analyses) {
            if (analysis.supportsFormat(Format.CNF)) {
                csvRow.append(analysis.getResult(node)).append(",");
            }
        }
        return csvRow.substring(0, csvRow.length() - 1) + "\n";
    }

    public String getCsvHeader() {
        StringBuilder headerRow = new StringBuilder("model,");
        for (IFMAnalysis analysis : analyses) {
            headerRow.append(analysis.getLabel()).append(",");
        }

        return headerRow.substring(0, headerRow.length() - 1);
    }

    public String getFailRow() {
        StringBuilder failRow = new StringBuilder();
        for (int i = 0; i < analyses.size() - 2; i++) {
            failRow.append(",");
        }
        failRow.append("\n");
        return failRow.toString();
    }

}
