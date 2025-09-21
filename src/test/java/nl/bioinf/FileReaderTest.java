//package nl.bioinf;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//class FileReaderTest {
//    static String fileName = "C:/Users/yamil/Downloads";
//    static Path path = Paths.get(fileName);
//
//
//    @Test
//    @DisplayName("Test incorrect file paths")
//    void readCSV() throws IOException {
//        String fileName = "9382jdn";
//        Path path = Paths.get(fileName);
//        String expectedMessage = "Incorrect file path";
//        assertThrows(IOException.class, () -> FileReader.readCSV(path));
//        //assertEquals(expectedMessage, FileReader.readCSV(path));
//
//    }
//
//        //assertEquals({"id,gene,chr,fpos,tpos,strand,Sample1,Sample2,Sample3\n"}, FileReader.readCSV(path));
//}
//
//
//
//
//
////file.isreadable
////file.exist
////s.matches to check if first line matches required input