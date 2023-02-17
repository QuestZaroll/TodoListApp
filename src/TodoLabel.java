import javax.swing.*;

public class TodoLabel extends JLabel {
    private TodoItem item;

    public TodoLabel (TodoItem item){
        super(item.getTask());
        this.item = item;
    }

    public TodoItem getItem() {
        return item;
    }

    public void setItem(TodoItem item) {
        this.item = item;
    }
}
