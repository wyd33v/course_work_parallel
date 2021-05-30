package index;

import parser.Parser;
import source.DocumentSource;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InvertedIndexParallel implements InvertedIndex{

    private ConcurrentHashMap<String, Set<Integer>> invertedIndex = new ConcurrentHashMap<>(200000);
    private DocumentSource source;

    public void createInvertedIndex(int threadCount, DocumentSource source, Parser parser) {
        InvertedIndexThread[] threads = new InvertedIndexThread[threadCount];
        this.source = source;
        int partSize = source.getCountDocuments() / threadCount;
        for (int i = 0; i < threadCount; i++) {
            int startId = i * partSize;
            int endId = i == (threadCount - 1) ?
                    source.getCountDocuments() :
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
    }

    public Set<Path> search(String word){
        Set<Path> paths = new HashSet<>();
        for (int id :  invertedIndex.get(word)) {
            paths.add(source.getDocumentPath(id));
        }
        return paths;
        // return invertedIndex.get(word).stream().map(id-> source.getDocumentPath(id)).collect(Collectors.toSet());
    }


}
