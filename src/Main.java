import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;

public class Main {

    public static final String validCharactersRegex = "[a-z]|[A-Z]|[~|&>]";

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int input = 5;
        do {
            try {
                System.out.println("""
                        1 - Convert Infix Expression to Postfix Expression
                        2 - Evaluate a Postfix Expression
                        3 - Create a Truth Table for a Postfix Expression
                        4 - Help
                        5 - Exit""");
                input = scan.nextInt();
                if (input == 1) {
                    System.out.println("Enter an infix expression:");
                    scan.nextLine();
                    System.out.println(infixToPostfix(scan.nextLine()));
                }
                else if (input == 2) {
                    System.out.println("Enter a postfix expression:");
                    scan.nextLine();
                    String postfixExpression = scan.nextLine();
                    HashMap<Character, Boolean> variableValues = new HashMap<>();
                    System.out.println("Enter values for the required variables in the format \"[Variable Character] = [T/F]\", and \"done\" to stop:");
                    String varIn;
                    do {
                        varIn = scan.nextLine();
                        if (!varIn.equals("done")) {
                            Character variableName = varIn.charAt(0);
                            Boolean variableValue = (Character.toLowerCase(varIn.charAt(varIn.length() - 1)) == 't');
                            variableValues.put(variableName, variableValue);
                        }
                    } while (!varIn.equals("done"));
                    System.out.println("The expression evaluated to " + evaluatePostfixLogicalExpression(variableValues, postfixExpression) + ".");
                }
                else if (input == 3) {
                    System.out.println("Enter a postfix expression:");
                    scan.nextLine();
                    System.out.println(postFixToTruthTable(scan.nextLine()));
                }
                else if (input != 5) {
                    printHelp();
                }
            }
            catch (Exception exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        } while(input != 5);
    }

    /*
    Prints out the help document to standard output.
     */
    public static void printHelp() {
        System.out.println("""
                Help:
                To use this software, enter the appropriate command
                and then enter the input, if any is asked for, for\s
                that command. Below is a listing of the commands and
                a description of what they do.

                1 - This command will prompt for an infix expression
                    when selected, and output the postfix version of
                    it. The algorithm reads character by character, so
                    use single letters for variables and the logical
                    operator characters specified at the bottom of this
                    help document for logical operators.
                2 - This command will prompt for a postfix logical
                    expression and then for values for each unique
                    variable in the given postfix expression.
                3 - This command is the same as command 2, but it will,\s
                    instead of prompting for values for variables,\s
                    construct a full truth table with all possibilities
                    for the given expression.
                4 - This, along with any invalid main menu command, will
                    display this help document.
                5 - This command will terminate the program.

                Below are the valid logical operators for use with this\s
                program. Variables can be any lowercase or capital letters.

                '~' - Not Unary Operator
                '*' - And Operator
                '+' - Or Operator
                '>' - Implication Operator

                This program was written by Philip Rodriguez for funsies.\s
                Hope you like it. It comes with absolutely no warranty.
                """);
    }

    /*
    This just denotes our operator precedence.
     */
    public static HashMap<Character, Integer> operatorPrecedence = new HashMap<>();
    static{
        operatorPrecedence.put('~', 1);
        operatorPrecedence.put('|', 2);
        operatorPrecedence.put('&', 3);
        operatorPrecedence.put('>', 4);
    }

    /*
    Takes in a string containing only a valid infix logical expression, and returns a string representing that same
    logical expression in postfix form.
     */
    public static String infixToPostfix(String infixLogicalExpression) throws Exception {
        Stack<Character> operandStack = new Stack<>();
        StringBuilder output = new StringBuilder(infixLogicalExpression.length()+1);
        for(int i = 0; i < infixLogicalExpression.length(); i++) {
            //Make sure the character is valid
            if (!((""+infixLogicalExpression.charAt(i)).matches(validCharactersRegex) || (""+infixLogicalExpression.charAt(i)).matches("[()]")))
            {
                throw new Exception("Invalid character in input string: '" + infixLogicalExpression.charAt(i) + "'");
            }

            if (!Character.isAlphabetic(infixLogicalExpression.charAt(i))) {
                char characterInQuestion = infixLogicalExpression.charAt(i);
                if (characterInQuestion == '(') {
                    //Then push on our new open parenthesis
                    operandStack.push(characterInQuestion);
                }
                else if (characterInQuestion == ')') {
                    //Kick off everything until we hit an open parenthesis and do not add the close parenthesis to the stack
                    while(operandStack.peek() != '(') {
                        output.append(operandStack.pop());
                    }
                    //Pop off the ( itself
                    operandStack.pop();
                } else {
                    //Push off everything of higher or equal precedence until an open parenthesis is hit or the stack is empty
                    while (!operandStack.empty() && operandStack.peek() != '('
                            && operatorPrecedence.get(operandStack.peek()) <= operatorPrecedence.get(characterInQuestion))
                    {
                        output.append(operandStack.pop());
                    }

                    //Then push on our new operator
                    operandStack.push(characterInQuestion);
                }
            } else {
                output.append(infixLogicalExpression.charAt(i));
            }
        }

        //It is possible that our stack isn't empty still, so push all remaining things to output
        while(!operandStack.empty()) {
            output.append(operandStack.pop());
        }

        return output.toString();
    }

