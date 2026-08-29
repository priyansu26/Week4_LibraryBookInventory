import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** JUnit 5 tests for CRUD operations and refactored ISBN handling. */
class LibraryServiceTest {
    private LibraryService service;

    @BeforeEach
    void setUp() {
        service = new LibraryService();
    }

    @Test
    void shouldCreateAndReadBook() {
        service.addBook(
            new Book("Java Programming", "James Gosling", "ISBN-001", 2020)
        );

        assertEquals(1, service.getAllBooks().size());
        assertEquals(
            "Java Programming",
            service.findByIsbn("ISBN-001").getTitle()
        );
    }

    @Test
    void shouldPreventDuplicateIsbn() {
        service.addBook(
            new Book("Java Programming", "James Gosling", "ISBN-002", 2020)
        );

        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> service.addBook(
                new Book("Another", "Author", " isbn-002 ", 2021)
            )
        );

        assertEquals(
            "A book with this ISBN already exists.",
            e.getMessage()
        );
    }

    @Test
    void shouldFindBookIgnoringIsbnCaseAndSpaces() {
        service.addBook(
            new Book("Clean Code", "Robert Martin", "abc-123", 2008)
        );

        assertNotNull(service.findByIsbn("  ABC-123  "));
    }

    @Test
    void shouldUpdateBook() {
        service.addBook(
            new Book("Java Programming", "James Gosling", "ISBN-003", 2020)
        );

        service.updateBook(
            "ISBN-003",
            "Advanced Java",
            "James Gosling",
            2022
        );

        Book book = service.findByIsbn("ISBN-003");

        assertEquals("Advanced Java", book.getTitle());
        assertEquals(2022, book.getPublicationYear());
    }

    @Test
    void shouldRejectUpdateForMissingBook() {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> service.updateBook(
                "MISSING", "Title", "Author", 2020
            )
        );

        assertEquals("Book not found.", e.getMessage());
    }

    @Test
    void shouldDeleteExistingBook() {
        service.addBook(
            new Book("Java Basics", "Author One", "ISBN-004", 2021)
        );

        assertTrue(service.deleteBook("ISBN-004"));
        assertNull(service.findByIsbn("ISBN-004"));
        assertTrue(service.getAllBooks().isEmpty());
    }

    @Test
    void shouldReturnFalseWhenDeletingMissingBook() {
        assertFalse(service.deleteBook("MISSING"));
    }

    @Test
    void shouldRejectNullBook() {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> service.addBook(null)
        );

        assertEquals("Book cannot be null.", e.getMessage());
    }

    @Test
    void shouldRejectBlankTitle() {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> service.addBook(
                new Book(" ", "Author", "ISBN-005", 2020)
            )
        );

        assertEquals("Title cannot be empty.", e.getMessage());
    }

    @Test
    void shouldRejectInvalidPublicationYear() {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class,
            () -> service.addBook(
                new Book("Book", "Author", "ISBN-006", 999)
            )
        );

        assertEquals("Invalid publication year.", e.getMessage());
    }

    @Test
    void shouldReturnNullForMissingIsbn() {
        service.addBook(
            new Book("Book", "Author", "ISBN-007", 2020)
        );

        assertNull(service.findByIsbn("NOT-FOUND"));
    }

    @Test
    void shouldHandleMultipleBooks() {
        service.addBook(
            new Book("Book One", "Author One", "ISBN-008", 2020)
        );
        service.addBook(
            new Book("Book Two", "Author Two", "ISBN-009", 2021)
        );

        assertEquals(2, service.getAllBooks().size());
        assertEquals(
            "ISBN-008",
            service.getAllBooks().get(0).getIsbn()
        );
    }
}
