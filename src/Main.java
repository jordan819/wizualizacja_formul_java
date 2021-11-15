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
                        1 - Konwersja postaci infiksowej na postfiksową
                        2 - Obliczenie wartości wyrażenia postfiksowego
                        3 - Generowanie macierzy logicznej wyrażenia postfiksowego
                        4 - Pomoc
                        5 - Wyjście""");
                input = scan.nextInt();

                switch (input) {
                    case 1 -> {
                        System.out.println("Wprowadź wyrażenie w postaci infiksowej: ");
                        scan.nextLine();
                        System.out.println(infixToPostfix(scan.nextLine()));
                    }
                    case 2 -> {
                        System.out.println("Wprowadź wyrażenie w postaci postfixowej: ");
                        scan.nextLine();
                        String postfixExpression = scan.nextLine();
                        HashMap<Character, Boolean> variableValues = new HashMap<>();
                        System.out.println("Podaj wartości zmiennych  \"[zmienna] = [T/F]\" i wprowadź \"done\" aby zakończyć:");
                        String varIn;

                        // read logical values of variables from user
                        do {
                            varIn = scan.nextLine();
                            if (!varIn.equals("done")) {
                                Character variableName = varIn.charAt(0);
                                Boolean variableValue = (Character.toLowerCase(varIn.charAt(varIn.length() - 1)) == 't');
                                variableValues.put(variableName, variableValue);
                            }
                        } while (!varIn.equals("done"));

                        System.out.println("wartość wyrażenia to " + evaluatePostfixLogicalExpression(variableValues, postfixExpression) + ".");
                    }
                    case 3 -> {
                        System.out.println("Wprowadź wyrażenie w postaci postfixowej: ");
                        scan.nextLine();
                        System.out.println(postfixToTruthTable(scan.nextLine()));
                    }
                    case 5 -> System.out.println("Thank you for using our program :) Goodbye!");
                    default -> printHelp();
                }
            }
            catch (Exception exception) {
                System.err.println("Error: " + exception.getMessage());
            }
        } while(input != 5);
    }

    /*
    Prints out the help document to standard output.
     */
    public static void printHelp() {
        System.out.println("""
                Dostępne symbole logiczne:
                '~' - Negacja
                '|' - Koniunkcja
                '&' - Alternatywa
                '>' - Implikacja
                """);
    }

    /*
    Denotes operator precedence.
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
    public static String infixToPostfix(String infixExpression) throws Exception {
        Stack<Character> operandStack = new Stack<>();
        StringBuilder output = new StringBuilder(infixExpression.length()+1);
        for(int i = 0; i < infixExpression.length(); i++) {

            //Make sure the character is valid
            if (!((""+infixExpression.charAt(i)).matches(validCharactersRegex) || (""+infixExpression.charAt(i)).matches("[()]")))
                throw new Exception("Niedozwolony znak: '" + infixExpression.charAt(i) + "'");

            if (!Character.isAlphabetic(infixExpression.charAt(i))) {
                char characterInQuestion = infixExpression.charAt(i);
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
                output.append(infixExpression.charAt(i));
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
    Takes in a postfix logical expression and returns a completed truth table for it.
     */
    public static LogicalMatrix postfixToTruthTable(String postfixLogicalExpression) throws Exception {
        //This is what we will be returning
        LogicalMatrix logicalMatrix = new LogicalMatrix();

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
                    logicalMatrix.addItemToColumn("" + uniqueVariables.get(i), putVal);
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
                throw new Exception("Niedowzolony znak: '" + characterInQuestion + "'");
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
                //how will we populate its values?
                int numRows = (int)Math.pow(2, uniqueVariables.size());
                for(int j = 0; j < numRows; j++) {
                    //Get variable values for this row
                    HashMap<Character, Boolean> variableValues = new HashMap<>();
                    for (Character uniqueVariable : uniqueVariables) {
                        variableValues.put(uniqueVariable, logicalMatrix.getColumnValue("" + uniqueVariable, j));
                    }

                    //Add the correct result
                    logicalMatrix.addItemToColumn(miniOutput, evaluatePostfixLogicalExpression(variableValues, infixToPostfix(miniOutput)));
                }
            }
        }
        return logicalMatrix;
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
            postfixLogicalExpression = postfixLogicalExpression.replace(uniqueVariable + "",
                                                                (variableValues.get(uniqueVariable) + "").charAt(0) + "");
        }

        //Read from left to right the postfixLogicalExpression, evaluating as we scan!
        for(int i = 1; i < postfixLogicalExpression.length(); i++) {
            char characterInQuestion = postfixLogicalExpression.charAt(i);
            //Make sure the character is valid
            if (!("" + characterInQuestion).matches(validCharactersRegex)){
                throw new Exception("Niedozwolony znak: '" + characterInQuestion + "'");
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

class LogicalMatrix {

    private final HashMap<String, ArrayList<Boolean>> data;
    private final ArrayList<String> orderAdded;

    public LogicalMatrix() {
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
