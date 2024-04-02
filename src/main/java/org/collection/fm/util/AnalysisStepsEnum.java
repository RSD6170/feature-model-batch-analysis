package org.collection.fm.util;

import org.collection.fm.analyses.*;
import org.collection.fm.handler.FeatureStepHandler;
import org.collection.fm.handler.SatzillaHandler;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum AnalysisStepsEnum {

    FeatureBasic("Feature-basic"),
    FeatureConstraints("Feature-constraints"),
    FeatureTree("Feature-tree"),
    FeatureClause("Feature-clause"),
    FeatureConstraintRedundancy("Feature-clause-redundancy"),
    FeatureDense("Feature-dense"),
    FeatureCore("Feature-core"),
    FeatureValid("Feature-valid"),
    FeatureCyclo("Feature-cyclo"),
    SatzillaBASE("Satzilla-BASE"),
    SatzillaSP("Satzilla-SP"),
    SatzillaDIA("Satzilla-DIA"),
    SatzillaCL("Satzilla-CL"),
    SatzillaUNIT("Satzilla-UNIT"),
    SatzillaLS("Satzilla-LS"),
    SatzillaLOBJOIS("Satzilla-LOBJOIS"),
    SatzillaLP("Satzilla-LP");

    private final String name;

    public String getName(){
        return name;
    }

    AnalysisStepsEnum(String name){
        this.name = name;
    }

    public static AnalysisStepsEnum getIgnoreCase(String string) throws NoSuchElementException {
        return Arrays.stream(AnalysisStepsEnum.values()).filter(e -> e.toString().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

    public FeatureStepHandler getFeatureStepHandler(int timeout) {
        AnalysisCacher analysisCacher = new AnalysisCacher();
        return switch (this) {
            case FeatureBasic -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, this.getName());
                featureStepHandler.addAnalysis(new NumberOfFeatures());
                featureStepHandler.addAnalysis(new NumberOfLeafFeatures());
                featureStepHandler.addAnalysis(new NumberOfTopFeatures());
                yield featureStepHandler;
            }
            case FeatureConstraints -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, this.getName());
                featureStepHandler.addAnalysis(new NumberOfConstraints());
                featureStepHandler.addAnalysis(new AverageConstraintSize());
                featureStepHandler.addAnalysis(new CtcDensity());
                featureStepHandler.addAnalysis(new FeaturesInConstraintsDensity());
                yield featureStepHandler;
            }
            case FeatureConstraintRedundancy -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, this.getName());
                featureStepHandler.addAnalysis(new NumberOfTautologies(analysisCacher));
                featureStepHandler.addAnalysis(new NumberOfRedundantConstraints(analysisCacher));
                yield featureStepHandler;
            }
            case FeatureTree -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, this.getName());
                featureStepHandler.addAnalysis(new TreeDepth());
                featureStepHandler.addAnalysis(new AverageNumberOfChilden());
                featureStepHandler.addAnalysis(new NumberOfAlternatives());
                featureStepHandler.addAnalysis(new NumberOfOrs());
                yield featureStepHandler;
            }
            case FeatureClause -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, this.getName());
                featureStepHandler.addAnalysis(new NumberOfClauses());
                featureStepHandler.addAnalysis(new NumberOfLiterals());
                featureStepHandler.addAnalysis(new NumberOfUnitClauses());
                featureStepHandler.addAnalysis(new NumberOfTwoClauses());
                featureStepHandler.addAnalysis(new ClauseDensity());
                yield featureStepHandler;
            }
            case FeatureDense -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, this.getName());
                featureStepHandler.addAnalysis(new ConnectivityDensity());
                yield featureStepHandler;
            }
            case FeatureCore -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, this.getName());
                featureStepHandler.addAnalysis(new VoidModel());
                featureStepHandler.addAnalysis(new NumberOfCoreFeatures(analysisCacher));
                featureStepHandler.addAnalysis(new NumberOfDeadFeatures(analysisCacher));
                featureStepHandler.addAnalysis(new RatioOfOptionalFeatures(analysisCacher));
                featureStepHandler.addAnalysis(new NumberOfFalseOptionalFeatures(analysisCacher));
                featureStepHandler.addAnalysis(new NumberOfOptionalFeatures(analysisCacher));
                yield featureStepHandler;
            }
            case FeatureValid -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, this.getName());
                featureStepHandler.addAnalysis(new NumberOfValidConfigurationsLog());
                yield featureStepHandler;
            }
            case FeatureCyclo -> {
                FeatureStepHandler featureStepHandler = new FeatureStepHandler(timeout, this.getName());
                featureStepHandler.addAnalysis(new SimpleCyclomaticComplexity());
                featureStepHandler.addAnalysis(new IndependentCyclomaticComplexity());
                yield featureStepHandler;

            }
            case SatzillaBASE -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.BASE);
            case SatzillaSP -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.SP);
            case SatzillaDIA -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.DIA);
            case SatzillaCL -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.CL);
            case SatzillaUNIT -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.UNIT);
            case SatzillaLS -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.LS);
            case SatzillaLOBJOIS -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.LOBJOIS);
            case SatzillaLP -> new SatzillaHandler(timeout, SatzillaHandler.SatzillaTypes.LP);
        };
    }

}
