import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final String CONFIG_FILE_NAME = "config.txt";
    private static ConfigManager instance;
    private Map<String, String> preferences;

    //preference name constants
    public static final String LOAD_LAST_FILE = "Load Last File"; //if app should load last todolist loaded by user upon next startup
    public static final String LAST_FILE = "Last File Loaded";//location of the last file loaded
    public static final String DEFAULT_FOLDER = "Default Folder";//default folder to open the save as / open JFileChooser at
    /*
    potential config options for the future:
    todoitem font style, size, and color
    description text style, size, and color
    items panel background color
    descripttext background color
    label formatting (such as automatically adding capital letters)
     */

    //default preferences
    public static final boolean DEFAULT_LOAD_LAST_FILE = true; //load last file on startup
    public static String lastFileLoaded = "src/Lists/FinishingTouches.txt";//will be loaded from config, if exists
    public static final String DEFAULT_FOLDER_LOCATION = "src/Lists";

    private ConfigManager() {
        // Load preferences from file when the ConfigManager is created
        preferences = new HashMap<>();
        loadPreferences();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void setPreference(String name, String value) {
        preferences.put(name, value);
        savePreferences();
    }

    public String getPreference(String name, String defaultValue) {
        return preferences.getOrDefault(name, defaultValue);
    }

    private void loadPreferences() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    preferences.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            // If the file doesn't exist or there's an error reading it, just use default values
        }
    }

    private void savePreferences() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONFIG_FILE_NAME))) {
            for (Map.Entry<String, String> entry : preferences.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            // If there's an error writing the file, just ignore it
        }
    }
}
