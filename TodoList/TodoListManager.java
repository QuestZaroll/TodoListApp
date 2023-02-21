import java.io.*;
import java.util.ArrayList;

public class TodoListManager {

    /*
    due to how the file is loaded, if a return character is included in the description, the loading will throw
    an exception due to the next line being empty. we've replaced the return character with an indicator to let
    the program know to insert a return when it re-loads the list
     */
    private static final String RETURN_INDICATOR = "%RETURN%";
    private static final char CHILD_ITEM_INDICATOR = '\t';

    public static ArrayList<TodoItem> loadList(File file) {
        ArrayList<TodoItem> items = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            TodoItem currentItem = null;
            int currentIndent = 0;
            while ((line = reader.readLine()) != null) {
                int indentLevel = getIndentLevel(line);
                String[] parts = line.trim().split("::");
                String task = parts[0];
                String description = parts[1];
                description = description.replaceAll(RETURN_INDICATOR, "\n");
                boolean isDone = Boolean.parseBoolean(parts[2]);

                TodoItem item = new TodoItem(task, description);
                item.setDone(isDone);

                if (indentLevel == 0) {
                    items.add(item);
                    currentItem = item;
                    currentIndent = 0;
                } else if (indentLevel == currentIndent + 1) {
                    currentItem.addChild(item);
                    currentItem = item;
                    currentIndent++;
                } else if (indentLevel <= currentIndent) {
                    int levelDiff = currentIndent - indentLevel + 1;
                    for (int i = 0; i < levelDiff; i++) {
                        currentItem = currentItem.getParent();
                        currentIndent--;
                    }
                    currentItem.addChild(item);
                    currentItem = item;
                    currentIndent++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;
    }

    private static int getIndentLevel(String line) {
        int count = 0;
        for (char c : line.toCharArray()) {
            if (c == CHILD_ITEM_INDICATOR) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }


    public static void saveList(File file, ArrayList<TodoItem> list){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (TodoItem item: list) {
                writeTodoItem(writer, item, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeTodoItem(BufferedWriter writer, TodoItem item, int level) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append(CHILD_ITEM_INDICATOR);
        }

        String description = item.getDescription().replaceAll("\n", RETURN_INDICATOR);

        sb.append(item.getTask()).append("::").append(description).append("::").append(item.getIsDone());
        writer.write(sb.toString());
        writer.newLine();

        if(!item.getChildren().isEmpty()){
            level++;
        }
        for (TodoItem child : item.getChildren()){
            writeTodoItem(writer, child, level);
        }
    }
}
