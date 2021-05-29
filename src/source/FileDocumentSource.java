package source;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;




public class FileDocumentSource implements  DocumentSource{
    private List<Path> files = new ArrayList<>();


    // Files.walk(dir) медленнее Files.list(dir)
    public void add2(String folder, int startIndex, int endIndex) throws IOException {
        Path dir = Paths.get(folder);
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }
        Predicate<Path> filterByIndex = p -> {
            String fileName = p.getFileName().toString();
            int pos = fileName.indexOf("_");
            if(pos==-1) return false;
            int fileIndex = Integer.parseInt(fileName.substring(0, pos ));
            return startIndex <= fileIndex && fileIndex < endIndex;
        };
        try (Stream<Path> walk = Files.walk(dir)) {
            List<Path> fileNames = walk
                    .filter(filterByIndex)
                    .collect(Collectors.toList());
            files.addAll(fileNames);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void readAll() throws IOException { // 12 sec = 50000 files and 2.5 sec in parallel mode
        List<String> texts = new ArrayList<>();
        for (Path path : files) {
            String text = Files.readString(path, StandardCharsets.UTF_8);
            texts.add(text);
        }
    }

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
