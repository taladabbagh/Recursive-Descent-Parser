import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class MainTest1 {

    public static void main(String[] args) {

        JFileChooser fileChooser = new JFileChooser();

        // show the file chooser dialog
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            // get the selected file
            Path selectedFile = fileChooser.getSelectedFile().toPath();

            try {
                // read the content of the selected file into a string
                String program = new String(Files.readAllBytes(selectedFile));

                // tokenize the input program
                Tokenizer tokenizer = new Tokenizer(new StringBuilder(program));
                ArrayList<String> tokens = tokenizer.tokenize();

                Parser1 parser = new Parser1(tokens);

                boolean parsingResult = parser.module_decl();

                // print parsing result
                if (parsingResult) {
                    System.out.println("Parsing successful!");
                } else {
                    System.out.println("Parsing failed!");
                }

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error reading the program file: " + e.getMessage());
            }
        } else {
            System.out.println("File selection canceled.");
        }
    }
}