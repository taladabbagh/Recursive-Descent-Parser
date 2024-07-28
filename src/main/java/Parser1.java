import java.util.ArrayList;

public class Parser1 {

    private ArrayList<String> tokens;
    private int currentIndex;
    private String currentToken;

    private String procName;

    private String moduleName;

    public Parser1(ArrayList<String> tokens) {
        this.tokens = tokens;
        this.currentIndex = 0;
        this.currentToken = getNextToken();
    }

    private String getNextToken() {
        if (currentIndex < tokens.size()) {
            return tokens.get(currentIndex++);
        } else {
            return "$"; // end of input
        }
    }

    private String peekNextToken() {
        // String tempCurrent = currentToken;
        int tempIndex = currentIndex;
        if (tempIndex < tokens.size()) {
            return tokens.get(tempIndex++);
        } else {
            return "$"; // end of input
        }
    }

    private boolean matchToken(String expected) {
        if (currentToken.equals(expected)) {
            currentToken = getNextToken();
            return true;
        } else {
            return false;
        }
    }

    public boolean module_decl() {
        module_heading();
        declarations();
        procedure_decl();
        block();
        name();
        if (currentToken.equals(moduleName)) {
            currentToken = getNextToken();
            if (matchToken(".")) {
                // System.out.println("Parsed Successfully <3");
                return true;
            } else {
                System.out.println(currentToken);
                return false;
            }
        } else {
            error("Module name does not match");
            return false;
        }
    }

    // parses a module heading
    private void module_heading() {
        if (!matchToken("module")) {
            error("Expected 'module', but found'" + currentToken + "'");
        }
        if (!name()) {
            error("Expected name but found'" + currentToken + "'");
        } else {
            moduleName = currentToken;
            currentToken = getNextToken();
        }
        if (!matchToken(";")) {
            error("Expected ';', but found'" + currentToken + "'");
        }
    }

    // parses declarations
    private void declarations() {
        const_decl();
        var_decl();
    }

    // parses constant declarations
    private void const_decl() {

        if (currentToken.equals("const")) {
            matchToken("const");
            const_list();
        } else {
            // Handle any other logic related to constant declarations
        }
    }

    // parses a constant list
    private void const_list() {

        while (name()) {
            currentToken = getNextToken();
            if (matchToken("=")) {
                if (value()) {
                    currentToken = getNextToken();
                    if (!matchToken(";")) {
                        error("Expected ';', but found'" + currentToken + "'");
                        break;
                    }
                } else {
                    error("Expected value but found'" + currentToken + "'");
                    break;
                }
            } else {
                error("Expected = but found'" + currentToken + "'");
                break;
            }
        }
    }

    // parses a variable declaration
    private void var_decl() {
        if (currentToken.equals("var")) {
            matchToken("var");
            var_list();

        } else {
            // Handle any other logic related to constant declarations
        }
    }

    // parses a variable list
    private void var_list() {
        do {
            if (var_item()) {
                if (!matchToken(";")) {
                    error("Expected ';', but found'" + currentToken + "'");
                    break;
                }
            } else {
                break;
            }
        } while (true);
        // no need to return anything since it's always gonna stop when the list is done
    }

    // parses a variable item
    private boolean var_item() {
        if (name_list()) {
            if (matchToken(":")) {
                if (data_type()) { // data type
                    return true;
                } else {
                    error("Expected integer or real or char, but found'" + currentToken + "'");
                    return false;
                }
            } else {
                error("Expected ':', but found'" + currentToken + "'");
                return false;
            }
        } else {
            return false;
        }
    }

    // parses a name list
    private boolean name_list() {

        if (name()) {
            currentToken = getNextToken();
            do {
                if (matchToken(",")) {
                    if (name()) {
                        currentToken = getNextToken();
                        continue;
                    } else {
                        error("Expected name but found'" + currentToken + "'");
                        return false;
                    }
                } else {
                    return true;
                }
            } while (true);
        } else {
            return false;
        }
    }

    // parses a data type
    private boolean data_type() {

        if (matchToken("integer") || matchToken("real") || matchToken("char")) {
            return true;
        } else
            return false;
    }

    // parses a procedure declaration
    private void procedure_decl() {
        procedure_heading();
        declarations();
        block();
        name();
        currentToken = getNextToken();
        if (!matchToken(";")) {
            error("Expected ';', but found'" + currentToken + "'");
        }
    }

