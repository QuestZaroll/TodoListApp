import javax.swing.*;
import java.awt.*;
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

    private JTextArea descriptText = new JTextArea("init test text");

    private DefaultListModel<TodoItem> itemsModel = new DefaultListModel<>();
    private JList<TodoItem> itemsList;

    private TodoItem item1 = new TodoItem("test item one", "test description for item one");
    private TodoItem item2 = new TodoItem("test item two", "test description for item two");
    private TodoItem childItem1 = new TodoItem("child test item", "child of item one");

    public TodoApp(){

        for (int i = 0; i < 25; i++) {
            TodoItem item = new TodoItem("test item number: " + (i + 1), "test description: " + (i + 1));
            items.add(item);
        }
        for (int i = 0; i < items.toArray().length; i++) {
            itemsModel.addElement(items.get(i));
        }
        itemsList = new JList<>(itemsModel);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(420, 650);
        frame.setResizable(true);



        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.BLUE);

        JScrollPane scrollPane = new JScrollPane(itemsList);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        itemsPanel.add(scrollPane);

        descriptText.setEditable(false);
        descriptText.setLineWrap(true);
        descriptText.setWrapStyleWord(true);
        descriptPanel.setLayout(new BoxLayout(descriptPanel, BoxLayout.Y_AXIS));
        descriptPanel.setBackground(Color.RED);
        descriptPanel.add(descriptText);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                itemsPanel,
                descriptPanel);

        splitPane.setResizeWeight(0.5);

        frame.add(splitPane);

        frame.setVisible(true);
    }
}