    /*
    Takes in a string. Returns an arraylist containing all unique alphabetic characters in the string in sorted order.
     */
    public static ArrayList<Character> getAllUniqueVariables(String postfixLogicalExpression) {
        ArrayList<Character> uniqueVariables = new ArrayList<>();
        for(int i = 0; i < postfixLogicalExpression.length(); i++) {
            if (Character.isAlphabetic(postfixLogicalExpression.charAt(i)) && !uniqueVariables.contains(postfixLogicalExpression.charAt(i))) {
                uniqueVariables.add(postfixLogicalExpression.charAt(i));
            }
        }
        Collections.sort(uniqueVariables);
        return uniqueVariables;
    }

    /*
    Takes in a postfix logical expression and returns a completed truth table for that logical expression.
     */
    public static TruthTable postFixToTruthTable(String postfixLogicalExpression) throws Exception {
        //This is what we will be returning
        TruthTable truthTable = new TruthTable();

        //First detect all variables
        ArrayList<Character> uniqueVariables = getAllUniqueVariables(postfixLogicalExpression);

        //Now that uniqueVariables contains all unique variables in sorted order,
        //it is necessary to create columns in the truth table for each one
        int numRowsPerColumn = (int)Math.pow(2, uniqueVariables.size());
        for(int i = 0; i < uniqueVariables.size(); i++) {
            int numBeforeSwitch = (int)(numRowsPerColumn/(Math.pow(2, i+1)));
            boolean putVal = false;
            for(int j = 0; j < (numRowsPerColumn/numBeforeSwitch); j++) {
                for(int k = 0; k < numBeforeSwitch; k++) {
                    truthTable.addItemToColumn("" + uniqueVariables.get(i), putVal);
                }
                putVal = !putVal;
            }
        }
        //Now add the other columns
        Stack<String> evaluationStack = new Stack<>();
        for(int i = 0; i < postfixLogicalExpression.length(); i++)
        {
            char characterInQuestion = postfixLogicalExpression.charAt(i);
            //Make sure the character is valid
            if (!("" + characterInQuestion).matches(validCharactersRegex)) {
                throw new Exception("Invalid character in input string: '" + characterInQuestion + "'");
            }
            if (Character.isAlphabetic(characterInQuestion)) {
                //Push it onto the stack
                evaluationStack.push(""+characterInQuestion);
            }
            else {
                //Depending on the operator type, pop things off the stack into a mini output and push onto it the resulting expression succounded in parenthesis make a new column in the truth table
                String miniOutput;
                if (characterInQuestion == '~') {
                    //This handles the only unary operator we have
                    miniOutput = "(~" + evaluationStack.pop() + ")";
                }
                else {
                    String firstPop = evaluationStack.pop();
                    String secondPop = evaluationStack.pop();
                    miniOutput = "(" + secondPop + characterInQuestion + firstPop + ")";
                }
                evaluationStack.push(miniOutput);

                //Now, minioutput holds the column heading we need! So,
                //how will we populate it's values?
                int numRows = (int)Math.pow(2, uniqueVariables.size());
                for(int j = 0; j < numRows; j++) {
                    //Get variable values for this row
                    HashMap<Character, Boolean> variableValues = new HashMap<>();
                    for (Character uniqueVariable : uniqueVariables) {
                        variableValues.put(uniqueVariable, truthTable.getColumnValue("" + uniqueVariable, j));
                    }

                    //Add the correct result
                    truthTable.addItemToColumn(miniOutput, evaluatePostfixLogicalExpression(variableValues, infixToPostfix(miniOutput)));
                }
            }
        }
        return truthTable;
    }
    /*
    Takes in a string, an index, and a character. Returns a new string that is the same as the passed in string except
    that the character at index is now newChar.
     */
    public static String replaceChar(String oldString, int index, char newChar) {
        return oldString.substring(0, index) + newChar + oldString.substring(index+1);
    }
    /*
    Take in a string and the index of the character to remove in the given string. Returns a new string that is the same
    as the passed in string but missing that character that was at index in the old string.
     */
    public static String removeChar(String oldString, int index) {
        return oldString.substring(0, index) + oldString.substring(index+1);
    }
    public static boolean evaluatePostfixLogicalExpression(HashMap<Character, Boolean> variableValues, String postfixLogicalExpression) throws Exception {
        //First detect all variables
        ArrayList<Character> uniqueVariables = getAllUniqueVariables(postfixLogicalExpression);

        //Replace all of them with their truth values (lowercase 't' or 'f') values to make solving easier
        for (Character uniqueVariable : uniqueVariables) {
            postfixLogicalExpression = postfixLogicalExpression.replace(uniqueVariable + "", (variableValues.get(uniqueVariable) + "").charAt(0) + "");
        }

        //Read from left to right the postfixLogicalExpression, evaluating as we scan!
        for(int i = 1; i < postfixLogicalExpression.length(); i++) {
            char characterInQuestion = postfixLogicalExpression.charAt(i);
            //Make sure the character is valid
            if (!("" + characterInQuestion).matches(validCharactersRegex)){
                throw new Exception("Invalid character in input string: '" + characterInQuestion + "'");
            }
            //Now just match patterns
            if (characterInQuestion == '|') {
                //Remove char at i, Replace i-1 with result, remove i-2 char, subtract 2 from i
                postfixLogicalExpression = removeChar(postfixLogicalExpression, i);
                if (postfixLogicalExpression.charAt(i-1) == 't' && postfixLogicalExpression.charAt(i-2) == 't') {
                    //Holds
                    postfixLogicalExpression = replaceChar(postfixLogicalExpression, i-1, 't');
                } else {
                    //Does NOT hold
                    postfixLogicalExpression = replaceChar(postfixLogicalExpression, i-1, 'f');
                }
                postfixLogicalExpression = removeChar(postfixLogicalExpression, i-2);
                i -= 2;
            } else if (characterInQuestion == '&') {
                //Remove char at i, Replace i-1 with result, remove i-2 char, subtract 2 from i
                postfixLogicalExpression = removeChar(postfixLogicalExpression, i);
                if (postfixLogicalExpression.charAt(i-1) == 't' || postfixLogicalExpression.charAt(i-2) == 't') {
                    //Holds
                    postfixLogicalExpression = replaceChar(postfixLogicalExpression, i-1, 't');
                }
                else {
                    //Does NOT hold
                    postfixLogicalExpression = replaceChar(postfixLogicalExpression, i-1, 'f');
                }
                postfixLogicalExpression = removeChar(postfixLogicalExpression, i-2);
                i -= 2;
            } else if (characterInQuestion == '>') {
                //Remove char at i, Replace i-1 with result, remove i-2 char, subtract 2 from i
                postfixLogicalExpression = removeChar(postfixLogicalExpression, i);
                if (postfixLogicalExpression.charAt(i-1) == 't') {
                    //Holds vacuously
                    postfixLogicalExpression = replaceChar(postfixLogicalExpression, i-1, 't');
                    postfixLogicalExpression = removeChar(postfixLogicalExpression, i-2);
                }
                else if (postfixLogicalExpression.charAt(i-2) == 'f') {
                    //Holds
                    postfixLogicalExpression = replaceChar(postfixLogicalExpression, i-1, 't');
                    postfixLogicalExpression = removeChar(postfixLogicalExpression, i-2);
                }
                else {
                    //Does NOT hold
                    postfixLogicalExpression = replaceChar(postfixLogicalExpression, i-1, 'f');
                    postfixLogicalExpression = removeChar(postfixLogicalExpression, i-2);
                }
                i -= 2;
            }
            else if (characterInQuestion == '~') {
                //Flip preceeding value, remove the not, subtract from i
                if (postfixLogicalExpression.charAt(i-1) == 't') {
                    postfixLogicalExpression = replaceChar(postfixLogicalExpression, i-1, 'f');
                }
                else {
                    postfixLogicalExpression = replaceChar(postfixLogicalExpression, i-1, 't');
                }
                postfixLogicalExpression = removeChar(postfixLogicalExpression, i);
                i--;
            }
        }

        //Our answer is whatever survived our above operation in postfixLogicalExpression!
        return (postfixLogicalExpression.charAt(0) == 't');
    }
}

