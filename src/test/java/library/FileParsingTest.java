package library;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileParsingTest {

    private final ClassLoader cl = FileParsingTest.class.getClassLoader();
    private static final Gson gson = new Gson();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void csvFileParsingTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("book-inventory.csv")) {
            Assertions.assertNotNull(is);
            try (CSVReader csvReader = new CSVReader(new InputStreamReader(is))) {
                List<String[]> data = csvReader.readAll();
                Assertions.assertEquals(2, data.size());
                Assertions.assertArrayEquals(
                        new String[]{"firstCount", "secondCount", "expectedTotal"},
                        data.get(0)
                );
                Assertions.assertArrayEquals(
                        new String[]{"1", "2", "3"},
                        data.get(1)
                );
            }
        }

    }

    @Test
    void zipFileNameParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                Objects.requireNonNull(cl.getResourceAsStream("library/testdata/library.zip"))
        )) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
            }

        }

    }

    @Test
    void zipFileParsingTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("library/testdata/library.zip")) {
            Assertions.assertNotNull(is);
            try (ZipInputStream zis = new ZipInputStream(is)) {

                List<String> actualFilenames = new ArrayList<>();
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    actualFilenames.add(entry.getName());
                }

                List<String> expectedFilenames = List.of(
                        "books.csv",
                        "books-glossary.json",
                        "reader's diary.xlsx",
                        "Clean Code.pdf"
                );

                Assertions.assertEquals(
                        expectedFilenames.size(),
                        actualFilenames.size(),
                        "Unexpected files count in zip"
                );
                Assertions.assertTrue(
                        actualFilenames.containsAll(expectedFilenames),
                        "Zip does not contain all expected files"
                );
            }
        }
    }

    @Test
    void jsonFileParsingImprovedTest() throws Exception {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(cl.getResourceAsStream("library/testdata/books-glossary.json"))
        )) {
            JsonObject actual = gson.fromJson(reader, JsonObject.class);

            Assertions.assertEquals("Demo Library", actual.get("libraryName").getAsString());
            Assertions.assertEquals("2026-04-08", actual.get("updatedAt").getAsString());

            JsonObject inner = actual.get("books").getAsJsonObject();

            Assertions.assertEquals("B001", inner.get("id").getAsString());
            Assertions.assertEquals("Clean Code", inner.get("title").getAsString());
            Assertions.assertEquals("Robert C. Martin", inner.get("author").getAsString());

        }

    }

    @Test
    void jsonFileParsingImprovedTestJackson() throws Exception {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(cl.getResourceAsStream("library/testdata/books-glossary.json"))
        )) {
            JsonNode actual = objectMapper.readTree(reader);

            Assertions.assertEquals("Demo Library",
                    actual.get("libraryName").asText());
            Assertions.assertEquals("2026-04-08",
                    actual.get("updatedAt").asText());

            JsonNode inner = actual.get("books");
            Assertions.assertEquals("B001", inner.get("id").asText());
            Assertions.assertEquals("Clean Code", inner.get("title").asText());
            Assertions.assertEquals("Robert C. Martin", inner.get("author").asText());
        }
    }


}
