package org.collection.fm;

import org.checkerframework.checker.units.qual.A;
import org.collection.fm.analyses.*;
import org.collection.fm.handler.AnalysisStepHandler;
import org.collection.fm.handler.FeatureStepHandler;
import org.collection.fm.handler.SatzillaHandler;
import org.collection.fm.util.AnalysisCacher;
import org.collection.fm.util.AnalysisStepsEnum;
import org.collection.fm.util.FMUtils;
import org.collection.fm.util.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

/**
 * Entry point for command line based analysis.
 *
 * Uses all feature steps with 60 secs timeout.
 */
public class FeatureStepAnalysis {

    public static void main(String[] args) {
        AnalysisStepHandler analysis = new AnalysisStepHandler();



        FMUtils.installLibraries();
        if (args.length < 1) {
            System.err.println("""
                    Mandatory argument([0]): Input path
                    Optional Argument([1]): Output path results
                    Optional Argument([2]): Output path time
                    Optional Argument([3]): Output path runstatus
                    """);
            System.exit(-1);
        }

        EnumMap<AnalysisStepsEnum, Integer> enumMap = new EnumMap<>(AnalysisStepsEnum.class);

        Arrays.stream(AnalysisStepsEnum.values()).forEach(e -> enumMap.put(e, 60));

        analysis.initializeHandler(enumMap);

        List<File> files = FileUtils.getFileList(args[0]);

        analysis.handleFiles(files, args.length == 1 ? "result.csv" : args[1], args.length == 1 ? "time.csv" : args[2], args.length == 1 ? "runstatus.csv" : args[3]);
    }

}
