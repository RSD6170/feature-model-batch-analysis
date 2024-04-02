package org.collection.fm;

import org.checkerframework.checker.units.qual.A;
import org.collection.fm.analyses.*;
import org.collection.fm.handler.AnalysisStepHandler;
import org.collection.fm.handler.FeatureStepHandler;
import org.collection.fm.handler.SatzillaHandler;
import org.collection.fm.util.AnalysisCacher;
import org.collection.fm.util.AnalysisCategory;
import org.collection.fm.util.AnalysisStepsEnum;
import org.collection.fm.util.FMUtils;
import org.collection.fm.util.FileUtils;

import java.io.File;
import java.nio.file.Path;
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
        AnalysisStepHandler analysis = new AnalysisStepHandler(Path.of(""));



        FMUtils.installLibraries();
        if (args.length < 1) {
            System.err.println("""
                    Mandatory argument([0]): Input path
                    Optional Argument([1]): Analysis category: All (chosen if not set), Default, Simple, CNF, Satzilla
                    Optional Argument([2]): Output path results
                    Optional Argument([3]): Output path time
                    Optional Argument([4]): Output path runstatus
                    """);
            System.exit(-1);
        }

        EnumMap<AnalysisStepsEnum, Integer> enumMap = getEnumMapByCategory(args.length > 1 ? args[1] : "All", 60);

        analysis.initializeHandler(enumMap);

        List<File> files = FileUtils.getFileList(args[0]);

        analysis.handleFiles(files, args.length < 3 ? "result.csv" : args[1], args.length < 3 ? "time.csv" : args[2], args.length < 3 ? "runstatus.csv" : args[3]);
    }

    private static EnumMap<AnalysisStepsEnum, Integer> getEnumMapByCategory(String category, int timeout) {
        EnumMap<AnalysisStepsEnum, Integer> enumMap = new EnumMap<>(AnalysisStepsEnum.class);
        for (AnalysisStepsEnum analysis : AnalysisStepsEnum.values()) {
            for (AnalysisCategory categoryIt : analysis.getCategories()) {
                if (categoryIt.name().equals(category)) {
                    enumMap.put(analysis, timeout);
                    break;
                }
            }
        }

        return enumMap;
    }

}
