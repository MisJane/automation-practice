package library;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class LibraryZipTest {

    private InputStream getLibraryZipStream() {
        InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("library/testdata/library.zip");
        Assertions.assertNotNull(is);
        return is;
    }

    @Test
    void checkBooksCsvInLibraryZip() throws Exception {
        try (InputStream is = getLibraryZipStream();
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            boolean booksCsvFound = false;

            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();

                if (name.equals("books.csv")) {
                    booksCsvFound = true;
                    String content = new String(zis.readAllBytes());
                    assertThat(content).contains("Clean Code");
                    break;
                }
                zis.closeEntry();
            }

            assertThat(booksCsvFound)
                    .as("books.csv должен быть в архиве")
                    .isTrue();
        }
    }

    @Test
    void checkIntentsJsonInLibraryZip() throws Exception {
        try (InputStream is = getLibraryZipStream();
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            boolean intentsJsonFound = false;

            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();

                if (name.equals("books-glossary.json")) {
                    intentsJsonFound = true;
                    String content = new String(zis.readAllBytes());
                    assertThat(content).contains("\"libraryName\"");
                    break;
                }
                zis.closeEntry();
            }

            assertThat(intentsJsonFound)
                    .as("books-glossary.json должен быть в архиве")
                    .isTrue();
        }
    }

    @Test
    void checkReadersDiaryXlsxInLibraryZip() throws Exception {
        try (InputStream is = getLibraryZipStream();
             ZipInputStream zis = new ZipInputStream(is)) {

            ZipEntry entry;
            boolean readersDiaryFound = false;

            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();

                if (name.equals("reader's diary.xlsx")) {
                    readersDiaryFound = true;

                    byte[] bytes = zis.readAllBytes();
                    try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
                        Sheet sheet = workbook.getSheetAt(0);
                        Row header = sheet.getRow(0);

                        String firstColHeader = header.getCell(0).getStringCellValue();
                        String secondColHeader = header.getCell(1).getStringCellValue();

                        assertThat(firstColHeader).isEqualTo("Автор");
                        assertThat(secondColHeader).isEqualTo("Название");
                    }
                    break;
                }
                zis.closeEntry();
            }

            assertThat(readersDiaryFound)
                    .as("reader's diary.xlsx должен быть в архиве")
                    .isTrue();
        }
    }
}