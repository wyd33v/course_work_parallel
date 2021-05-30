package source;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentSource {
    Path getDocumentPath(int id);
    int getCountDocuments();
    void add(String folder, int startIndex, int endIndex) throws IOException;

}


