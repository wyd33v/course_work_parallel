package source;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class FileDocumentSource2 implements  DocumentSource{

    private List<File> files = new ArrayList<>();

    public void add(String folder, int startIndex, int endIndex){
        File directory = new File(folder);
        FilenameFilter filter = (dir, name) -> {
            int fileIndex = Integer.parseInt(name.substring(0, name.indexOf("_")));
            return startIndex <= fileIndex && fileIndex < endIndex;
        };
        files.addAll(Arrays.asList(Objects.requireNonNull(directory.listFiles(filter))));
    }

    @Override
    public Path getDocumentPath(int id){
        return files.get(id).toPath();
    }

    @Override
    public int getCountDocuments() {
        return files.size();
    }
}
