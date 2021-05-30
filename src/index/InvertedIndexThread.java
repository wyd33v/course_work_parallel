package index;

import parser.Parser;
import parser.RegexParser2;
import source.DocumentSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InvertedIndexThread extends Thread {

    private ConcurrentHashMap<String, Set<Integer>> invertedIndex;
    private Parser parser;
    private DocumentSource source;
    private int startId;
    private int endId;

    public InvertedIndexThread(ConcurrentHashMap<String, Set<Integer>> invertedIndex, Parser parser,
                             DocumentSource source, int startId, int endId) {
        this.invertedIndex = invertedIndex;
        this.parser = parser;
        this.source = source;
        this.startId = startId;
        this.endId = endId;
    }

    // hell!,o -> hello

    @Override
    public void run() {
        AtomicInteger currDoc = new AtomicInteger(startId);
        for (int id = startId; id < endId; id++) {
            try {
                final int i = currDoc.getAndIncrement();
                String text = Files.readString(source.getDocumentPath(i), StandardCharsets.UTF_8);
                List<String> words = parser.parse(text.toLowerCase(Locale.ROOT));
                for (String word : words) {
                    invertedIndex.compute(word,
                        (key, value) -> {
                            if(value == null) {
                                Set<Integer> s = new HashSet<>();
                                s.add(i);
                                return s;
                            } else{
                                value.add(i);
                                return value;
                            }
                        }
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


}
