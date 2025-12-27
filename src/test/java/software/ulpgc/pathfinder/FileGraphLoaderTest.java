package software.ulpgc.pathfinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileGraphLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void should_load_valid_graph_and_find_path() throws IOException {
        Path file = tempDir.resolve("graph.txt");
        Files.write(file, List.of("A,B,10.0", "B,C,5.0"));

        FileGraphLoader loader = new FileGraphLoader(file.toFile());
        GraphContainer container = loader.load();

        List<String> path = container.shortestPathBetween("A", "C");
        assertEquals(List.of("A", "B", "C"), path);
        assertEquals(15.0, container.pathWeightBetween("A", "C"));
    }

    @Test
    void should_handle_parsing_errors_gracefully() throws IOException {
        Path file = tempDir.resolve("bad_format.txt");
        Files.write(file, List.of("X,Y,1.0", "INVALID", "X,Z,not_a_number"));

        FileGraphLoader loader = new FileGraphLoader(file.toFile());
        GraphContainer container = loader.load();

        assertDoesNotThrow(() -> container.shortestPathBetween("X", "Y"));
    }

    @Test
    void should_throw_exception_when_file_does_not_exist() {
        File missingFile = new File("i_do_not_exist.txt");
        FileGraphLoader loader = new FileGraphLoader(missingFile);

        assertThrows(IOException.class, loader::load);
    }
}