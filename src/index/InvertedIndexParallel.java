package index;

import parser.Parser;
import source.DocumentSource;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InvertedIndexParallel {

    private ConcurrentHashMap<String, Set<Integer>> invertedIndex = new ConcurrentHashMap<>();
    private final DocumentSource source;
    
    public InvertedIndexParallel(final DocumentSource source) {
         this.source = source;
    }



    public void createInvertedIndex(int threadCount, final DocumentSource source, Parser parser) throws IOException {
        InvertedIndexThread[] threads = new InvertedIndexThread[threadCount];
        int partSize = source.getCountDocuments() / threadCount;

        for (int i = 0; i < threadCount; i++) {
            int startId = i * partSize;
            int endId = i == (threadCount - 1) ?
                    (source.getCountDocuments() - 1) :
                    (i + 1) * partSize;
            threads[i] = new InvertedIndexThread(invertedIndex, parser, source, startId, endId);
            threads[i].start();
        }
        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        for (Integer id :  invertedIndex.get("number")) {
//            System.out.println(source.getDocumentPath(id));
//        }
//        System.out.println(invertedIndex.get("number"));

//        for (String word : invertedIndex.keySet()) {
//            System.out.println(word);
//            System.out.println(invertedIndex.get(word));
//        }

    }

    public Set<Path> search(String word){
        Set<Path> paths = new HashSet<>();
        for (Integer id :  invertedIndex.get("number")) {
            paths.add(source.getDocumentPath(id));
        }
        return paths;
    }


}
