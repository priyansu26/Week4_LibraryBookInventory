import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Business logic for the Library Book Inventory System.
 *
 * Week 4 refactoring:
 * - LinkedHashMap provides average O(1) ISBN lookup.
 * - ISBN normalization is centralized.
 * - Validation is centralized to reduce repetition.
 * - LinkedHashMap preserves insertion order for display.
 */
public class LibraryService {
    private static final int MIN_PUBLICATION_YEAR = 1000;

    private final Map<String, Book> booksByIsbn = new LinkedHashMap<>();

    /** CREATE: validates and adds a book. */
    public void addBook(Book book) {
        validateBook(book);

        String normalizedIsbn = normalizeIsbn(book.getIsbn());

        if (booksByIsbn.containsKey(normalizedIsbn)) {
            throw new IllegalArgumentException("A book with this ISBN already exists.");
        }

        book.setTitle(book.getTitle().trim());
        book.setAuthor(book.getAuthor().trim());
        book.setIsbn(normalizedIsbn);

        booksByIsbn.put(normalizedIsbn, book);
    }

    /** READ: returns an unmodifiable list without exposing the internal map. */
    public List<Book> getAllBooks() {
        return Collections.unmodifiableList(
            new ArrayList<>(booksByIsbn.values())
        );
    }

    /** READ: finds a book by ISBN using an average O(1) map lookup. */
    public Book findByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }

        return booksByIsbn.get(normalizeIsbn(isbn));
    }

    /** UPDATE: changes title, author and publication year. */
    public void updateBook(String isbn, String newTitle, String newAuthor, int newYear) {
        Book book = findByIsbn(isbn);

        if (book == null) {
            throw new IllegalArgumentException("Book not found.");
        }

        validateText(newTitle, "Title");
        validateText(newAuthor, "Author");
        validateYear(newYear);

        book.setTitle(newTitle.trim());
        book.setAuthor(newAuthor.trim());
        book.setPublicationYear(newYear);
    }

    /** DELETE: removes a matching book and reports whether removal occurred. */
    public boolean deleteBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }

        return booksByIsbn.remove(normalizeIsbn(isbn)) != null;
    }

    /** Performs all common validation for a book. */
    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }

        validateText(book.getTitle(), "Title");
        validateText(book.getAuthor(), "Author");
        validateText(book.getIsbn(), "ISBN");
        validateYear(book.getPublicationYear());
    }

    /** Validates required text fields in one reusable method. */
    private void validateText(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
    }

    /** Validates publication year against the current year. */
    private void validateYear(int year) {
        int currentYear = Year.now().getValue();

        if (year < MIN_PUBLICATION_YEAR || year > currentYear) {
            throw new IllegalArgumentException("Invalid publication year.");
        }
    }

    /** Gives ISBN values one consistent format for map keys and searches. */
    private String normalizeIsbn(String isbn) {
        return isbn.trim().toUpperCase();
    }
}
