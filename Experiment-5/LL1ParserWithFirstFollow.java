
import java.util.*;

public class LL1ParserWithFirstFollow {

    private String input;
    private int index;
    private Stack<String> stack;
    private Map<String, Map<String, String[]>> parsingTable;
    private Map<String, List<String[]>> grammar;

    public LL1ParserWithFirstFollow(String input) {
        this.input = input;
        this.index = 0;
        this.stack = new Stack<>();
        initGrammar();
        computeParsingTable();
    }

    // Initialize grammar
    private void initGrammar() {
        grammar = new HashMap<>();

        grammar.put("E", Arrays.asList(new String[][]{{"T", "E'"}}));
        grammar.put("E'", Arrays.asList(
                new String[]{"+", "T", "E'"},
                new String[]{""} // epsilon
        ));
        grammar.put("T", Arrays.asList(new String[][]{{"i"}}));
    }

    // Build simplified parsing table
    private void computeParsingTable() {
        parsingTable = new HashMap<>();
        for (String nt : grammar.keySet()) {
            parsingTable.put(nt, new HashMap<>());
        }

        parsingTable.get("E").put("i", new String[]{"T", "E'"});
        parsingTable.get("E'").put("+", new String[]{"+", "T", "E'"});
        parsingTable.get("E'").put("$", new String[]{}); // epsilon
        parsingTable.get("T").put("i", new String[]{"i"});
    }

    private boolean isTerminal(String symbol) {
        return !grammar.containsKey(symbol);
    }

    // Print parsing table
    public void printParsingTable() {
        // Collect all terminals appearing in the table
        Set<String> terminals = new TreeSet<>();
        for (Map<String, String[]> row : parsingTable.values()) {
            terminals.addAll(row.keySet());
        }

        // Header row
        System.out.printf("%-8s", "NT/T");  // Column for Non-Terminal/Terminal
        for (String terminal : terminals) {
            System.out.printf("%-15s", terminal);
        }
        System.out.println();

        // Separator
        System.out.printf("%-8s", "--------");
        for (int i = 0; i < terminals.size(); i++) {
            System.out.printf("%-15s", "----------");
        }
        System.out.println();

        // For each non-terminal row
        for (String nonTerminal : parsingTable.keySet()) {
            System.out.printf("%-8s", nonTerminal);
            Map<String, String[]> row = parsingTable.get(nonTerminal);

            for (String terminal : terminals) {
                String[] production = row.get(terminal);
                String cellContent = "";
                if (production != null) {
                    cellContent = (production.length == 0) ? "ε" : String.join(" ", production);
                    cellContent = nonTerminal + " -> " + cellContent;
                }
                System.out.printf("%-15s", cellContent);
            }
            System.out.println();
        }
        System.out.println();
    }

    // Left factoring elimination
    private void eliminateLeftFactoring() {
        Map<String, List<String[]>> newGrammar = new HashMap<>();
        int newNTIndex = 1;

        for (String nt : grammar.keySet()) {
            List<String[]> prods = grammar.get(nt);
            Map<String, List<String[]>> prefixGroups = new HashMap<>();

            // Group productions by their first symbol
            for (String[] prod : prods) {
                String prefix = (prod.length > 0) ? prod[0] : "ε";
                prefixGroups.computeIfAbsent(prefix, k -> new ArrayList<>()).add(prod);
            }

            boolean factored = false;
            for (String prefix : prefixGroups.keySet()) {
                List<String[]> group = prefixGroups.get(prefix);

                if (group.size() > 1 && !prefix.equals("ε")) {
                    factored = true;

                    // Create new non-terminal (e.g. E')
                    String newNT = nt + "_LF" + (newNTIndex++);

                    // Keep the common prefix in old production
                    newGrammar.computeIfAbsent(nt, k -> new ArrayList<>())
                            .add(new String[]{prefix, newNT});

                    // Push the suffixes to new NT
                    for (String[] prod : group) {
                        if (prod.length == 1) {
                            newGrammar.computeIfAbsent(newNT, k -> new ArrayList<>())
                                    .add(new String[]{""}); // epsilon
                        } else {
                            String[] suffix = Arrays.copyOfRange(prod, 1, prod.length);
                            newGrammar.computeIfAbsent(newNT, k -> new ArrayList<>()).add(suffix);
                        }
                    }
                } else {
                    // No left factoring needed, just copy
                    newGrammar.computeIfAbsent(nt, k -> new ArrayList<>()).addAll(group);
                }
            }

            // If no factoring happened, keep the productions
            if (!factored) {
                newGrammar.putIfAbsent(nt, prods);
            }
        }

        grammar = newGrammar;
    }

