import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TodoListManager {

    public static ArrayList<TodoItem> loadList(File file) {
        ArrayList<TodoItem> list = new ArrayList<>();
        Map<TodoItem, Integer> hierarchy = new HashMap<>();
        TodoItem root = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("::");
                String task = parts[0];
                String description = parts.length > 1 ? parts[1] : "";
                boolean isDone = Boolean.parseBoolean(parts[2]);

                TodoItem item = new TodoItem(task, description);
                item.setDone(isDone);
                list.add(item);

                int level = line.indexOf(task) / 4;
                if (level == 0) {
                    root = item;
                } else {
                    TodoItem parent = hierarchy.entrySet().stream()
                            .filter(entry -> entry.getValue() == (level - 1))
                            .map(Map.Entry::getKey)
                            .findFirst()
                            .orElse(null);
                    assert parent != null;
                    parent.addChild(item);
                }

                hierarchy.put(item, level);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return root != null ? root.getFamily() : list;
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

        for (TodoItem child : item.getChildren()){
            writeTodoItem(writer, child, ++level);
        }
    }
}
