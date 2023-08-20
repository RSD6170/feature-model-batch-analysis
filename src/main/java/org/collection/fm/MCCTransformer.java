package org.collection.fm;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

public class MCCTransformer {

    public static final double timeout = 3601;

    public static final Path csvFile = Path.of("/home/ubuntu/mcc2022/x_ana_mcc2022/3-outputs_track1_mc_private/0_all/5-correct.csv");
    public static final Path outputFile = Path.of("algo_runs_private.csv");


    public static void main(String[] args) {



        try (CSVParser parser = CSVParser.parse(csvFile, Charset.defaultCharset(), CSVFormat.Builder.create(CSVFormat.DEFAULT).setHeader().setAllowMissingColumnNames(true).setSkipHeaderRecord(true).build());
             CSVPrinter printer = new CSVPrinter(Files.newBufferedWriter(outputFile, Charset.defaultCharset(), StandardOpenOption.CREATE), CSVFormat.Builder.create().build())){
            Table<String, String, Double> table = HashBasedTable.create();
            parser.stream().forEach(e -> {
                String benchmark = e.get("benchmark");
                String solver = e.get("solver")+"/"+e.get("configuration");
                double walltime = Double.parseDouble(e.get("wall_time"));

                switch (e.get("status")){
                    case "complete" -> {
                        if (Objects.equals(e.get("corr"), "1")) table.put(benchmark, solver, walltime);
                        else table.put(benchmark, solver, timeout);
                    }
                    case "memout" -> {
                        table.put(benchmark, solver, timeout);
                    }
                    case "timeout (cpu)", "timeout (wallclock)" -> {
                        table.put(benchmark, solver, timeout);
                    }
                    default -> {
                        throw new RuntimeException(e.toString());
                    }
                }
            });
            printer.print("instance");
            printer.printRecord(table.columnKeySet());
            printer.printRecords(table.rowMap().entrySet().stream()
                    .map(entry -> ImmutableList.builder()
                            .add(entry.getKey()).addAll(entry.getValue().values()).build())
                    .collect(Collectors.toList()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
