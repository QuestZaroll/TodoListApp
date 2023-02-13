import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
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

    //temporary, TODO: change implementation to better associate labels with items
    private ArrayList<TodoItem> itemsWithChild = new ArrayList<>();
    private ArrayList<JLabel> itemLabels = new ArrayList<>();

    private JFrame frame = new JFrame("ToDo List");

    private JPanel itemsPanel = new JPanel();
    private JPanel descriptPanel = new JPanel();

    private JTextArea descriptText = new JTextArea("init test text");

    private JMenuBar menuBar = new JMenuBar();

    private JMenu fileMenu = new JMenu("File");
    private JMenuItem saveListMenu = new JMenuItem("Save List");
    private JMenuItem openListMenu = new JMenuItem("Open List");
    private JMenuItem newListMenu = new JMenuItem("New List");
    private JMenuItem newItemMenu = new JMenuItem("New Item");

    private MouseAdapter labelMouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getButton() == 1){
                if(e.getSource().getClass() == JLabel.class){
                    JLabel label = (JLabel) e.getSource();
                    if(itemLabels.contains(label)){
                        int index = itemLabels.indexOf(label);
                        descriptText.setText(itemsWithChild.get(index).getDescription());
                    }
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            //TODO: implement dragging items
        }
    };

    public TodoApp(){
//        addTestData(); //temporary test data, later implement saving the last opened file
        //to a file, and check that file upon initialization to load that list instead

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(420, 650);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);

        fileMenu.add(saveListMenu);
        fileMenu.add(openListMenu);
        fileMenu.add(newListMenu);
        fileMenu.add(newItemMenu);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        itemsPanel.setLayout(null);

        initializeList();

        itemsPanel.setPreferredSize(new Dimension(frame.getWidth() - 50, itemLabels.size() * 20));

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(9);

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
                scrollPane,
                descriptPanel);

        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(2);

        frame.add(splitPane);

        frame.setVisible(true);

        //listeners
        saveListMenu.addActionListener(e -> {
            saveTodoList();
        });
        openListMenu.addActionListener(e ->{
            loadTodoList();
        });
        newListMenu.addActionListener(e -> {
            newTodoList();
        });
        newItemMenu.addActionListener(e -> {
            addNewItem();
        });
    }

    private void initializeList() {
        for (TodoItem item : items) {
            JLabel itemLabel = new JLabel();
            addItemLabel(itemLabel, item, 0);
        }

        for (JLabel label : itemLabels) {
            itemsPanel.add(label);
        }
    }

    private void saveTodoList() {
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter);

        int returnValue = fileChooser.showSaveDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION){
            File selectedFile = fileChooser.getSelectedFile();

            TodoListManager.saveList(selectedFile, items);
        }
    }

    private void loadTodoList() {
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(null);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            ArrayList<TodoItem> newList = TodoListManager.loadList(selectedFile);
            newTodoList();
            items = newList;
            initializeList();
            itemsPanel.repaint();
            frame.repaint();
        }
    }

    private void newTodoList() {
        items.clear();
        itemsWithChild.clear();
        itemLabels.clear();

        itemsPanel.removeAll();

        descriptText.setText("");
        itemsPanel.repaint();
        frame.repaint();
    }

    private void addNewItem() {
    }

    public void addItemLabel(JLabel itemLabel, TodoItem item, int offset) {
        itemLabel.setText(item.getTask());
        itemLabel.addMouseListener(labelMouseAdapter);
        itemLabels.add(itemLabel);
        itemsWithChild.add(item);

        int index = itemLabels.indexOf(itemLabel);
        itemLabel.setBounds(15 * offset, 20 * index, 200, 20);

        if(!item.getChildren().isEmpty()){
            ArrayList<TodoItem> children = item.getChildren();
            for (TodoItem child : children) {
                ++offset;
                addItemLabel(new JLabel(), child, offset);
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
                    TodoItem childFizzBuzz = new TodoItem("Child fizzbuzz for item: " + (i + 1), "FizzBuzz!");
                    childFizz.addChild(childFizzBuzz);
                }
            }else if ((i + 1) % 5 == 0){
                TodoItem childBuzz = new TodoItem("Child buzz for item: " + (i + 1), "Buzz!");
                item.addChild(childBuzz);
            }
            items.add(item);
        }
    }
}
