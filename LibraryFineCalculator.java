import java.io.*;
import java.util.Scanner;

/**
 * PS 46 - Library Fine Calculator
 * Concepts: Array, Searching, Sorting, Methods, File Handling
 * Run: javac LibraryFineCalculator.java && java LibraryFineCalculator
 */
class FineRecord {
    String id;
    String member;
    String book;
    int days;
    int fine;

    FineRecord(String id, String member, String book, int days, int fine) {
        this.id = id;
        this.member = member;
        this.book = book;
        this.days = days;
        this.fine = fine;
    }

    // Used for display and for saving to file
    public String toString() {
        return id + " | " + member + " | " + book + " | Days: " + days + " | Fine: Rs " + fine;
    }
}

public class LibraryFineCalculator {
    static final int MAX = 100;
    static final String FILE_NAME = "fines.txt";

    static FineRecord[] records = new FineRecord[MAX];
    static int count = 0;
    static int nextIdNumber = 101; // first ID is F101

    static Scanner sc = new Scanner(System.in);

    // Fine rule: Rs 2 per day beyond 14 days, no fine for 14 days or less
    static int calculateFine(int days) {
        if (days <= 14) {
            return 0;
        }
        return (days - 14) * 2;
    }

    // Add a new record
    static void addRecord() {
        System.out.print("Member: ");
        String member = sc.nextLine().trim();
        System.out.print("Book: ");
        String book = sc.nextLine().trim();
        System.out.print("Days kept: ");
        String input = sc.nextLine().trim();

        int days;
        try {
            days = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid days. Days kept must be 1 or more.");
            return;
        }

        if (days < 1) {
            System.out.println("Invalid days. Days kept must be 1 or more.");
            return;
        }
        if (count >= MAX) {
            System.out.println("Storage full. Cannot add more records.");
            return;
        }

        String id = "F" + nextIdNumber;
        nextIdNumber++;
        int fine = calculateFine(days);
        records[count] = new FineRecord(id, member, book, days, fine);
        count++;

        System.out.println("Record added. " + records[count - 1]);
    }

    // Display all records
    static void displayAll() {
        if (count == 0) {
            System.out.println("No records found.");
            return;
        }
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + records[i]);
        }
    }

    // Linear search by member name (own logic, no built-in search)
    static void searchByMember() {
        System.out.print("Member name: ");
        String name = sc.nextLine().trim();
        boolean found = false;

        for (int i = 0; i < count; i++) {
            if (records[i].member.equalsIgnoreCase(name)) {
                System.out.println("Found: " + records[i]);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No record found for member: " + name);
        }
    }

    // Bubble sort by fine, high to low (own logic, no built-in sort)
    static void sortByFine() {
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - 1 - i; j++) {
                if (records[j].fine < records[j + 1].fine) {
                    FineRecord temp = records[j];
                    records[j] = records[j + 1];
                    records[j + 1] = temp;
                }
            }
        }
        displayAll();
    }

    // Load records from file at start
    static void loadFromFile() {
        File f = new File(FILE_NAME);
        if (!f.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null && count < MAX) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] p = line.split("\\|");
                if (p.length < 5) {
                    continue;
                }
                String id = p[0].trim();
                String member = p[1].trim();
                String book = p[2].trim();
                int days = Integer.parseInt(p[3].trim());
                int fine = Integer.parseInt(p[4].trim());
                records[count] = new FineRecord(id, member, book, days, fine);
                count++;

                int num = Integer.parseInt(id.substring(1));
                if (num >= nextIdNumber) {
                    nextIdNumber = num + 1;
                }
            }
        } catch (Exception e) {
            System.out.println("Could not read " + FILE_NAME + ": " + e.getMessage());
        }
    }

    // Save records to file on exit (file format: id|member|book|days|fine)
    static void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < count; i++) {
                FineRecord r = records[i];
                pw.println(r.id + "|" + r.member + "|" + r.book + "|" + r.days + "|" + r.fine);
            }
        } catch (IOException e) {
            System.out.println("Could not save " + FILE_NAME + ": " + e.getMessage());
        }
    }

    static void showMenu() {
        System.out.println("\n--- Library Fine Calculator ---");
        System.out.println("1-Add Record");
        System.out.println("2-Display All");
        System.out.println("3-Search by Member");
        System.out.println("4-Sort by Fine");
        System.out.println("5-Exit");
        System.out.print("Choose option: ");
    }

    public static void main(String[] args) {
        loadFromFile();

        boolean running = true;
        while (running) {
            showMenu();
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addRecord();
                    break;
                case "2":
                    displayAll();
                    break;
                case "3":
                    searchByMember();
                    break;
                case "4":
                    sortByFine();
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Choose 1-5.");
            }
        }

        saveToFile();
        System.out.println("Records saved to " + FILE_NAME + ". Goodbye!");
    }
}
