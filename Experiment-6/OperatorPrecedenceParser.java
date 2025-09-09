
import java.util.*;

public class OperatorPrecedenceParser {

    private String input;        // Input string
    private Stack<String> stack; // Parsing stack
    private Map<String, Map<String, String>> precedenceTable; // Precedence relations

    // Constructor initializes input and stack
    public OperatorPrecedenceParser(String input) {
        this.input = input + "$"; // Add end marker
        this.stack = new Stack<>();
        buildPrecedenceTable();
    }

    // Build the operator precedence table
    private void buildPrecedenceTable() {
        precedenceTable = new HashMap<>();

        // List of terminals/operators
        String[] symbols = {"i", "+", "-", "*", "/", "(", ")", "$"};

        // Initialize precedence relations as empty
        for (String sym : symbols) {
            precedenceTable.put(sym, new HashMap<>());
            for (String other : symbols) {
                precedenceTable.get(sym).put(other, " ");
            }
        }

        // Fill the table based on standard arithmetic precedence
        // '>' means higher precedence, '<' means lower precedence, '=' means same precedence
        // Operand
        precedenceTable.get("i").put("+", ">");
        precedenceTable.get("i").put("-", ">");
        precedenceTable.get("i").put("*", ">");
        precedenceTable.get("i").put("/", ">");
        precedenceTable.get("i").put(")", ">");
        precedenceTable.get("i").put("$", ">");

        // '+'
        precedenceTable.get("+").put("i", "<");
        precedenceTable.get("+").put("(", "<");
        precedenceTable.get("+").put("+", ">");
        precedenceTable.get("+").put("-", ">");
        precedenceTable.get("+").put("*", "<");
        precedenceTable.get("+").put("/", "<");
        precedenceTable.get("+").put(")", ">");
        precedenceTable.get("+").put("$", ">");

        // '-'
        precedenceTable.get("-").put("i", "<");
        precedenceTable.get("-").put("(", "<");
        precedenceTable.get("-").put("+", ">");
        precedenceTable.get("-").put("-", ">");
        precedenceTable.get("-").put("*", "<");
        precedenceTable.get("-").put("/", "<");
        precedenceTable.get("-").put(")", ">");
        precedenceTable.get("-").put("$", ">");

        // '*'
        precedenceTable.get("*").put("i", "<");
        precedenceTable.get("*").put("(", "<");
        precedenceTable.get("*").put("+", ">");
        precedenceTable.get("*").put("-", ">");
        precedenceTable.get("*").put("*", ">");
        precedenceTable.get("*").put("/", ">");
        precedenceTable.get("*").put(")", ">");
        precedenceTable.get("*").put("$", ">");

        // '/'
        precedenceTable.get("/").put("i", "<");
        precedenceTable.get("/").put("(", "<");
        precedenceTable.get("/").put("+", ">");
        precedenceTable.get("/").put("-", ">");
        precedenceTable.get("/").put("*", ">");
        precedenceTable.get("/").put("/", ">");
        precedenceTable.get("/").put(")", ">");
        precedenceTable.get("/").put("$", ">");

        // '('
        precedenceTable.get("(").put("i", "<");
        precedenceTable.get("(").put("(", "<");
        precedenceTable.get("(").put("+", "<");
        precedenceTable.get("(").put("-", "<");
        precedenceTable.get("(").put("*", "<");
        precedenceTable.get("(").put("/", "<");
        precedenceTable.get("(").put(")", "=");

        // ')'
        precedenceTable.get(")").put("+", ">");
        precedenceTable.get(")").put("-", ">");
        precedenceTable.get(")").put("*", ">");
        precedenceTable.get(")").put("/", ">");
        precedenceTable.get(")").put(")", ">");
        precedenceTable.get(")").put("$", ">");

        // '$'
        precedenceTable.get("$").put("i", "<");
        precedenceTable.get("$").put("(", "<");
        precedenceTable.get("$").put("+", "<");
        precedenceTable.get("$").put("-", "<");
        precedenceTable.get("$").put("*", "<");
        precedenceTable.get("$").put("/", "<");
        precedenceTable.get("$").put("$", " ");
    }

    // Find the topmost terminal in the stack
    private String getTopTerminal() {
        for (int i = stack.size() - 1; i >= 0; i--) {
            String sym = stack.get(i);
            if (!sym.equals("E")) { // Skip non-terminals like 'E'
                return sym;
            }
        }
        return "$"; // Default to end marker if none found
    }

    // Display precedence table neatly
    public void printPrecedenceTable() {
        List<String> terminals = Arrays.asList("i", "+", "-", "*", "/", "(", ")", "$");
        System.out.printf("%-5s", " ");
        for (String term : terminals) {
            System.out.printf("%-5s", term);
        }
        System.out.println();
        for (String row : terminals) {
            System.out.printf("%-5s", row);
            for (String col : terminals) {
                System.out.printf("%-5s", precedenceTable.get(row).get(col));
            }
            System.out.println();
        }
    }

    // Perform the parsing process
    public void parse() {
        stack.push("$");
        int step = 1;

        System.out.printf("%-5s %-20s %-20s %-20s\n", "Step", "Stack", "Input", "Action");
        System.out.println("--------------------------------------------------------------------");

        int ptr = 0;
        while (true) {
            String currentToken = String.valueOf(input.charAt(ptr));
            String topTerminal = getTopTerminal();
            String precedence = precedenceTable.get(topTerminal).get(currentToken);

            System.out.printf("%-5d %-20s %-20s ", step, stack.toString(), input.substring(ptr));

            if (precedence.equals("<") || precedence.equals("=")) {
                stack.push(currentToken);
                System.out.println("Shift " + currentToken);
                ptr++;
            } else if (precedence.equals(">")) {
                // Reduce by popping until the first terminal with lower precedence
                List<String> handle = new ArrayList<>();
                while (true) {
                    String sym = stack.pop();
                    handle.add(sym);
                    String nextTop = getTopTerminal();
                    String nextPrec = precedenceTable.get(nextTop).get(sym);
                    if (nextPrec == null) {
                        System.out.println("Error: invalid precedence relation for " + nextTop + " and " + sym);
                        return;
                    }
                    if (nextPrec.equals("<")) {
                        break;
                    }

                }
                // Replace handle with 'E'
                stack.push("E");
                System.out.println("Reduce " + handle);
            } else {
                System.out.println("Error: invalid relation or input");
                return;
            }

            if (stack.size() == 2 && stack.peek().equals("E") && currentToken.equals("$")) {
                System.out.printf("%-5d %-20s %-20s %-20s\n", ++step, stack.toString(), input.substring(ptr), "Accept");
                break;
            }
            step++;
        }
    }

    // Main method to run the parser
    public static void main(String[] args) {
        String input = "i+i*i$"; // Example input

        System.out.println("Input Expression: " + input);

        OperatorPrecedenceParser parser = new OperatorPrecedenceParser(input);
        System.out.println("=== Operator Precedence Table ===");
        parser.printPrecedenceTable();
        System.out.println("\n=== Parsing Process ===");
        parser.parse();
    }
}
