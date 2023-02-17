

import java.util.ArrayList;

public class TodoItem {
    private TodoItem parent;
    private ArrayList<TodoItem> children;

    private String task;
    private String description;
    private boolean isDone;

    public TodoItem(String task){
        this.children = new ArrayList<>();
        this.task = task;
        this.isDone = false;
        this.description = "";
    }

    public TodoItem(String task, String description){
        this.children = new ArrayList<>();
        this.task = task;
        this.isDone = false;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDone(boolean bool){
        this.isDone = bool;
    }

    public boolean getIsDone(){
        return isDone;
    }

    public void addChild(TodoItem item){
        this.children.add(item);
        item.parent = this;
    }

    public void removeChild(TodoItem item){
        children.remove(item);
    }

    public TodoItem getParent(){
        return parent;
    }

    public String getTask(){
        return task;
    }

    public void setTask(String newTask){
        this.task = newTask;
    }

    public ArrayList<TodoItem> getChildren() {
        return children;
    }

    public ArrayList<TodoItem> getFamily(){//neat function but so far not-needed, consider removing
        ArrayList<TodoItem> list = new ArrayList<>(this.getChildren());

        for (TodoItem item : children) {
            list.addAll(item.getFamily());
        }

        return list;
    }

    public TodoItem getRoot(){
        if(this.getParent() == null){
            return this;
        }
        TodoItem item = this.getParent();
        while (item.getParent() != null){
            item = item.getParent();
        }
        return item;
    }

    public TodoItem findDescendant(TodoItem item){
        if(this == item){
            return this;
        }

        for (TodoItem child : children) {
            TodoItem found = child.findDescendant(item);
            if(found != null){
                return found;
            }
        }
        //not found in this item's descendants
        return null;
    }

    //probably unneeded later but keeping for testing right now
    @Override
    public String toString(){
        return task;
    }
}
