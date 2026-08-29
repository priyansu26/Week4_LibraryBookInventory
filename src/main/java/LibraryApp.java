import java.time.Year;
import java.util.Scanner;

/** Command-line interface for the Library Book Inventory System. */
public class LibraryApp {
    private static final int ADD_BOOK = 1;
    private static final int LIST_BOOKS = 2;
    private static final int UPDATE_BOOK = 3;
    private static final int DELETE_BOOK = 4;
    private static final int EXIT = 5;

    private static final Scanner scanner = new Scanner(System.in);
    private static final LibraryService library = new LibraryService();

    public static void main(String[] args) {
        printHeader();

        boolean running = true;

        while (running) {
            displayMenu();

            switch (readInteger("Enter your choice: ")) {
                case ADD_BOOK -> addBook();
                case LIST_BOOKS -> listBooks();
                case UPDATE_BOOK -> updateBook();
                case DELETE_BOOK -> deleteBook();
                case EXIT -> {
                    running = false;
                    System.out.println(
                        "Thank you for using the Library Book Inventory System."
                    );
                }
                default -> System.out.println(
                    "Invalid choice. Please enter a number from 1 to 5."
                );
            }
        }

        scanner.close();
    }

    private static void printHeader() {
        System.out.println("====================================");
        System.out.println("   LIBRARY BOOK INVENTORY SYSTEM");
        System.out.println("====================================");
    }

    private static void displayMenu() {
        System.out.println("\n------------- MENU -----------------");
        System.out.println("1. Add Book");
        System.out.println("2. List All Books");
        System.out.println("3. Update Book");
        System.out.println("4. Delete Book");
        System.out.println("5. Exit");
        System.out.println("------------------------------------");
    }

    private static void addBook() {
        System.out.println("\n--- Add Book ---");

        String title = readNonEmpty("Enter title: ");
        String author = readNonEmpty("Enter author: ");
        String isbn = readNonEmpty("Enter ISBN: ");
        int year = readYear();

        try {
            library.addBook(new Book(title, author, isbn, year));
            System.out.println("Book added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void listBooks() {
        System.out.println("\n--- All Books ---");

        var books = library.getAllBooks();

        if (books.isEmpty()) {
            System.out.println("No books are currently available.");
            return;
        }

        for (int i = 0; i < books.size(); i++) {
            System.out.println((i + 1) + ". " + books.get(i));
        }
    }

    private static void updateBook() {
        System.out.println("\n--- Update Book ---");

        if (library.getAllBooks().isEmpty()) {
            System.out.println("No books are available to update.");
            return;
        }

        String isbn = readNonEmpty("Enter the ISBN of the book to update: ");
        Book book = library.findByIsbn(isbn);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        System.out.println("Press Enter to keep the existing value.");

        String title = readOptional(
            "Enter new title [" + book.getTitle() + "]: "
        );
        String author = readOptional(
            "Enter new author [" + book.getAuthor() + "]: "
        );
        int year = readOptionalYear(book.getPublicationYear());

        if (title.isEmpty()) {
            title = book.getTitle();
        }

        if (author.isEmpty()) {
            author = book.getAuthor();
        }

        try {
            library.updateBook(isbn, title, author, year);
            System.out.println("Book updated successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void deleteBook() {
        System.out.println("\n--- Delete Book ---");

        if (library.getAllBooks().isEmpty()) {
            System.out.println("No books are available to delete.");
            return;
        }

        String isbn = readNonEmpty("Enter the ISBN of the book to delete: ");
        Book book = library.findByIsbn(isbn);

        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        String confirmation = readNonEmpty(
            "Are you sure you want to delete \"" + book.getTitle() + "\"? (Y/N): "
        );

        if (confirmation.equalsIgnoreCase("Y")
                && library.deleteBook(isbn)) {
            System.out.println("Book deleted successfully.");
        } else {
            System.out.println("Delete operation cancelled.");
        }
    }

    /** Reusable method for required text input. */
    private static String readNonEmpty(String message) {
        while (true) {
            String value = readOptional(message);

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    /** Reads a line and removes surrounding whitespace. */
    private static String readOptional(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    /** Reads a valid integer without crashing on invalid input. */
    private static int readInteger(String message) {
        while (true) {
            String value = readOptional(message);

            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println(
                    "Invalid input. Please enter a whole number."
                );
            }
        }
    }

    private static int readYear() {
        while (true) {
            int year = readInteger("Enter publication year: ");

            if (isValidYear(year)) {
                return year;
            }

            System.out.println("Please enter a valid publication year.");
        }
    }

    private static int readOptionalYear(int currentYear) {
        while (true) {
            String value = readOptional(
                "Enter new publication year [" + currentYear + "]: "
            );

            if (value.isEmpty()) {
                return currentYear;
            }

            try {
                int year = Integer.parseInt(value);

                if (isValidYear(year)) {
                    return year;
                }

                System.out.println("Please enter a valid publication year.");
            } catch (NumberFormatException e) {
                System.out.println(
                    "Invalid year. Please enter a whole number."
                );
            }
        }
    }

    private static boolean isValidYear(int year) {
        return year >= 1000 && year <= Year.now().getValue();
    }
}
