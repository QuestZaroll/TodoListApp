import java.io.*;
import java.util.ArrayList;

public class TodoListManager {

    public static ArrayList<TodoItem> loadList(File file) {
        ArrayList<TodoItem> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while ((line = reader.readLine()) != null){
                TodoItem item = loadTodoItem(reader, line, 0);
                list.add(item);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }

    private static TodoItem loadTodoItem(BufferedReader reader, String line, int level) throws IOException {
        String[] parts = line.split("::");
        TodoItem item = new TodoItem(parts[0], parts[1]);
        boolean isDone = parts[2].equals("true");
        item.setDone(isDone);

        // TODO: 2/13/2023 fix load bug
        /*
        potential fix: we need to track the current sub-level, and increment the tabs accordingly
        then as we work our way back, we need to decrement it. this way, the expected tab levels for each
        item will be as follows
        item 1 -- 0
            item 1.1 -- 1
            item 1.2 -- 1
            item 1.3 -- 1
                item 1.3.1 -- 2
            item 1.4 -- 1
        item 2 -- 0
            item 2.1 -- 1

         a sample execution would be:
         load item 1, check to see if next line has indentation
            item has indentation above current level, add to item 1 children
            next item is not above current level, add to item 1 children
            next item is not above current level, add to item 1 children
                next item is above current level, add to item 1.3 children
                next item is below current level,
            add to item 1 children
            next item is below current level
        load item 2 as a root item, check children
            above current level, add to item 2 children
        no more items, close reader
         */
        reader.mark(100);
        boolean isChild = reader.read() == '\t';
        if(isChild){
            level++;
        }
        reader.reset();
        if(level > 0) {
            line = reader.readLine();
            TodoItem child = loadTodoItem(reader, line, --level);
            item.addChild(child);
        }
        return item;
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
            sb.append("\t");
        }

        sb.append(item.getTask()).append("::").append(item.getDescription()).append("::").append(item.getIsDone());
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
