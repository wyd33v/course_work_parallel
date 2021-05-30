package main;

import index.InvertedIndexParallel;
import parser.RegexParser;
import parser.RegexParser2;
import source.DocumentSource;
import source.FileDocumentSource;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {

        DocumentSource source = new FileDocumentSource();
        String mainFolder = "C:\\course_work_parallel_computing\\aclImdb";
        long startTime = System.currentTimeMillis();
        source.add(mainFolder + "\\test\\neg", 8250, 8500);
        source.add(mainFolder + "\\test\\pos", 8250, 8500);
        source.add(mainFolder + "\\train\\neg", 8250, 8500);
        source.add(mainFolder + "\\train\\unsup", 0, 5000); // 33000, 34000



        InvertedIndexParallel invertedIndex = new InvertedIndexParallel();
        invertedIndex.createInvertedIndex(4, source, new RegexParser2());
        System.out.println("Ended in " + (System.currentTimeMillis() - startTime) + " ms");




        System.out.println("CountDocuments = " + source.getCountDocuments());
         System.out.println(invertedIndex.search("number"));

    }
}
