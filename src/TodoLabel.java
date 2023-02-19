import javax.swing.*;

public class TodoLabel extends JLabel {
    private TodoItem item;
    private final JCheckBox checkBox;

    public TodoLabel (TodoItem item){
        super(item.getTask());
        this.item = item;
        checkBox = new JCheckBox();
        checkBox.setSelected(item.getIsDone());

        checkBox.addActionListener(e -> {
            item.setDone(checkBox.isSelected());
        });
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public TodoItem getItem() {
        return item;
    }

    public void setItem(TodoItem item) {
        this.item = item;
    }
}