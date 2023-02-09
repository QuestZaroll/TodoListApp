import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
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
    private ArrayList<JLabel> itemLabels = new ArrayList<>();

    private JFrame frame = new JFrame("ToDo List");

    private JPanel itemsPanel = new JPanel();
    private JPanel descriptPanel = new JPanel();

    private JTextArea descriptText = new JTextArea("init test text");

    /*this implementation presents an issue to me when I'm trying to add sub-items to the gui, I think though
    I could change it to every task being represented in a JLabel, and add sub-items underneath the label
    at an offset. I think this would also make it easy to collapse child trees for a cleaner overall interface
     */
    private DefaultListModel<TodoItem> itemsModel = new DefaultListModel<>();
//    private JList<TodoItem> itemsList;

    private MouseListener itemLabelListener = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {

        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    };

    public TodoApp(){
        addTestData();

        for (int i = 0; i < items.size(); i++) {
            JLabel itemLabel = new JLabel();
            addItemLabel(itemLabel, items.get(i));
        }

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(420, 650);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);

//        itemsList = new JList<>(itemsModel);
//        itemsList.setBackground(Color.GRAY);

//        JScrollPane scrollPane = new JScrollPane(itemLabels);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        for (JLabel label : itemLabels) {
            itemsPanel.add(label);
            System.out.println(label.getText());
        }

        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.add(scrollPane);

        descriptText.setEditable(false);
        descriptText.setLineWrap(true);
        descriptText.setWrapStyleWord(true);
        descriptText.setBackground(Color.DARK_GRAY);
        descriptText.setForeground(Color.BLACK);
        descriptText.setFont(new Font("Consolas", Font.PLAIN, 40));

        descriptPanel.setLayout(new BoxLayout(descriptPanel, BoxLayout.Y_AXIS));
        descriptPanel.add(descriptText);

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                itemsPanel,
                descriptPanel);

        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(2);

        frame.add(splitPane);
        frame.pack();

        frame.setVisible(true);

//        //listeners
//        itemsList.addListSelectionListener(e -> {
//            if (!e.getValueIsAdjusting()) {
//                TodoItem selectedItem = itemsList.getSelectedValue();
//                descriptText.setText(selectedItem.getDescription());
//
//                if (!selectedItem.getChildren().isEmpty()){
//                    //TODO: display family of todo item
//                }
//            }
//        });

    }

    private void addItemLabel(JLabel itemLabel, TodoItem item) {
        itemLabel.setText(item.getTask());
        itemLabel.addMouseListener(itemLabelListener);
        itemLabels.add(itemLabel);
        if(!item.getChildren().isEmpty()){
            ArrayList<TodoItem> children = item.getChildren();
            for (int i = 0; i < children.size(); i++) {
                addItemLabel(new JLabel(), children.get(i), 0);
            }
        }
    }

    private void addItemLabel(JLabel itemLabel, TodoItem item, int offset) {
        itemLabel.setText(item.getTask());
        itemLabel.addMouseListener(itemLabelListener);
        itemLabels.add(itemLabel);
        if(!item.getChildren().isEmpty()){
            ArrayList<TodoItem> children = item.getChildren();
            for (int i = 0; i < children.size(); i++) {
                offset += 10;
                addItemLabel(new JLabel(), children.get(i), offset);
            }
        }
    }

    private void addTestData(){
        //I know this is a terrible implementation of fizzbuzz, just making some test data for task nesting
        for (int i = 0; i < 30; i++) {
            TodoItem item = new TodoItem("test item number: " + (i + 1), "test description: " + (i + 1));
            if((i + 1) % 3 == 0){
                TodoItem childFizz = new TodoItem("Child fizz for item: " + (i + 1), "Fizz!");
                item.addChild(childFizz);
                if((i + 1) % 5 == 0){
                    TodoItem childFizzBuzz = new TodoItem("child fizzbuzz for item: " + (i + 1), "FizzBuzz!");
                    childFizz.addChild(childFizzBuzz);
                }
            }else if ((i + 1) % 5 == 0){
                TodoItem childBuzz = new TodoItem("child buzz for item: " + (i + 1), "Buzz!");
                item.addChild(childBuzz);
            }
            items.add(item);
        }

        for (int i = 0; i < items.toArray().length; i++) {
            itemsModel.addElement(items.get(i));
        }

    }
}
