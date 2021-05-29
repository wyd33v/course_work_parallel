package main;

import index.InvertedIndexParallel;
import parser.RegexParser;
import source.FileDocumentSource;
import source.FileDocumentSource2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {
        FileDocumentSource source = new FileDocumentSource();
        String mainFolder = "C:\\(D)\\dev\\eclipse-workspace\\course_work_parallel_computing\\aclImdb";
        long startTime = System.currentTimeMillis();
        source.add(mainFolder + "\\test\\pos", 6500, 6750);
        source.add(mainFolder + "\\test\\neg", 6500, 6750);
        source.add(mainFolder + "\\train\\pos", 6500, 6750);
        source.add(mainFolder + "\\train\\neg", 6500, 6750);
        source.add(mainFolder + "\\train\\unsup", 6500, 6750);

        
        InvertedIndexParallel invertedIndex = new InvertedIndexParallel(source);
        invertedIndex.createInvertedIndex(4,source, new RegexParser());
        System.out.println("Ended in " + (System.currentTimeMillis() - startTime) + " ms");
        System.out.println("CountDocuments = " + source.getCountDocuments());

        System.out.println(invertedIndex.search("number"));


    }
}
