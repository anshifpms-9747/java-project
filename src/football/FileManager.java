package football;

import java.io.*;

// ============================================================
// OOP CONCEPT: FILE HANDLING — Java Object Serialization
// FileManager persists and restores the complete Tournament object graph
// (Tournament -> Teams -> Players, Coaches -> Matches, Referees).
// Demonstrates try-with-resources for automatic resource cleanup.
// ============================================================
public class FileManager {

    // Default serialization storage file
    private static final String DEFAULT_FILE = "tournament_data.dat";

    // Private constructor prevents direct instantiation (utility class pattern)
    private FileManager() {}

    /**
     * Persists the Tournament object graph to disk using ObjectOutputStream.
     *
     * @param tournament the tournament instance to save
     * @param filename   the destination file path
     */
    public static boolean save(Tournament tournament, String filename) {
        String targetFile = (filename == null || filename.isBlank()) ? DEFAULT_FILE : filename;

        // Try-with-resources guarantees streams close automatically even if an exception occurs
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(targetFile))) {
            oos.writeObject(tournament);
            System.out.printf("%n  [FILE SUCCESS] Tournament saved successfully to '%s'.%n", targetFile);
            return true;
        } catch (IOException e) {
            System.err.printf("  [FILE ERROR] Failed to save tournament: %s%n", e.getMessage());
            return false;
        }
    }

    /**
     * Reconstructs the Tournament object graph from disk using ObjectInputStream.
     *
     * @param filename the source file path
     * @return the deserialized Tournament object, or null on failure
     */
    public static Tournament load(String filename) {
        String targetFile = (filename == null || filename.isBlank()) ? DEFAULT_FILE : filename;
        File file = new File(targetFile);

        if (!file.exists()) {
            System.out.printf("%n  [FILE NOTICE] File '%s' does not exist yet.%n", targetFile);
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Tournament loaded = (Tournament) ois.readObject();
            System.out.printf("%n  [FILE SUCCESS] Tournament '%s' (%s) loaded from '%s'.%n",
                    loaded.getName(), loaded.getSeason(), targetFile);
            return loaded;
        } catch (FileNotFoundException e) {
            System.err.printf("  [FILE ERROR] File not found: %s%n", targetFile);
        } catch (IOException e) {
            System.err.printf("  [FILE ERROR] I/O reading error: %s%n", e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("  [FILE ERROR] Class mismatch: class version incompatible with serialized data.");
        }
        return null;
    }

    // Convenience overloads using default filename
    public static boolean save(Tournament tournament) {
        return save(tournament, DEFAULT_FILE);
    }

    public static Tournament load() {
        return load(DEFAULT_FILE);
    }
}
