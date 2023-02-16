import javax.swing.*;
import java.awt.*;

public class ItemDialog extends JDialog {
    private JTextField taskField;
    private JTextArea descriptionArea;
    private JButton okButton;
    private JButton cancelButton;
    private ItemDialogListener listener;

    public ItemDialog(JFrame parent, ItemDialogListener listener){
        super(parent, "Todo Item Editor", true);

        taskField = new JTextField(20);
        descriptionArea = new JTextArea(5, 20);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(new JLabel("Task:"));
        panel.add(taskField);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(descriptionArea));
        panel.add(okButton);
        panel.add(cancelButton);
        getContentPane().add(panel);

        pack();
        setLocationRelativeTo(parent);

        okButton.addActionListener(e -> {
            String task = taskField.getText();
            String description = descriptionArea.getText();
            if(!task.isBlank()){
                TodoItem item = new TodoItem(task, description);
                listener.onItemCreated(item);
                dispose();
            }
        });
        cancelButton.addActionListener(e -> {
            dispose();
        });

        this.listener = listener;
    }

    public interface ItemDialogListener {
        void onItemCreated(TodoItem item);
    }

    public JTextField getTaskField() {
        return taskField;
    }

    public JTextArea getDescriptionArea() {
        return descriptionArea;
    }
}