class TruthTable {

    private final HashMap<String, ArrayList<Boolean>> data;
    private final ArrayList<String> orderAdded;

    public TruthTable() {
        data = new HashMap<>();
        orderAdded = new ArrayList<>();
    }

    public void addItemToColumn(String columnHeader, Boolean value) {
        if (!data.containsKey(columnHeader)) {
            orderAdded.add(columnHeader);
        }
        if (data.get(columnHeader) == null) {
            data.put(columnHeader, new ArrayList<>());
        }
        data.get(columnHeader).add(value);
    }

    public Boolean getColumnValue(String columnHeader, int row) {
        return data.get(columnHeader).get(row);
    }

    @Override
    public String toString() {
        int numRows = data.get(orderAdded.get(0)).size();
        String[] rows = new String[numRows+1];
        for(int i = 0; i < rows.length; i++)
            rows[i] = "| ";

        for (String key : orderAdded) {
            //Print the whole column for key
            rows[0] += key + " | ";
            for (int i = 1; i < data.get(key).size() + 1; i++) {
                boolean val = data.get(key).get(i - 1);
                StringBuilder add = new StringBuilder(Character.toUpperCase((val + "").charAt(0)) + "");

                while (add.length() < key.length()) {
                    if (add.length() % 2 == 0)
                        add.append(" ");
                    else
                        add.insert(0, " ");
                }

                rows[i] += add + " | ";
            }
        }

        StringBuilder returning = new StringBuilder();

        for (String row : rows)
            returning.append(row).append("\n");

        return returning.toString();
    }
}
