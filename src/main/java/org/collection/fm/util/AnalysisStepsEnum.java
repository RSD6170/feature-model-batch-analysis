package org.collection.fm.util;

import org.collection.fm.analyses.*;
import org.collection.fm.formulagraph.ConnectivityGraph;
import org.collection.fm.handler.FeatureStepHandler;
import org.collection.fm.handler.SatzillaHandler;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.NoSuchElementException;

public enum AnalysisStepsEnum {

    FeatureBasic("Feature-basic", List.of(AnalysisCategory.All, AnalysisCategory.Default, AnalysisCategory.Simple)),
    FeatureConstraints("Feature-constraints",
            List.of(AnalysisCategory.All, AnalysisCategory.Default, AnalysisCategory.Simple)),
    FeatureTree("Feature-tree", List.of(AnalysisCategory.All, AnalysisCategory.Default, AnalysisCategory.Simple)),
    FeatureClause("Feature-clause", List.of(AnalysisCategory.All, AnalysisCategory.Default, AnalysisCategory.CNF)),
    FeatureConstraintRedundancy("Feature-clause-redundancy", List.of(AnalysisCategory.All, AnalysisCategory.Default)),
    FeatureCore("Feature-core", List.of(AnalysisCategory.All, AnalysisCategory.Default)),
    FeatureValid("Feature-valid", List.of(AnalysisCategory.All, AnalysisCategory.Default)),
    FeatureCyclo("Feature-cyclo", List.of(AnalysisCategory.All, AnalysisCategory.Default)),
    SatzillaBASE("Satzilla-BASE", List.of(AnalysisCategory.All, AnalysisCategory.SATzilla)),
    SatzillaSP("Satzilla-SP", List.of(AnalysisCategory.All, AnalysisCategory.SATzilla)),
    SatzillaDIA("Satzilla-DIA", List.of(AnalysisCategory.All, AnalysisCategory.SATzilla)),
    SatzillaCL("Satzilla-CL", List.of(AnalysisCategory.All, AnalysisCategory.SATzilla)),
    SatzillaUNIT("Satzilla-UNIT", List.of(AnalysisCategory.All, AnalysisCategory.SATzilla)),
    SatzillaLS("Satzilla-LS", List.of(AnalysisCategory.All, AnalysisCategory.SATzilla)),
    SatzillaLOBJOIS("Satzilla-LOBJOIS", List.of(AnalysisCategory.All, AnalysisCategory.SATzilla)),
    SatzillaLP("Satzilla-LP", List.of(AnalysisCategory.All, AnalysisCategory.SATzilla));

    private final String name;
    private List<AnalysisCategory> categories;

    public String getName() {
        return name;
    }

    public List<AnalysisCategory> getCategories() {
        return categories;
    }

    AnalysisStepsEnum(String name, List<AnalysisCategory> categories) {
        this.name = name;
        this.categories = categories;
    }

    public static AnalysisStepsEnum getIgnoreCase(String string) throws NoSuchElementException {
        return Arrays.stream(AnalysisStepsEnum.values()).filter(e -> e.toString().equalsIgnoreCase(string)).findFirst()
                .orElse(null);
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
                featureStepHandler.addAnalysis(new ConnectivityDensity());
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

    public static EnumMap<AnalysisStepsEnum, Integer> getEnumMapByCategory(String category, int timeout) {
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
