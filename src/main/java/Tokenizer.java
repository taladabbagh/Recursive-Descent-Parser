import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokenizer {

    public StringBuilder inputfile;
    private ArrayList<String> generatedTokens;
    private static ArrayList<Integer> lineNumberList;
    private int currentIndex;
    private static int lineNumber;

    public static final List<String> reservedWords = Arrays.asList("module", "begin", "end", "const", "var",
            "integer", "real", "char", "procedure", "mod", "div", "readint", "readchar", "readln", "writeint",
            "writereal", "writechar", "writeln", "if", "then", "elseif", "else", "while", "do", "loop", "until",
            "exit", "call");

    public static final List<String> terminals = Arrays.asList(".", ";", ":", ",", ":=", "|=", "(", ")", "+", "-",
            "*", "/", "%", "=", "<>", "<", "<=", ">", ">=");

    public Tokenizer(StringBuilder inputfile) {
        this.inputfile = inputfile;
        this.generatedTokens = new ArrayList<>(); // list to store the tokens
        this.lineNumberList = new ArrayList<>(); // list to store the line numbers, each one corresponds to its token in
                                                 // the generatedtokens at the same index
        this.currentIndex = 0;
        this.lineNumber = 1; // Line number starts from 1

    }

    public ArrayList<String> tokenize() {
        StringBuilder currentToken = new StringBuilder();

        while (currentIndex < inputfile.length()) {
            char currentChar = inputfile.charAt(currentIndex);

            if (Character.isWhitespace(currentChar) || isTerminalCharacter(currentChar)) {
                // if whitespace or terminal character is encountered, add the current token
                if (currentToken.length() > 0) {
                    addToken(currentToken.toString());
                    lineNumberList.add(lineNumber); // add corresponding line number
                    currentToken.setLength(0);
                }

                if (currentChar == '\n') {
                    // increment line number when a newline character is encountered
                    lineNumber++;
                }

                if (isTerminalCharacter(currentChar)) {
                    // check for special cases
                    String combinedToken = checkForSpecialCases(currentChar);
                    if (combinedToken != null) {
                        addToken(combinedToken);
                        lineNumberList.add(lineNumber); // add corresponding line number
                        currentIndex += combinedToken.length(); // add the special case
                        continue;
                    } else {
                        addToken(String.valueOf(currentChar));
                        lineNumberList.add(lineNumber);
                    }
                }

                currentIndex++;
            } else {
                // check for real numbers
                if (Character.isDigit(currentChar) || currentChar == '.') {
                    boolean isReal = false;

                    while (currentIndex < inputfile.length()) {
                        char nextChar = inputfile.charAt(currentIndex);
                        if (Character.isDigit(nextChar)) {
                            currentToken.append(nextChar);
                        } else if (nextChar == '.') {
                            if (isReal) {
                                // already encountered a decimal point, break loop
                                break;
                            }
                            currentToken.append(nextChar);
                            isReal = true;
                        } else {
                            // stop when encountering non-digit characters
                            break;
                        }
                        currentIndex++;
                    }
                    addToken(currentToken.toString());
                    lineNumberList.add(lineNumber);
                    currentToken.setLength(0); // resetting it to 0 to not retain characters from prev token
                    continue;
                }

                currentToken.append(currentChar);
                currentIndex++;
            }
        }

        if (currentToken.length() > 0) {
            addToken(currentToken.toString());
            lineNumberList.add(lineNumber); // add corresponding line number
        }

        // System.out.println(generatedTokens);
        // System.out.println(lineNumberList); // print the line numbers

        return generatedTokens;
    }

    private String checkForSpecialCases(char currentChar) {
        String combinedToken = null;

        // Check for special cases
        switch (currentChar) {
            case ':':
                if (currentIndex + 1 < inputfile.length() && inputfile.charAt(currentIndex + 1) == '=') {
                    combinedToken = ":=";
                    break;
                }
            case '|':
                if (currentIndex + 1 < inputfile.length() && inputfile.charAt(currentIndex + 1) == '=') {
                    combinedToken = "|=";
                    break;
                }
            case '<':
                if (currentIndex + 1 < inputfile.length() && inputfile.charAt(currentIndex + 1) == '=') {
                    combinedToken = "<=";
                    break;
                } else if (currentIndex + 1 < inputfile.length() && inputfile.charAt(currentIndex + 1) == '>') {
                    combinedToken = "<>";
                    break;
                }
            case '>':
                if (currentIndex + 1 < inputfile.length() && inputfile.charAt(currentIndex + 1) == '=') {
                    combinedToken = ">=";
                    break;
                }
        }
        return combinedToken;
    }

    private boolean isTerminalCharacter(char character) {
        return terminals.contains(String.valueOf(character));
    }

    private void addToken(String token) {
        generatedTokens.add(token);
    }

    public static int getLineNum(int index) {
        int line = lineNumberList.get(index);
        return line;
    }

}