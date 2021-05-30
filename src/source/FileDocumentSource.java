package source;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class FileDocumentSource implements  DocumentSource{

    private List<Path> files = new ArrayList<>();

    public void add(String folder, int startIndex, int endIndex) throws IOException {
        Path dir = Paths.get(folder);
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }
        Predicate<Path> filterByIndex = p -> {
            String fileName = p.getFileName().toString();
            int fileIndex = Integer.parseInt(fileName.substring(0, fileName.indexOf("_")));
            return startIndex <= fileIndex && fileIndex < endIndex;
        };
        List<Path> fileNames = Files.list(dir)
                .filter(filterByIndex)
                .collect(Collectors.toList());
        files.addAll(fileNames);
    }


    @Override
    public int getCountDocuments(){
        return files.size();
    }

    @Override
    public Path getDocumentPath(int id){
        return files.get(id);
    }

}
