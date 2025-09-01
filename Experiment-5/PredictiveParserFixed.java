import java.util.*;

public class PredictiveParserFixed {

    private String[] tokens;    // input tokens
    private int index;          // current input index
    private Stack<String> stack; // parser stack

    // Parsing table: map Non-terminal -> (Terminal -> Production)
    private Map<String, Map<String, String[]>> parsingTable;

    public PredictiveParserFixed(String input) {
        // Split by spaces
        this.tokens = input.split("\\s+");
        this.index = 0;
        this.stack = new Stack<>();
        initParsingTable();
    }

    // Initialize LL(1) parsing table
    private void initParsingTable() {
        parsingTable = new HashMap<>();

        // E -> T E'
        Map<String, String[]> tableE = new HashMap<>();
        tableE.put("int", new String[]{"T", "E'"});
        parsingTable.put("E", tableE);

        // E' -> + T E' | ε
        Map<String, String[]> tableEPrime = new HashMap<>();
        tableEPrime.put("+", new String[]{"+", "T", "E'"}); // + T E'
        tableEPrime.put("$", new String[]{}); // epsilon (end of input)
        parsingTable.put("E'", tableEPrime);

        // T -> int
        Map<String, String[]> tableT = new HashMap<>();
        tableT.put("int", new String[]{"int"});
        parsingTable.put("T", tableT);
    }

    // Main parsing method
    public void parse() {
        stack.push("$"); // End marker
        stack.push("E"); // Start symbol

        while (!stack.isEmpty()) {
            String top = stack.peek();
            String currentToken = (index < tokens.length) ? tokens[index] : "$";

            // End marker check
            if (top.equals("$")) {
                if (currentToken.equals("$")) {
                    System.out.println("Parsing successful!");
                    return;
                } else {
                    System.out.println("Error: unexpected input remaining.");
                    return;
                }
            }

            if (isTerminal(top)) {
                if (top.equals(currentToken)) {
                    System.out.println("Matched terminal: " + top);
                    stack.pop();
                    index++;
                } else {
                    System.out.println("Error: expected " + top + ", got " + currentToken);
                    return;
                }
            } else {
                String[] production = getProduction(top, currentToken);
                if (production != null) {
                    stack.pop();
                    for (int i = production.length - 1; i >= 0; i--) {
                        if (!production[i].equals("")) stack.push(production[i]);
                    }
                    System.out.println(top + " -> " + String.join(" ", production));
                } else {
                    System.out.println("Error: no rule for " + top + " with token " + currentToken);
                    return;
                }
            }
        }
    }

    private boolean isTerminal(String symbol) {
        return !parsingTable.containsKey(symbol);
    }

    private String[] getProduction(String nonTerminal, String token) {
        Map<String, String[]> row = parsingTable.get(nonTerminal);
        if (row.containsKey(token)) return row.get(token);
        if (row.containsKey("$")) return row.get("$"); // epsilon
        return null;
    }

    public static void main(String[] args) {
        PredictiveParserFixed parser = new PredictiveParserFixed("int + int + int");
        parser.parse();
    }
}
