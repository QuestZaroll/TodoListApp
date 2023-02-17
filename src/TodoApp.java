import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

/*
--parameters for completion
app should be able to save, load, and create new to-do lists. !*! completed !*!
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
    private ArrayList<TodoLabel> itemLabels = new ArrayList<>();

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

    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem editItemPop = new JMenuItem("Edit");
    private JMenuItem subItemPop = new JMenuItem("Add sub-item");
    private JMenuItem deleteItemPop = new JMenuItem("Delete");

    private MouseAdapter labelMouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getButton() == 1){
                if(e.getSource().getClass() == TodoLabel.class){
                    TodoLabel label = (TodoLabel) e.getSource();
                    if(itemLabels.contains(label)){
                        int index = itemLabels.indexOf(label);
                        descriptText.setText(itemLabels.get(index).getItem().getDescription());
                    }
                }
            }
            if(e.getButton() == 3){
                if(e.getSource().getClass() == TodoLabel.class) {
                    TodoLabel label = (TodoLabel) e.getSource();
                    popupMenu.show(label, e.getX(), e.getY());
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            //TODO: implement dragging items
        }
    };

    public TodoApp(){
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

        popupMenu.add(editItemPop);
        popupMenu.add(subItemPop);
        popupMenu.add(deleteItemPop);

        itemsPanel.setLayout(null);

        initializeList();

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

        //File menu
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

        //Right click popup
        editItemPop.addActionListener(e -> {
            if(popupMenu.getInvoker() instanceof TodoLabel){
                TodoLabel label = (TodoLabel) popupMenu.getInvoker();
                if(itemLabels.contains(label)){
                    int index = itemLabels.indexOf(label);
                    ItemDialog itemDialog = new ItemDialog(frame, item -> {
                        itemLabels.get(index).setText(item.getTask());
                        itemLabels.get(index).getItem().setTask(item.getTask());
                        itemLabels.get(index).getItem().setDescription(item.getDescription());

                    });
                    itemDialog.setVisible(true);
                }
            }
        });

        subItemPop.addActionListener(e -> {
            if(popupMenu.getInvoker() instanceof TodoLabel){
                TodoLabel label = (TodoLabel) popupMenu.getInvoker();
                if(itemLabels.contains(label)){
                    int index = itemLabels.indexOf(label);
                    ItemDialog itemDialog = new ItemDialog(frame, item -> {

                        TodoItem descendant = itemLabels.get(index).getItem();
                        TodoItem parentalUnit = descendant.getParent();

                        parentalUnit = parentalUnit.getRoot();

                        if(items.contains(parentalUnit)){
                            int index1 = items.indexOf(parentalUnit);
                            items.get(index1).findDescendant(descendant).addChild(item);
                        }
                    });

                    itemDialog.setVisible(true);
                    itemsPanel.removeAll();
                    itemLabels.clear();
                    initializeList();
                    frame.validate();
                    frame.repaint();
                }
            }
        });
        deleteItemPop.addActionListener(e -> {
            //open "are you sure?" dialogue, if sure, remove item and label from
            //items, itemsWithChild, and itemLabels
            //re-initialize list to update changes
            System.out.println("delete clicked");
        });
    }

    private void initializeList() {

        for (TodoItem item : items) {
            TodoLabel itemLabel = new TodoLabel(item);
            addItemLabel(itemLabel, item, 0);
        }

        for (TodoLabel label : itemLabels) {
            itemsPanel.add(label);
        }
        itemsPanel.setPreferredSize(new Dimension(frame.getWidth() - 50, itemLabels.size() * 20));
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
        itemLabels.clear();

        itemsPanel.removeAll();

        descriptText.setText("");
        itemsPanel.repaint();
        frame.repaint();
    }

    private void addNewItem() {
        // TODO: 2/15/2023
        /*
        bring up a dialog box to create a new item, have a field for task, and description
        save new item to items list and re-initialize list
         */
    }

    public void addItemLabel(TodoLabel itemLabel, TodoItem item, int offset) {
        itemLabel.setText(item.getTask());
        itemLabel.addMouseListener(labelMouseAdapter);
        itemLabel.setItem(item);
        itemLabels.add(itemLabel);

        int index = itemLabels.indexOf(itemLabel);
        itemLabel.setBounds(15 * offset, 20 * index, 200, 20);

        if(!item.getChildren().isEmpty()){
            ArrayList<TodoItem> children = item.getChildren();
            for (TodoItem child : children) {
                addItemLabel(new TodoLabel(child), child, ++offset);
                offset--;
            }
        }
    }
}
