package source;

import java.nio.file.Path;

public interface DocumentSource {
    Path getDocumentPath(int id);
    int getCountDocuments();
}


