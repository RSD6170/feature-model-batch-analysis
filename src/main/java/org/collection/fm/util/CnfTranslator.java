package org.collection.fm.util;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import org.prop4j.Node;

import de.ovgu.featureide.fm.core.analysis.cnf.formula.FeatureModelFormula;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.io.dimacs.DimacsReader;

public class CnfTranslator {
    

    public static void main(String[] args) {
        FMUtils.installLibraries();
        String csvContent = "model;translation time(milliseconds)\n";
        FileUtils.createDirIfItDoesntExist("cnf/evo");
        
        for (File file : FileUtils.getFileList("evolution")) {
            csvContent += handleFile(file);
        }
        FileUtils.writeContentToFileAndCreateDirs("cnf" + File.separator + "runtimes_evo.csv", csvContent, true);
        
    }



    private static String handleFile(File file) {
        if (!FileUtils.getExtension(file.getPath()).equals("xml")) {
            return "";
        }
        IFeatureModel model = FMUtils.readFeatureModel(file.getPath());
        long startTime = System.nanoTime();
        FeatureModelFormula formula = new FeatureModelFormula(model);
        formula.getCNFNode();
        long endTime = System.nanoTime();
        String[] split = file.getPath().split(File.separator);
        String dirName = split[split.length - 2];
        if (!dirName.equals("data")) {
            FileUtils.createDirIfItDoesntExist("cnf/evo" + File.separator + dirName);
            FMUtils.saveCNF(formula, "cnf" + File.separator + "evo" + File.separator + dirName + File.separator + file.getName());
        } else {
            FMUtils.saveCNF(formula, "cnf" + File.separator + file.getName());
        }

        return file.getPath() + ";" + ((endTime - startTime) / 1000000) + "\n";
    }

    public static Node readDimacs(String path) {
        DimacsReader reader = new DimacsReader();
        Node cnf = null;
        try {
            cnf = reader.read(new FileReader(new File(path)));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return cnf;
    }

}