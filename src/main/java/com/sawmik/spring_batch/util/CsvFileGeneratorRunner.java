package com.sawmik.spring_batch.util;

import java.io.IOException;

public class CsvFileGeneratorRunner {
    public static void main(String[] args) throws IOException {
        CsvFileGenerator generator = new CsvFileGenerator();
        String outputDir = args.length > 0 ? args[0] : "Csv_Files";
        new java.io.File(outputDir).mkdirs();
        generator.generateAllFiles(outputDir);
    }
}
