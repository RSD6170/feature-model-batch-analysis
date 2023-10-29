package org.collection.fm.handler;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.collection.fm.util.AnalysisStepsEnum;
import org.collection.fm.util.FMUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * Handles analysis of all {@link FeatureStepHandler}
 */
public class AnalysisStepHandler {

    private final List<FeatureStepHandler> featureSteps = new ArrayList<>();
    private final Path solverRelativePath;

    public AnalysisStepHandler(Path solverRelativePath) {
        this.solverRelativePath = solverRelativePath;
    }

    /**
     * Registers a {@link FeatureStepHandler}
     * @param featureStep {@link FeatureStepHandler} to be registered
     */
    public void registerFeatureStep(FeatureStepHandler featureStep) {
        featureSteps.add(featureStep);
    }

    /**
     * Analysises all files with registered {@link FeatureStepHandler}
     * @param files Files to be analyzed
     * @param outputResult Path for results csv file
     * @param outputTime Path for time csv file
     * @param outputStatus Path for runstatus csv file
     */
    public void handleFiles(List<File> files, String outputResult, String outputTime, String outputStatus){
        try(CSVPrinter resultWriter = new CSVPrinter(new BufferedWriter(new FileWriter(outputResult)), generateCSVFormatResult());
            CSVPrinter timeWriter = new CSVPrinter(new BufferedWriter(new FileWriter(outputTime)), generateCSVFormatStatus());
            CSVPrinter statusWriter = new CSVPrinter(new BufferedWriter(new FileWriter(outputStatus)), generateCSVFormatStatus());
            ExecutorService executorService = Executors.newSingleThreadExecutor()) {
            for (File file : files) {
                System.out.println("Handle file "+file.toString());
                List<FeatureStep> featureStep = handleFile(file, executorService);
                if (featureStep == null) continue;
                resultWriter.print(file.toString());
                resultWriter.printRecord(featureStep.stream().map(FeatureStep::values).flatMap(Collection::stream));

                timeWriter.print(file.toString());
                timeWriter.print("1");
                timeWriter.printRecord(featureStep.stream().map(e -> e.featureStatus().equals(FeatureStatus.crash) ? "?" : String.format("%f", e.usedRuntime())));

                statusWriter.print(file.toString());
                statusWriter.print("1");
                statusWriter.printRecord(featureStep.stream().map(FeatureStep::featureStatus));

                resultWriter.flush();
                timeWriter.flush();
                statusWriter.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Analysis a single File
     * @param file File to analyse
     * @return List of analysis results as {@link FeatureStep}s
     */
    public List<FeatureStep> getSingleAnalysis(File file) {
        try(ExecutorService executorService = Executors.newSingleThreadExecutor()){
            return handleFile(file, executorService);
        }
    }

    private List<FeatureStep> handleFile(File file, ExecutorService executorService) {
        IFeatureModel featureModel = FMUtils.readFeatureModel(file.getPath());
        if (featureModel == null) return null;
        FeatureModelFormula formula = new FeatureModelFormula(featureModel);
        return featureSteps.stream().map(e -> e.evaluateFeatureStep(executorService, featureModel, formula, file.toPath(), solverRelativePath)).toList();
    }

    private CSVFormat generateCSVFormatResult(){
        return CSVFormat.Builder.create().setHeader(
                Stream.concat(Stream.of("File"), featureSteps.stream().flatMap(e -> e.getCSVHeader().stream())).toArray(String[]::new)
        ).build();
    }

    private CSVFormat generateCSVFormatStatus(){
        return CSVFormat.Builder.create().setHeader(
                Stream.concat(Stream.of("File", "Run"),featureSteps.stream().map(FeatureStepHandler::getName)).toArray(String[]::new)
        ).build();
    }


    public void initializeHandler(EnumMap<AnalysisStepsEnum, Integer> selectionMap){
        selectionMap.entrySet().stream().filter(e -> e.getValue() > 0).map(e -> e.getKey().getFeatureStepHandler(e.getValue())).forEachOrdered(this::registerFeatureStep);
    }


}
