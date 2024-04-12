//Ali Bokaei
//Maryam Amir Shahkarami


import java.io.*;
import java.util.*;

public class SimpleShell {
    private static String lastCommand = "";
    private static List<String> commandHistory = new ArrayList<>();

    public static void main(String[] args) {

        String command = "";
        boolean shouldRun = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(System.out, true);

        while (shouldRun) {
            System.out.print("OSshell$ ");
            System.out.flush();
            try {
                command = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            if (command.equals("exit")) {
                shouldRun = false;
                continue;
            }

            if (command.equals("history")) {
                showHistory();
                continue;
            }

            if (command.equals("clearHistory")) {
                clearHistory();
                continue;
            }

            if (command.equals("!!")) {
                System.out.println(lastCommand);
                continue;
            }

            List<String> tokens = tokenize(command);
            String[] cmdArray = tokens.toArray(new String[0]);
            if (cmdArray.length > 0) {
                execute(cmdArray, writer);
            }

            lastCommand = command;
            saveCommand(command);
        }
    }

    private static List<String> tokenize(String command) {
        List<String> tokens = new ArrayList<>();
        StringBuilder token = new StringBuilder();
        boolean inQuotes = false;

        for (char c : command.toCharArray()) {
            if (c == ' ') {
                if (!inQuotes && token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                } else {
                    token.append(c);
                }
            } else if (c == '"') {
                inQuotes = !inQuotes;
            } else {
                token.append(c);
            }
        }

        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        return tokens;
    }

    private static void execute(String[] cmdArray, PrintWriter writer)  {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmdArray);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader processOutput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = processOutput.readLine()) != null) {
                writer.println(line);
            }
            p.waitFor();
        } catch (IOException e) {
            // در صورت خطا
//            e.printStackTrace();
            System.out.println("This Command is Not Supported or Not Defined.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showHistory() {
        System.out.println("Command History:");
        for (int i = 0; i < commandHistory.size(); i++) {
            System.out.println((i + 1) + ". " + commandHistory.get(i));
        }
    }

    private static void clearHistory() {
        commandHistory.clear();
        new File("cmdHistory.txt").delete();
    }

    private static void saveCommand(String command) {
        commandHistory.add(command);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("cmdHistory.txt", true))) {
            writer.write(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}