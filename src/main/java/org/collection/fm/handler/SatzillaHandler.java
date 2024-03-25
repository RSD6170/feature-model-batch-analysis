package org.collection.fm.handler;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import org.collection.fm.analyses.IFMAnalysis;
import org.collection.fm.util.BinaryRunner;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;

public class SatzillaHandler extends FeatureStepHandler{

    public enum SatzillaTypes {

        BASE("-base", new String[]{"nvarsOrig", "nclausesOrig", "nvars", "nclauses", "reducedVars", "reducedClauses", "Pre-featuretime", "vars-clauses-ratio", "POSNEG-RATIO-CLAUSE-mean", "POSNEG-RATIO-CLAUSE-coeff-variation", "POSNEG-RATIO-CLAUSE-min", "POSNEG-RATIO-CLAUSE-max", "POSNEG-RATIO-CLAUSE-entropy", "VCG-CLAUSE-mean", "VCG-CLAUSE-coeff-variation", "VCG-CLAUSE-min", "VCG-CLAUSE-max", "VCG-CLAUSE-entropy", "UNARY", "BINARY+", "TRINARY+", "Basic-featuretime", "VCG-VAR-mean", "VCG-VAR-coeff-variation", "VCG-VAR-min", "VCG-VAR-max", "VCG-VAR-entropy", "POSNEG-RATIO-VAR-mean", "POSNEG-RATIO-VAR-stdev", "POSNEG-RATIO-VAR-min", "POSNEG-RATIO-VAR-max", "POSNEG-RATIO-VAR-entropy", "HORNY-VAR-mean", "HORNY-VAR-coeff-variation", "HORNY-VAR-min", "HORNY-VAR-max", "HORNY-VAR-entropy", "horn-clauses-fraction", "VG-mean", "VG-coeff-variation", "VG-min", "VG-max", "KLB-featuretime", "CG-mean", "CG-coeff-variation", "CG-min", "CG-max", "CG-entropy", "cluster-coeff-mean", "cluster-coeff-coeff-variation", "cluster-coeff-min", "cluster-coeff-max", "cluster-coeff-entropy","CG-featuretime"}),
        SP("-sp", new String[]{"SP-bias-mean", "SP-bias-coeff-variation", "SP-bias-min", "SP-bias-max", "SP-bias-q90", "SP-bias-q10", "SP-bias-q75", "SP-bias-q25", "SP-bias-q50", "SP-unconstraint-mean", "SP-unconstraint-coeff-variation", "SP-unconstraint-min", "SP-unconstraint-max", "SP-unconstraint-q90", "SP-unconstraint-q10", "SP-unconstraint-q75", "SP-unconstraint-q25", "SP-unconstraint-q50","sp-featuretime"}),
        DIA("-dia", new String[]{"DIAMETER-mean", "DIAMETER-coeff-variation", "DIAMETER-min", "DIAMETER-max", "DIAMETER-entropy", "DIAMETER-featuretime"}),
        CL("-cl", new String[]{"cl-num-mean", "cl-num-coeff-variation", "cl-num-min", "cl-num-max", "cl-num-q90", "cl-num-q10", "cl-num-q75", "cl-num-q25", "cl-num-q50", "cl-size-mean", "cl-size-coeff-variation", "cl-size-min", "cl-size-max", "cl-size-q90", "cl-size-q10", "cl-size-q75", "cl-size-q25", "cl-size-q50", "cl-featuretime"}),
        UNIT("-unit", new String[]{"vars-reduced-depth-1", "vars-reduced-depth-4", "vars-reduced-depth-16", "vars-reduced-depth-64", "vars-reduced-depth-256", "unit-featuretime"}),
        LS("-ls", new String[]{"saps_BestSolution_Mean", "saps_BestSolution_CoeffVariance", "saps_FirstLocalMinStep_Mean", "saps_FirstLocalMinStep_CoeffVariance", "saps_FirstLocalMinStep_Median", "saps_FirstLocalMinStep_Q.10", "saps_FirstLocalMinStep_Q.90", "saps_BestAvgImprovement_Mean", "saps_BestAvgImprovement_CoeffVariance", "saps_FirstLocalMinRatio_Mean", "saps_FirstLocalMinRatio_CoeffVariance", "ls-saps-featuretime", "gsat_BestSolution_Mean", "gsat_BestSolution_CoeffVariance", "gsat_FirstLocalMinStep_Mean", "gsat_FirstLocalMinStep_CoeffVariance", "gsat_FirstLocalMinStep_Median", "gsat_FirstLocalMinStep_Q.10", "gsat_FirstLocalMinStep_Q.90", "gsat_BestAvgImprovement_Mean", "gsat_BestAvgImprovement_CoeffVariance", "gsat_FirstLocalMinRatio_Mean", "gsat_FirstLocalMinRatio_CoeffVariance", "ls-gsat-featuretime"}),
        LOBJOIS("-lobjois", new String[]{"lobjois-mean-depth-over-vars", "lobjois-log-num-nodes-over-vars", "lobjois-featuretime"}),
        LP("-lp", new String[]{"LP_OBJ","LPSLack-mean","LPSLack-coeff-variation","LPSLack-min","LPSLack-max","lpIntRatio", "lpTIME"});

