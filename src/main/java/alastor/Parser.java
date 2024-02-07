package alastor;

import alastor.command.AddCommand;
import alastor.command.Command;
import alastor.command.DeleteCommand;
import alastor.command.ExitCommand;
import alastor.command.InvalidCommand;
import alastor.command.ListCommand;
import alastor.command.MarkCommand;
import alastor.task.Deadline;
import alastor.task.Event;
import alastor.task.Task;
import alastor.task.ToDo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Parser {

    public enum ParseType {
        FILE {
            @Override
            public String getErrorMessage() {
                return "I'm afraid the file I tried reading is corrupted, my dear.\n";
            }
        },
        COMMAND {
            @Override
            public String getErrorMessage() {
                return "I'm afraid I've encountered an error while parsing the command, my dear.\n";
            }
        };

        public abstract String getErrorMessage();
    }

    public static Integer stringToInt(String integer) throws AlastorException {
        try {
            return Integer.parseInt(integer);
        } catch (NumberFormatException e) {
            throw new AlastorException(ParseType.COMMAND.getErrorMessage()
                    + "The number might be in an incorrect format, it should be an integer");
        }
    }

    public static LocalDateTime stringToDateTime(String dateTime, ParseType type) throws AlastorException {
        try {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        } catch (DateTimeParseException e) {
            throw new AlastorException(type.getErrorMessage()
                    + "The date and time might be in an incorrect format, it should be dd-MM-yyyy HH:mm");
        }
    }

    public static void checkParameters(String[] arguments, int expectedNumber, ParseType type) throws AlastorException {
        if (arguments.length != expectedNumber) {
            if (arguments.length < expectedNumber) {
                throw new AlastorException(type.getErrorMessage()
                        + "There might be some missing parameters or invalid separators");
            } else {
                throw new AlastorException(type.getErrorMessage()
                        + "There might be some extra invalid parameters");
            }
        }
        for (String i : arguments) {
            if (i.isBlank()) {
                throw new AlastorException(type.getErrorMessage()
                        + "There might be some empty parameters");
            }
        }
    }

    public static void parseFile(String line, ArrayList<Task> list, int index) throws AlastorException {
        ParseType type = ParseType.FILE;
        String[] parameters = line.split("\\| ", 5);
        switch (parameters[0].trim()) {
        case "T":
            checkParameters(parameters, 3, type);
            list.add(new ToDo(parameters[2].trim()));
            break;
        case "D":
            checkParameters(parameters, 4, type);
            list.add(new Deadline(parameters[2].trim(), stringToDateTime(parameters[3].trim(), ParseType.FILE)));
            break;
        case "E":
            checkParameters(parameters, 5, type);
            list.add(new Event(parameters[2].trim(), stringToDateTime(parameters[3].trim(), ParseType.FILE),
                    stringToDateTime(parameters[4].trim(), ParseType.FILE)));
            break;
        default:
            throw new AlastorException(type.getErrorMessage()
                    + "There might be some invalid task types or separators");
        }

        String marking = parameters[1].trim();
        if (marking.equals("0") || marking.equals("1")) {
            if (marking.equals("1")) {
                list.get(index).mark();
            }
        } else {
            throw new AlastorException(type.getErrorMessage()
                    + "There might be some invalid marking values");
        }
    }

    public static Command parseCommand(String command) throws AlastorException {
        ParseType type = ParseType.COMMAND;
        String[] parameters = command.trim().split(" ", 2);
        switch (parameters[0]) {
        case "list":
            return new ListCommand();
            // Fallthrough
        case "mark":
            checkParameters(parameters, 2, type);
            return new MarkCommand(stringToInt(parameters[1]), true);
            // Fallthrough
        case "unmark":
            checkParameters(parameters , 2, type);
            return new MarkCommand(stringToInt(parameters[1]), false);
            // Fallthrough
        case "todo":
            checkParameters(parameters, 2, type);
            return new AddCommand(new ToDo(parameters[1]));
            // Fallthrough
        case "deadline":
            checkParameters(parameters, 2, type);
            String[] deadlineArgs = parameters[1].split("/by ", 2);
            checkParameters(deadlineArgs, 2, type);
            return new AddCommand(new Deadline(deadlineArgs[0].trim(), stringToDateTime(deadlineArgs[1].trim(),
                    ParseType.COMMAND)));
            // Fallthrough
        case "event":
            checkParameters(parameters, 2, type);
            String[] eventArgs = parameters[1].split("/from|/to", 3);
            checkParameters(eventArgs, 3, type);
            return new AddCommand(new Event(eventArgs[0].trim(), stringToDateTime(eventArgs[1].trim(),
                    ParseType.COMMAND), stringToDateTime(eventArgs[2].trim(), ParseType.COMMAND)));
            // Fallthrough
        case "delete":
            checkParameters(parameters, 2, type);
            return new DeleteCommand(stringToInt(parameters[1]));
            // Fallthrough
        case "bye":
            return new ExitCommand();
            // Fallthrough
        default:
            return new InvalidCommand();
        }
    }
}
