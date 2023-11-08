package org.collection.fm.handler;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.analysis.cnf.solver.RuntimeTimeoutException;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import org.collection.fm.analyses.IFMAnalysis;
import org.collection.fm.analyses.NumberOfValidConfigurationsLog;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;


public class FeatureStepHandler {


    private final List<IFMAnalysis> featureAnalyses = new ArrayList<>();
    protected final int runtimeLimit;
    protected final String name;

    public FeatureStepHandler(int runtimeLimit, String name) {
        this.runtimeLimit = runtimeLimit;
        this.name = name;
    }

    public void addAnalysis(IFMAnalysis analysis){
        featureAnalyses.add(analysis);
    }

    public List<String> getCSVHeader(){
        return featureAnalyses.stream().map(IFMAnalysis::getLabel).toList();
    }

    public String getName(){
        return name;
    }


    public FeatureStep evaluateFeatureStep(ExecutorService executorService, IFeatureModel featureModel, FeatureModelFormula formula, Path file, Path solverRelativePath) throws InterruptedException {
        //TODO time-handling, error handling, ...
        LocalDateTime before = LocalDateTime.now();
        ArrayList<String> values = new ArrayList<>();
        for (IFMAnalysis analysis : featureAnalyses) {
            if (Thread.currentThread().isInterrupted()) break;
            int timeout = runtimeLimit - (int) Math.ceil(Duration.between(before, LocalDateTime.now()).toMillis() / 1000d);

            if (analysis instanceof NumberOfValidConfigurationsLog) {
                values.add(analysis.getResult(featureModel, formula, timeout, solverRelativePath)); //Special Handling because of external solver call
                continue;
            }

            Future<String> result = executorService.submit(() -> analysis.getResult(featureModel, formula, timeout, solverRelativePath));
            try {
                values.add(result.get(timeout, TimeUnit.SECONDS));
            } catch (ExecutionException e) {
                return new FeatureStep(file, Collections.nCopies(featureAnalyses.size(), "?"), runtimeLimit, FeatureStatus.crash);
            } catch (InterruptedException e) {
                result.cancel(true);
                System.out.println(analysis.getLabel() + " interrupted!");
                throw new InterruptedException();
            } catch (TimeoutException | RuntimeTimeoutException e) {
                result.cancel(true);
                System.out.println(analysis.getLabel() + " ran out of time!");
                return new FeatureStep(file, Collections.nCopies(featureAnalyses.size(), "?"), runtimeLimit, FeatureStatus.timeout);
            }
        }
        double runtime = Duration.between(before, LocalDateTime.now()).toMillis() / 1000d;
        return new FeatureStep(file, values, runtime, FeatureStatus.ok);
    }


}
