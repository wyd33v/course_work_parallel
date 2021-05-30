package index;

import parser.Parser;
import source.DocumentSource;

import java.nio.file.Path;
import java.util.Set;

public interface InvertedIndex {
    void createInvertedIndex(int threadCount, DocumentSource source, Parser parser);
    Set<Path> search(String word);
}
