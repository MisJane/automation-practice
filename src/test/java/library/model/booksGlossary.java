package library.model;

import java.time.LocalDate;

public class booksGlossary {

    private String libraryName;
    private LocalDate updatedAt;
    private booksGlossaryInner books;

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public booksGlossaryInner getBooks() {
        return books;
    }

    public void setBooks(booksGlossaryInner books) {
        this.books = books;
    }


}
