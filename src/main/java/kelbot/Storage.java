package kelbot;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Storage {
    
    private java.nio.file.Path path;
    private boolean fileExists;
    
    public Storage(java.nio.file.Path path) {
        this.path = path;
        this.fileExists = java.nio.file.Files.exists(path);
    }
    
    public TaskList load() throws KelbotException {
        if (fileExists) {
            try {
                FileInputStream fis = new FileInputStream(this.path.toString());
                ObjectInputStream ois = new ObjectInputStream(fis);
                TaskList taskList = new TaskList((ArrayList<Task>) ois.readObject());
                ois.close();
                System.out.println("Here is your task list from your previous Kelbot usage");
                System.out.println(taskList);
                return taskList;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            throw new KelbotException("No existing data file detected. A new file will be created");
        }
        return null;
    }
    
    public void save(ArrayList<Task> taskList) {
        try {
            FileOutputStream fos = new FileOutputStream(this.path.toString());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(taskList);
            oos.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}