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
    @Test
    void checkFilesInsideLibraryZip() throws Exception {
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream("library/testdata/library.zip")) {
            Assertions.assertNotNull(is);
            try (ZipInputStream zis = new ZipInputStream(is)) {

                ZipEntry entry;
                boolean xlsxChecked = false;
                boolean txtFound = false;
                boolean csvFound = false;
                boolean jsonFound = false;

                while ((entry = zis.getNextEntry()) != null) {
                    String name = entry.getName();

                    if (name.equals("books.csv")) {
                        txtFound = true;
                        String content = new String(zis.readAllBytes());

                        assertThat(content).contains("Clean Code");
                    }

                    if (name.equals("intents.json")) {
                        jsonFound = true;
                        String content = new String(zis.readAllBytes());

                        assertThat(content).contains("\"intent\"");
                    }

                    if (name.equals("reader's diary.xlsx")) {
                        csvFound = true;

                        byte[] bytes = zis.readAllBytes();
                        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
                            Sheet sheet = workbook.getSheetAt(0);
                            Row header = sheet.getRow(0);

                            String firstColHeader = header.getCell(0).getStringCellValue();
                            String secondColHeader = header.getCell(1).getStringCellValue();

                            assertThat(firstColHeader).isEqualTo("Title");
                            assertThat(secondColHeader).isEqualTo("Author");
                        }

                        xlsxChecked = true;
                    }

                    zis.closeEntry();
                }

                assertThat(xlsxChecked)
                        .as("Файл reader's diary.xlsx должен быть найден и проверен в архиве")
                        .isTrue();

                assertThat(txtFound).as("books.csv должен быть в архиве").isTrue();
                assertThat(jsonFound).as("intents.json должен быть в архиве").isTrue();
                assertThat(csvFound).as("reader's diary.xlsx должен быть в архиве").isTrue();
            }
        }
    }
}
