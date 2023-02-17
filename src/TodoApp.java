import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;


public class TodoApp {

    private ArrayList<TodoItem> items = new ArrayList<>();
    private ArrayList<TodoLabel> itemLabels = new ArrayList<>();

    private JFrame frame = new JFrame("ToDo List");

    private JPanel itemsPanel = new JPanel();
    private JPanel descriptPanel = new JPanel();

    private JTextArea descriptText = new JTextArea("Load a list or create an item :)");

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
            if(e.getButton() == 1){//left click
                if(e.getSource().getClass() == TodoLabel.class){
                    TodoLabel label = (TodoLabel) e.getSource();
                    if(itemLabels.contains(label)){
                        int index = itemLabels.indexOf(label);
                        descriptText.setText(itemLabels.get(index).getItem().getDescription());
                    }
                }
            }
            if(e.getButton() == 3){//right click
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
                        //item is root item, simply add here
                        if(items.contains(itemLabels.get(index).getItem())){
                            items.get(items.indexOf(itemLabels.get(index).getItem())).addChild(item);
                        }else{
                            //this is a sub item, we must search for it
                            TodoItem descendant = itemLabels.get(index).getItem();
                            TodoItem parentalUnit = descendant.getParent();

                            if(parentalUnit.getParent() != null){
                                parentalUnit = parentalUnit.getRoot();
                            }

                            if(items.contains(parentalUnit)){
                                int index1 = items.indexOf(parentalUnit);
                                items.get(index1).findDescendant(descendant).addChild(item);
                            }
                        }
                    });

                    itemDialog.setVisible(true);
                    reDrawList();
                }
            }
        });
        deleteItemPop.addActionListener(e -> {
            if(popupMenu.getInvoker() instanceof TodoLabel){
                TodoLabel label = (TodoLabel) popupMenu.getInvoker();
                if(itemLabels.contains(label)){
                    int result = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete this item?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION);
                    if(result == JOptionPane.YES_OPTION){
                        int index = itemLabels.indexOf(label);
                        deleteItem(itemLabels.get(index).getItem());
                    }
                }
            }
        });
    }

    private void deleteItem(TodoItem item) {
        if(descriptText.getText().equals(item.getDescription())){
            descriptText.setText("");
        }

        if(item.getParent() == null){ //item is root item
            items.remove(items.get(items.indexOf(item)));
        }else {
            //item was not root, we must find the item and remove it from the appropriate parent
            TodoItem parent = item.getParent();

            parent = parent.getRoot();

            if(items.contains(parent)){
                //this is convoluted, but basically we're finding the descendant in the items list,
                //then getting the parent, then removing the descendant from the parent's children
                items.get(items.indexOf(parent)).findDescendant(item).getParent().removeChild(item);
            }
        }
        reDrawList();
    }

    private void initializeList() {
        for (TodoItem item : items) {
            TodoLabel itemLabel = new TodoLabel(item);
            addItemLabel(itemLabel, item, 0);
        }
        //rest of this code might be better suited in the addItemLabel function
        for (TodoLabel label : itemLabels) {
            itemsPanel.add(label);
            itemsPanel.add(label.getCheckBox());
        }
        itemsPanel.setPreferredSize(new Dimension(frame.getWidth() - 50, itemLabels.size() * 20));
    }

    private void reDrawList(){
        itemsPanel.removeAll();
        itemLabels.clear();
        initializeList();
        frame.validate();
        frame.repaint();
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
            reDrawList();
        }
    }

    private void newTodoList() {
        items.clear();
        itemLabels.clear();
        itemsPanel.removeAll();
        descriptText.setText("");
        reDrawList();
    }

    private void addNewItem() {
        ItemDialog itemDialog = new ItemDialog(frame, item -> {
            items.add(item);
        });
        itemDialog.setVisible(true);
        reDrawList();
    }

    public void addItemLabel(TodoLabel itemLabel, TodoItem item, int offset) {
        itemLabel.setText(item.getTask());
        itemLabel.addMouseListener(labelMouseAdapter);
        itemLabel.setItem(item);
        itemLabels.add(itemLabel);

        int index = itemLabels.indexOf(itemLabel);

        itemLabel.setBounds(20 + (15 * offset), 20 * index, 200, 20);
        itemLabel.getCheckBox().setBounds(itemLabel.getX() - 20, itemLabel.getY(), 20, 20);

        if(!item.getChildren().isEmpty()){
            ArrayList<TodoItem> children = item.getChildren();
            for (TodoItem child : children) {
                addItemLabel(new TodoLabel(child), child, ++offset);
                offset--;
            }
        }
    }
}