    // parses a procedure heading
    private void procedure_heading() {
        if (!matchToken("procedure")) {
            error("Expected 'procedure', but found'" + currentToken + "'");
        }
        if (name()) {
            procName = currentToken;
            currentToken = getNextToken();
        } else if (!name()) {
            error("Expected name but found'" + currentToken + "'");
        }
        if (!matchToken(";")) {
            error("Expected ';', but found'" + currentToken + "'");
        }
    }

    private void block() {
        // begin
        if (matchToken("begin")) {
            if (stmt_list()) { // statement list
                if (!matchToken("end")) { // end

                    error("Expected 'end', but found'" + currentToken + "'");
                }
            } else {
                error("Expected statement list but found'" + currentToken + "'");
            }
        } else {
            error("Expected 'begin', but found'" + currentToken + "'");
        }
    }

    private boolean stmt_list() {

        if (statement()) {
            do {
                if (currentToken.equals(";")) {
                    matchToken(";");
                    if (!currentToken.equals("end") && !currentToken.equals("until")) {
                        statement();
                        continue;
                    } else {
                        break;
                    }
                } else {
                    return true;
                }
            } while (true);
            return true;
        }
        return true;
    }

    private boolean statement() {

        return ass_stmt() || read_stmt() || write_stmt() || if_stmt()
                || while_stmt() || repeat_stmt() || exit_stmt() || call_stmt();
    }

    // parses an assignment statement
    private boolean ass_stmt() {
        if (name()) { // name
            String temp = peekNextToken();
            if (temp.equals(":=")) {
                currentToken = getNextToken();
                matchToken(":=");
                if (exp()) { // expression
                    return true;
                } else {
                    error("Expected exp but found'" + currentToken + "'");
                    return false;
                }
            }
        }
        return false; // not an assignment statement
    }

    // parses a read statement
    private boolean read_stmt() {

        if (matchToken("readint")) { // read
            if (matchToken("(")) { // int
                if (name_list()) {
                    if (matchToken(")")) {
                        return true;
                    } else {
                        error("Expected ')' but found'" + currentToken + "'");
                    }
                } else {
                    error("Expected name list but found'" + currentToken + "'");
                }
            } else {
                error("Expected '(' but found'" + currentToken + "'");
            }
        } else if (matchToken("readreal")) { // read
            //// ALL THE PREVIOUS SHOULD BE REMOVED!

            // currentToken=getPreviousToken();

            if (matchToken("(")) { // int

                if (name_list()) {
                    // currentToken=getPreviousToken();

                    if (matchToken(")")) {
                        return true;
                    } else {
                        error("Expected ')' but found'" + currentToken + "'");
                    }
                } else {
                    error("Expected name list but found'" + currentToken + "'");
                }
            } else {
                error("Expected '(' but found'" + currentToken + "'");
            }
        } else if (matchToken("readchar")) { // read

            if (matchToken("(")) { // int
                if (name_list()) {
                    if (matchToken(")")) {
                        return true;
                    } else {
                        error("Expected ')' but found'" + currentToken + "'");
                    }
                } else {
                    error("Expected name list but found'" + currentToken + "'");
                }
            } else {
                error("Expected '(' but found'" + currentToken + "'");
            }
        } else if (matchToken("readln")) { // read
            return true;
        }
        return false;
    }

    private boolean write_stmt() {
        if (matchToken("writeint")) { // read
            if (matchToken("(")) { // int
                if (write_list()) {
                    if (matchToken(")")) {
                        return true;
                    } else {
                        error("Expected ')' but found'" + currentToken + "'");
                    }
                } else {
                    error("Expected name list but found'" + currentToken + "'");
                }
            } else {
                error("Expected '(' but found'" + currentToken + "'");
            }
        } else if (matchToken("writereal")) { // read

            if (matchToken("(")) { // int
                if (write_list()) {
                    if (matchToken(")")) {
                        return true;
                    } else {
                        error("Expected ')' but found'" + currentToken + "'");
                    }
                } else {
                    error("Expected name list but found'" + currentToken + "'");
                }
            } else {
                error("Expected '(' but found'" + currentToken + "'");
            }
        } else if (matchToken("writechar")) { // read

            if (matchToken("(")) { // int
                if (write_list()) {
                    if (matchToken(")")) {
                        return true;
                    } else {
                        error("Expected ')' but found'" + currentToken + "'");
                    }
                } else {
                    error("Expected name list but found'" + currentToken + "'");
                }
            } else {
                error("Expected '(' but found'" + currentToken + "'");
            }
        } else if (matchToken("writeln")) { // read
            return true;
        }
        return false;
    }

