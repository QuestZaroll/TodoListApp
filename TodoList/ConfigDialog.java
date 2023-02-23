import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class ConfigDialog extends JDialog {
    private JLabel loadLastFileLabel = new JLabel("Automatically load last file on startup?");
    private JCheckBox loadLastFile = new JCheckBox();

    private JLabel defaultFolderPath = new JLabel();

    private JButton saveButton = new JButton("Save");
    private JButton setDefaultButton = new JButton("Reset Default");

    public ConfigDialog(JFrame parent){
        super(parent, "Edit Preferences", true);

        this.setSize(300, 200);
        this.setLayout(null);
        this.setLocationRelativeTo(parent);

        loadLastFileLabel.setBounds(10, 10, 250, 20);
        loadLastFile.setBounds(225, 10, 20, 20);

        defaultFolderPath.setBounds(10, 40, 250, 20);

        saveButton.setBounds(200, 120, 75, 20);
        setDefaultButton.setBounds(10, 120, 125, 20);

        this.add(loadLastFileLabel);
        this.add(loadLastFile);
        this.add(defaultFolderPath);
        this.add(saveButton);
        this.add(setDefaultButton);

        loadPreferences();

        setDefaultButton.addActionListener(e -> {
            ConfigManager.getInstance().setDefaultPreferences();
            reDraw();
        });

        saveButton.addActionListener(e -> {
            dispose(); //actually preferences are set when editing so no changes are needed! crazy! >:D
        });

        loadLastFile.addActionListener(e -> {
            ConfigManager.getInstance().setPreference(ConfigManager.LOAD_LAST_FILE, String.valueOf(loadLastFile.isSelected()));
        });

        defaultFolderPath.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                File chosenFile = showChooseFileDialog();
                if(chosenFile != null){
                    if(chosenFile.exists()){
                        ConfigManager.getInstance().setPreference(ConfigManager.DEFAULT_FOLDER, chosenFile.getAbsolutePath());
                        reDraw();
                    }
                }
            }
        });
    }

    private void reDraw(){
        //this isn't working and I don't know why
        for (Component component : this.getComponents()) {
            component.validate();
            component.repaint();
        }
        this.validate();
        this.repaint();
    }

    private void loadPreferences() {
        loadLastFile.setSelected(Boolean.parseBoolean(ConfigManager.getInstance().getPreference(ConfigManager.LOAD_LAST_FILE, String.valueOf(ConfigManager.DEFAULT_LOAD_LAST_FILE))));
        defaultFolderPath.setText(ConfigManager.DEFAULT_FOLDER +  ": " + ConfigManager.getInstance().getPreference(ConfigManager.DEFAULT_FOLDER, ConfigManager.DEFAULT_FOLDER_LOCATION));
    }

    private File showChooseFileDialog() {
        // TODO: 2/22/2023
        /*
        implement file chooser to select new file
         */
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));
        chooser.setDialogTitle("Choose Default Folder");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showOpenDialog(this);

        if(result == JFileChooser.APPROVE_OPTION){
            return new File(chooser.getSelectedFile().getAbsolutePath());
        }
        return null;
    }
}