    // Compute FIRST sets
    private Map<String, Set<String>> computeFirstSets() {
        Map<String, Set<String>> first = new HashMap<>();
        for (String nt : grammar.keySet()) {
            first.put(nt, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (String nt : grammar.keySet()) {
                for (String[] prod : grammar.get(nt)) {
                    if (prod[0].equals("")) {
                        if (first.get(nt).add("")) {
                            changed = true;
                        }
                    } else {
                        String symbol = prod[0];
                        if (grammar.containsKey(symbol)) {
                            // Non-terminal
                            for (String s : first.get(symbol)) {
                                if (!s.equals("") && first.get(nt).add(s)) {
                                    changed = true;
                                }
                            }
                            if (first.get(symbol).contains("") && prod.length > 1) {
                                String next = prod[1];
                                for (String s : first.getOrDefault(next, new HashSet<>())) {
                                    if (!s.equals("") && first.get(nt).add(s)) {
                                        changed = true;
                                    }
                                }
                            } else if (first.get(symbol).contains("") && prod.length == 1) {
                                if (first.get(nt).add("")) {
                                    changed = true;
                                }
                            }
                        } else {
                            // Terminal
                            if (first.get(nt).add(symbol)) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);

        return first;
    }

    // Compute FOLLOW sets
    private Map<String, Set<String>> computeFollowSets(Map<String, Set<String>> first) {
        Map<String, Set<String>> follow = new HashMap<>();
        for (String nt : grammar.keySet()) {
            follow.put(nt, new HashSet<>());
        }
        follow.get("E").add("$"); // start symbol

        boolean changed;
        do {
            changed = false;
            for (String nt : grammar.keySet()) {
                for (String[] prod : grammar.get(nt)) {
                    for (int i = 0; i < prod.length; i++) {
                        String symbol = prod[i];
                        if (grammar.containsKey(symbol)) {
                            Set<String> trailer = new HashSet<>();
                            if (i + 1 < prod.length) {
                                String next = prod[i + 1];
                                if (grammar.containsKey(next)) {
                                    for (String s : first.get(next)) {
                                        if (!s.equals("")) {
                                            trailer.add(s);
                                        }
                                    }
                                } else {
                                    trailer.add(next);
                                }
                            } else {
                                trailer.addAll(follow.get(nt));
                            }
                            if (follow.get(symbol).addAll(trailer)) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);

        return follow;
    }

    //PRint stack
    private void printStack() {
        System.out.print("Stack: ");
        for (String s : stack) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

    // Predictive parsing
    public void parse() {
        stack.push("$");  // End marker
        stack.push("E");  // Start symbol

        while (!stack.isEmpty()) {
            String top = stack.peek();
            String currentToken = (index < input.length()) ? String.valueOf(input.charAt(index)) : "$";

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
                String[] production = parsingTable.get(top).get(currentToken);
                if (production != null) {
                    stack.pop();

                    for (int i = production.length - 1; i >= 0; i--) {
                        if (!production[i].equals("")) {
                            stack.push(production[i]);
                        }
                    }
                    System.out.println(top + " -> " + String.join(" ", production));
                    printStack();
                } else {
                    System.out.println("Error: no rule for " + top + " with token " + currentToken);
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        String input = "i+i+i";  // input without spaces
        LL1ParserWithFirstFollow parser = new LL1ParserWithFirstFollow(input);

        // Compute FIRST and FOLLOW
        Map<String, Set<String>> first = parser.computeFirstSets();
        Map<String, Set<String>> follow = parser.computeFollowSets(first);

        System.out.println("=== GRAMMAR ===");
        System.out.println("E -> T E'");
        System.out.println("E-> + T E' | epsilon");
        System.out.println("T -> i");

        parser.eliminateLeftFactoring();
        
        System.out.println("=== FIRST Sets ===");
        for (String nt : first.keySet()) {
            System.out.println(nt + " : " + first.get(nt));
        }

        System.out.println("\n=== FOLLOW Sets ===");
        for (String nt : follow.keySet()) {
            System.out.println(nt + " : " + follow.get(nt));
        }

        System.out.println("\n=== Parsing Table ===");
        parser.printParsingTable();

        System.out.println("\n=== Parsing ===");
        parser.parse();

    }
}