        public final String parameter;
        public final String[] header;


        SatzillaTypes(String parameter, String[] header){
            this.parameter = parameter;
            this.header = header;
        }

    }

    public final SatzillaTypes satzillaType;

    public SatzillaHandler(int runtimeLimit, SatzillaTypes satzillaType) {
        super(runtimeLimit, "Satzilla-"+satzillaType.name());
        this.satzillaType = satzillaType;
    }

    @Override
    public void addAnalysis(IFMAnalysis analysis) {
        throw new Error("Wrong usage! Use FeatureStepHandler instead!");
    }

    @Override
    public List<String> getCSVHeader() {
        return Arrays.asList(satzillaType.header);
    }

    @Override
    public FeatureStep evaluateFeatureStep(ExecutorService executorService, IFeatureModel featureModel, FeatureModelFormula formula, Path file, Path solverRelativePath) throws InterruptedException {
        LocalDateTime before = LocalDateTime.now();
        BinaryRunner.BinaryResult binaryResult = BinaryRunner.runSolverWithDir(this::getCommand, super.runtimeLimit, formula, solverRelativePath);

        try {
            return switch (binaryResult.status) {
                case SOLVED -> {
                    Optional<String> lastLine = binaryResult.stdout.lines().reduce((first, second) -> second);
                    List<String> results = parseSatzillaOutput(lastLine.orElseThrow());
                    yield new FeatureStep(name, file, results, Duration.between(before, LocalDateTime.now()).toMillis() / 1000d, FeatureStatus.ok);
                }
                case MEMORY_LIMIT_REACHED -> new FeatureStep(name, file, Collections.nCopies(satzillaType.header.length, "?"), super.runtimeLimit, FeatureStatus.memout);
                case TIMEOUT -> {
                    System.out.println("Timeout in Satzilla "+ satzillaType);
                    yield new FeatureStep(name, file, Collections.nCopies(satzillaType.header.length, "?"), super.runtimeLimit, FeatureStatus.timeout);
                }
                case UNEXPECTED -> throw new Exception();
            };
        }catch (Exception e){
            System.out.println("Error in Satzilla "+ satzillaType);
            return new FeatureStep(name, file, Collections.nCopies(satzillaType.header.length, "?"), super.runtimeLimit, FeatureStatus.crash);
        }
    }

    private List<String> parseSatzillaOutput(String stdout) throws NumberFormatException {
        List<String> retValue = List.of(stdout.split(","));
        for (String s : retValue) {
            if (!s.contains("nan")) Double.parseDouble(s);
        }
        return retValue.stream().map(e -> e.contains("nan") ? "?" : e).toList();
    }

    private String[] getCommand(Path solverRelativePath, Path path){
        String[] retValue = new String[3];
        retValue[0] = solverRelativePath.resolve("solver" + File.separator + "SATZilla_2012" + File.separator + "features").toString();
        retValue[1] = satzillaType.parameter;
        retValue[2] = path.toString();
        return retValue;
    }
}