    private boolean write_list() {
        if (write_item()) {
            currentToken = getNextToken();
            do {
                if (matchToken(",")) {
                    if (write_item()) {
                        currentToken = getNextToken();
                        continue;
                    } else {
                        error("Expected write item but found'" + currentToken + "'");
                        return false;
                    }
                } else {

                    return true;
                }
            } while (true);
        } else {
            return false;
        }
    }

    private boolean write_item() {

        if (name() == true || value() == true) {
            return true;
        }
        return false;
    }

    private boolean call_stmt() {

        if (matchToken("call")) {
            if (currentToken.equals(procName)) {
                currentToken = getNextToken();
                return true;
            } else {
                error("Expected name but found'" + currentToken + "'");
            }
        }
        return false;
    }

    private boolean exit_stmt() {

        if (matchToken("exit")) {
            return true;
        }
        return false;
    }

    private boolean repeat_stmt() {

        if (matchToken("loop")) {

            if (stmt_list()) {
                if (matchToken("until")) {
                    if (condition()) {
                        return true;
                    } else {
                        error("Expected condition but found'" + currentToken + "'");
                        return false;
                    }
                } else {
                    error("Expected 'until' but found'" + currentToken + "'");
                    return false;
                }
            } else {
                error("Expected statement list but found'" + currentToken + "'");
                return false;
            }
        }
        return false;
    }

    private boolean condition() {

        if (name_value()) {
            currentToken = getNextToken();
            if (relational_oper()) {
                if (name_value()) {
                    currentToken = getNextToken();
                    return true;
                } else {
                    error("Expected name value list but found '" + currentToken + "'");
                    return false;
                }
            } else {
                error("Expected relational operation list but found '" + currentToken + "'");
                return false;
            }
        } else {
            error("Expected name value but found '" + currentToken + "'");
            return false;
        }
    }

    private boolean name_value() {
        return name() || value();
    }

    private boolean relational_oper() {
        if (matchToken("=") || matchToken("|=") || matchToken("<") || matchToken("<=") || matchToken(">")
                || matchToken(">=")) {
            return true;
        } else if ((matchToken("|"))) { // all this because in my tokenizer class there was an issue with character '|'
                                        // only.
            if (currentToken.equalsIgnoreCase("=")) {
                matchToken("=");
                return true;
            }
        }
        return false;
    }

    private boolean while_stmt() {
        if (matchToken("while")) {
            if (condition()) {
                if (matchToken("do")) {
                    if (stmt_list()) {
                        if (matchToken("end")) {
                            return true;
                        } else {
                            error("Expected 'end' but found'" + currentToken + "'");
                            return false;
                        }
                    } else {
                        error("Expected statement list but found'" + currentToken + "'");
                        return false;
                    }
                } else {
                    error("Expected 'do' but found'" + currentToken + "'");
                    return false;
                }
            } else {
                error("Expected condition list but found'" + currentToken + "'");
                return false;
            }
        }
        return false;
    }

    private boolean if_stmt() {
        if (matchToken("if")) {
            if (condition()) {
                if (matchToken("then")) {
                    if (stmt_list()) {
                        if (elseif_part()) {
                            else_part();
                            if (matchToken("end")) {
                                return true;
                            } else {
                                error("Expected 'end' list but found '" + currentToken + "'");
                            }
                        } else {
                            error("Expected elseif_part list but found '" + currentToken + "'");
                        }
                    } else {
                        error("Expected statement list list but found '" + currentToken + "'");
                    }
                } else {
                    error("Expected 'then' list but found '" + currentToken + "'");
                }
            } else {
                error("Expected condition but found '" + currentToken + "'");
            }
        }
        return false;
    }

    private void else_part() {
        if (matchToken("else")) {
            if (!stmt_list()) {
                error("Expected statement list but found'" + currentToken + "'");
            }
        }
    }

