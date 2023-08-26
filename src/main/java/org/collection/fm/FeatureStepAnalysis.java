package org.collection.fm;

import org.checkerframework.checker.units.qual.A;
import org.collection.fm.analyses.*;
import org.collection.fm.handler.AnalysisStepHandler;
import org.collection.fm.handler.FeatureStepHandler;
import org.collection.fm.handler.SatzillaHandler;
import org.collection.fm.util.AnalysisCacher;
import org.collection.fm.util.FMUtils;
import org.collection.fm.util.FileUtils;

import java.io.File;
import java.util.List;

public class FeatureStepAnalysis {

    public static void main(String[] args) {
        AnalysisStepHandler analysis = new AnalysisStepHandler();



        FMUtils.installLibraries();
        if (args.length < 1) {
            System.out.println("""
                    Mandatory argument([0]): Input path
                    Optional Argument([1]): Output path results
                    Optional Argument([2]): Output path time
                    Optional Argument([3]): Output path runstatus
                    """);
            return;
        }

        initializeHandler(analysis);

        List<File> files = FileUtils.getFileList(args[0]);

        analysis.handleFiles(files, args[0], args.length == 1 ? "result.csv" : args[1], args.length == 1 ? "time.csv" : args[2], args.length == 1 ? "runstatus.csv" : args[3]);
    }

    public static void initializeHandler(AnalysisStepHandler stepHandler){

        FeatureStepHandler featureStepHandler = new FeatureStepHandler(60, "Feature-basic");
        featureStepHandler.addAnalysis(new NumberOfFeatures());
        featureStepHandler.addAnalysis(new NumberOfLeafFeatures());
        featureStepHandler.addAnalysis(new NumberOfTopFeatures());
        stepHandler.registerFeatureStep(featureStepHandler);

        featureStepHandler = new FeatureStepHandler(60, "Feature-constraints");
        featureStepHandler.addAnalysis(new NumberOfConstraints());
        featureStepHandler.addAnalysis(new AverageConstraintSize());
        featureStepHandler.addAnalysis(new CtcDensity());
        featureStepHandler.addAnalysis(new FeaturesInConstraintsDensity());
        stepHandler.registerFeatureStep(featureStepHandler);

        featureStepHandler = new FeatureStepHandler(60, "Feature-tree");
        featureStepHandler.addAnalysis(new TreeDepth());
        featureStepHandler.addAnalysis(new AverageNumberOfChilden());
        stepHandler.registerFeatureStep(featureStepHandler);

        featureStepHandler = new FeatureStepHandler(60, "Feature-clause");
        featureStepHandler.addAnalysis(new NumberOfClauses());
        featureStepHandler.addAnalysis(new NumberOfLiterals());
        featureStepHandler.addAnalysis(new ClauseDensity());
        stepHandler.registerFeatureStep(featureStepHandler);

        featureStepHandler = new FeatureStepHandler(60, "Feature-dense");
        featureStepHandler.addAnalysis(new ConnectivityDensity());
        stepHandler.registerFeatureStep(featureStepHandler);

        featureStepHandler = new FeatureStepHandler(60, "Feature-core");
        featureStepHandler.addAnalysis(new VoidModel());

        AnalysisCacher analysisCacher = new AnalysisCacher();

        featureStepHandler.addAnalysis(new NumberOfCoreFeatures(analysisCacher));
        featureStepHandler.addAnalysis(new NumberOfDeadFeatures(analysisCacher));
        featureStepHandler.addAnalysis(new RatioOfOptionalFeatures(analysisCacher));
        stepHandler.registerFeatureStep(featureStepHandler);

        featureStepHandler = new FeatureStepHandler(60, "Feature-valid");
        featureStepHandler.addAnalysis(new NumberOfValidConfigurationsLog());
        stepHandler.registerFeatureStep(featureStepHandler);

        featureStepHandler = new FeatureStepHandler(60, "Feature-cyclo");
        featureStepHandler.addAnalysis(new SimpleCyclomaticComplexity());
        featureStepHandler.addAnalysis(new IndependentCyclomaticComplexity());
        stepHandler.registerFeatureStep(featureStepHandler);

        stepHandler.registerFeatureStep(new SatzillaHandler(60, SatzillaHandler.SatzillaTypes.BASE));
        stepHandler.registerFeatureStep(new SatzillaHandler(60, SatzillaHandler.SatzillaTypes.SP));
        stepHandler.registerFeatureStep(new SatzillaHandler(60, SatzillaHandler.SatzillaTypes.DIA));
        stepHandler.registerFeatureStep(new SatzillaHandler(60, SatzillaHandler.SatzillaTypes.CL));
        stepHandler.registerFeatureStep(new SatzillaHandler(60, SatzillaHandler.SatzillaTypes.UNIT));
        stepHandler.registerFeatureStep(new SatzillaHandler(60, SatzillaHandler.SatzillaTypes.LS));
        stepHandler.registerFeatureStep(new SatzillaHandler(60, SatzillaHandler.SatzillaTypes.LOBJOIS));
        stepHandler.registerFeatureStep(new SatzillaHandler(60, SatzillaHandler.SatzillaTypes.LP));
    }
}
