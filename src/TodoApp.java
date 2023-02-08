import javax.swing.*;
import java.util.ArrayList;

//implement GUI and App logic here
/*

--parameters for completion
app should be able to save, load, and create new to-do lists.
new items should be able to be added either through a menu option or right-click.
additionally, sub-items should be able to be added to each to-do item.
--

--bonus items to polish app and improve user functionality
if possible, allow restructuring the to-do lists by dragging and dropping items

allow users to select a to-do item and input a description to the task itself,
which will be displayed to the side when selected

implement style changing for fonts and whatnot

huge big project: turn this into a mobile app
*/
public class TodoApp {
    private ArrayList<TodoItem> items = new ArrayList<>();

    private JFrame frame = new JFrame("ToDo List");

    private JPanel itemsPanel = new JPanel();
    private JPanel descriptPanel = new JPanel();
    JList<TodoItem> list = new JList<>();

    //maybe use panels to implement the list, scrollbar, and description panes

    public TodoApp(){
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(420, 650);
        frame.setLayout(null);
        frame.setResizable(true);



        frame.setVisible(true);
    }
}