    private boolean elseif_part() {

        if (currentToken.equals("elseif")) {
            matchToken("elseif");
            if (condition()) {
                if (matchToken("then")) {
                    if (stmt_list()) {
                        elseif_part();
                    } else {
                        error("Expected statement list but found'" + currentToken + "'");
                    }
                } else {
                    error("Expected 'then' but found'" + currentToken + "'");
                }
            } else {
                error("Expected condition but found'" + currentToken + "'");
            }
        }
        return true;
    }

    // parses a value
    private boolean value() {
        return integer_value() || real_value(); // integer or real value
    }

    // parses an expression
    private boolean exp() {

        if (term()) {
            // currentToken = getNextToken();
            do {
                if (add_oper()) {
                    if (term()) {
                        // currentToken = getNextToken();
                        continue;
                    } else {
                        error("Expected term but found'" + currentToken + "'");
                        return false;
                    }
                } else {
                    return true;
                }
            } while (true);

        } else {
            return false;
        }

    }

    // parses a term
    private boolean term() {

        if (factor()) {
            currentToken = getNextToken();
            do {
                if (mul_oper()) {
                    if (factor()) {
                        currentToken = getNextToken();
                        continue;
                    } else {
                        error("Expected factor but found'" + currentToken + "'");
                        return false;
                    }
                } else {
                    return true;
                }
            } while (true);

        } else {
            return false;
        }
    }

    // parses a factor
    private boolean factor() {
        if (matchToken("(")) { // (
            if (exp()) { // expression
                if (currentToken.equals(")")) { // )
                    return true;
                } else {
                    // error("Expected ')'");
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return name() || value(); // name or value
        }
    }

    // parses an addition operator
    private boolean add_oper() {
        return matchToken("+") || matchToken("-");
    }

    // parses a multiplication operator
    private boolean mul_oper() {
        return matchToken("*") || matchToken("/") || matchToken("mod") || matchToken("div");
    }

    // prints an error message and terminates the parsing
    private void error(String message) {
        System.out.println("Error: " + message);
        // System.out.println("Line Number: " + Tokenizer.getLineNumber(currentToken));
        // // Get line number from tokenizer
        System.out.println("Position: " + currentIndex);
        System.out.println("Token: " + currentToken);
        System.out.println("Line: " + Tokenizer.getLineNum(currentIndex - 1));
        System.out.println("the Token after it: " + getNextToken());

        System.exit(1); // terminate the parsing
    }
    // private int getLineNumber() {
    // // If you have a reference to the tokenizer in your parser class
    // // you can call getLineNumber() on it
    // return Tokenizer.getLineNumber();
    // }

    // parses a name
    private boolean name() {
        if (Tokenizer.reservedWords.contains(currentToken)) {
            // reversed words can't be used as regular names
            return false;
        }
        if (Tokenizer.terminals.contains(currentToken)) {
            // terminals cannot be used as names
            return false;
        }
        int i = 0;
        if (Character.isLetter(currentToken.charAt(0))) {
            if (currentToken.length() == 1) {
                return true;
            } else {
                i++;
                while (i < currentToken.length() && ((Character.isLetter(currentToken.charAt(i))
                        || Character.isDigit(currentToken.charAt(i))))) {
                    i++;
                    if (i == currentToken.length()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // parses an integer value
    private boolean integer_value() {
        int i = 0;
        if (Character.isDigit(currentToken.charAt(0))) {
            while (i < currentToken.length() && Character.isDigit(currentToken.charAt(i))) {
                if (i == currentToken.length() - 1) {
                    return true;
                }
                i++;
            }
        }

        return false;
    }

    // parses a real value
    private boolean real_value() {
        int i = 0;
        int fromPoint = -1; // Initialize fromPoint to -1 to track the position of the decimal point

        if (Character.isDigit(currentToken.charAt(i))) {
            i++;

            // Loop to find the decimal point
            while (i < currentToken.length()
                    && (Character.isDigit(currentToken.charAt(i)) || currentToken.charAt(i) == '.')) {
                if (currentToken.charAt(i) == '.') {
                    if (fromPoint == -1) {
                        fromPoint = i;
                    } else {
                        // Second decimal point, not a valid real value
                        return false;
                    }
                }
                i++;
            }

            // Check if there are digits after the decimal point
            if (fromPoint != -1 && fromPoint < currentToken.length() - 1) {
                return true;
            }
        }
        return false;
    }

}