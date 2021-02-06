package kelbot;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javafx.application.Platform;

public class Kelbot {
    private Path path = Paths.get(".", "data", "Kelbot.txt");
    private Storage storage;
    private TaskList taskList;
    private UI ui;
    /**
     * Initializes Kelbot
     */
    public Kelbot() {
        ui = new UI();
        storage = new Storage(path);
        try {
            taskList = storage.load();
        } catch (KelbotException e) {
            System.out.println(e.getMessage());
            taskList = new TaskList();
        }
    }
    public String getResponse(String input) {
        String response;
        try {
            Parser parser = new Parser(input);
            if (!parser.getIsValid()) {
                return "Invalid Command!";
            }
            assert parser.getIsValid();
            try {
                Command command = parser.getCommand();
                Integer taskNumber = parser.getTaskNumber();
                String keyword = parser.getKeyword();
                String taskName = parser.getTaskName();
                LocalDate date = parser.getDate();
                if (command == Command.BYE) {
                    response = ui.sayGoodbye();
                    Platform.exit();
                } else if (command == Command.LIST) {
                    response = ui.printList(taskList);
                } else if (command == Command.DONE || command == Command.DELETE) {
                    try {
                        assert !taskList.getTaskList().isEmpty();
                        if (command == Command.DONE) {
                            Task task = taskList.complete(taskNumber);
                            response = ui.printDone(task);
                        } else {
                            Task task = taskList.delete(taskNumber);
                            response = ui.printDelete(task);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        response = "The list is not that long!";
                    }
                } else if (command == Command.FIND) {
                    try {
                        if (keyword.equals("")) {
                            throw new KelbotException("Keyword cannot be empty!");
                        } else {
                            assert !taskList.getTaskList().isEmpty();
                            TaskList taskListToPrint = new TaskList(taskList.search(keyword));
                            if (taskListToPrint.toString().equals("")) {
                                response = "No tasks match your search!";
                            } else {
                                response = ui.printRelevantTasks(taskListToPrint);
                            }
                        }
                    } catch (KelbotException e) {
                        response = e.getMessage();
                    }
                } else {
                    try {
                        if (taskName == "") {
                            throw new KelbotException("Task name cannot be empty!");
                        } else if (command == Command.TODO) {
                            TodoTask newTodoTask = new TodoTask(taskName);
                            taskList.add(newTodoTask);
                            response = ui.printAdd(newTodoTask, taskList.getSize());
                        } else if (date == null) {
                            throw new KelbotException("Date cannot be empty!");
                        } else if (command == Command.DEADLINE) {
                            DeadlineTask newDeadlineTask = new DeadlineTask(taskName, date);
                            taskList.add(newDeadlineTask);
                            response = ui.printAdd(newDeadlineTask, taskList.getSize());
                        } else {
                            EventTask newEventTask = new EventTask(taskName, date);
                            taskList.add(newEventTask);
                            response = ui.printAdd(newEventTask, taskList.getSize());
                        }
                    } catch (KelbotException e) {
                        response = e.getMessage();
                    }
                }
            } catch (DateTimeParseException e) {
                response = "Invalid Date!";
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            response = "Which task are you referring to?";
        } catch (DateTimeParseException e) {
            response = "Date cannot be empty!";
        }
        storage.save(taskList.getTaskList());
        return response;
    }
    /**
     * Gets Task List
     *
     * @return Task List
     */
    public TaskList getTaskList() {
        return taskList;
    }
}
