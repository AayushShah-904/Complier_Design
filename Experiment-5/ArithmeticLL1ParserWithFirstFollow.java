
import java.util.*;

public class ArithmeticLL1ParserWithFirstFollow {

    private String[] tokens;
    private int index;
    private Stack<String> stack;
    private Map<String, Map<String, String[]>> parsingTable;
    private Map<String, List<String[]>> grammar;

    public ArithmeticLL1ParserWithFirstFollow(String input) {
        this.tokens = input.split("\\s+");
        this.index = 0;
        this.stack = new Stack<>();
        initGrammar();
        computeParsingTable();
    }

    private void initGrammar() {
        grammar = new HashMap<>();

        grammar.put("E", Arrays.asList(new String[][]{{"T", "E'"}}));
        grammar.put("E'", Arrays.asList(
                new String[]{"+", "T", "E'"},
                new String[]{"-", "T", "E'"},
                new String[]{""} // epsilon
        ));
        grammar.put("T", Arrays.asList(new String[][]{{"F", "T'"}}));
        grammar.put("T'", Arrays.asList(
                new String[]{"*", "F", "T'"},
                new String[]{"/", "F", "T'"},
                new String[]{""} // epsilon
        ));
        grammar.put("F", Arrays.asList(
                new String[]{"(", "E", ")"},
                new String[]{"i"}
        ));
    }

    private void computeParsingTable() {
        parsingTable = new HashMap<>();
        for (String nt : grammar.keySet()) {
            parsingTable.put(nt, new HashMap<>());
        }

        // E
        parsingTable.get("E").put("(", new String[]{"T", "E'"});
        parsingTable.get("E").put("i", new String[]{"T", "E'"});

        // E'
        parsingTable.get("E'").put("+", new String[]{"+", "T", "E'"});
        parsingTable.get("E'").put("-", new String[]{"-", "T", "E'"});
        parsingTable.get("E'").put(")", new String[]{});
        parsingTable.get("E'").put("$", new String[]{});

        // T
        parsingTable.get("T").put("(", new String[]{"F", "T'"});
        parsingTable.get("T").put("i", new String[]{"F", "T'"});

        // T'
        parsingTable.get("T'").put("*", new String[]{"*", "F", "T'"});
        parsingTable.get("T'").put("/", new String[]{"/", "F", "T'"});
        parsingTable.get("T'").put("+", new String[]{});
        parsingTable.get("T'").put("-", new String[]{});
        parsingTable.get("T'").put(")", new String[]{});
        parsingTable.get("T'").put("$", new String[]{});

        // F
        parsingTable.get("F").put("(", new String[]{"(", "E", ")"});
        parsingTable.get("F").put("i", new String[]{"i"});
    }

    private boolean isTerminal(String symbol) {
        return !grammar.containsKey(symbol);
    }

    // Compute FIRST sets
    private Map<String, Set<String>> computeFirst() {
        Map<String, Set<String>> first = new HashMap<>();
        for (String nt : grammar.keySet()) {
            first.put(nt, new HashSet<>());
        }

        boolean changed;
        do {
            changed = false;
            for (String nt : grammar.keySet()) {
                for (String[] prod : grammar.get(nt)) {
                    for (String symbol : prod) {
                        if (symbol.equals("")) { // epsilon
                            if (first.get(nt).add("")) {
                                changed = true;
                            }
                            break;
                        } else if (isTerminal(symbol)) {
                            if (first.get(nt).add(symbol)) {
                                changed = true;
                            }
                            break;
                        } else {
                            Set<String> symFirst = first.get(symbol);
                            for (String s : symFirst) {
                                if (!s.equals("") && first.get(nt).add(s)) {
                                    changed = true;
                                }
                            }
                            if (!symFirst.contains("")) {
                                break;
                            }
                        }
                    }
                }
            }
        } while (changed);
        return first;
    }

    // Compute FOLLOW sets
    private Map<String, Set<String>> computeFollow(Map<String, Set<String>> first) {
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
                        String B = prod[i];
                        if (!grammar.containsKey(B)) {
                            continue;
                        }

                        Set<String> trailer = new HashSet<>();
                        boolean epsilonAfter = true;
                        for (int j = i + 1; j < prod.length && epsilonAfter; j++) {
                            String next = prod[j];
                            if (isTerminal(next)) {
                                trailer.add(next);
                                epsilonAfter = false;
                            } else {
                                trailer.addAll(first.get(next));
                                if (!first.get(next).contains("")) {
                                    epsilonAfter = false; 
                                }else {
                                    trailer.remove("");
                                }
                            }
                        }
                        if (epsilonAfter) {
                            trailer.addAll(follow.get(nt));
                        }

                        if (follow.get(B).addAll(trailer)) {
                            changed = true;
                        }
                    }
                }
            }
        } while (changed);
        return follow;
    }

    public void displayFirstFollow() {
        Map<String, Set<String>> first = computeFirst();
        Map<String, Set<String>> follow = computeFollow(first);

        System.out.println("=== FIRST Sets ===");
        for (String nt : first.keySet()) {
            System.out.println(nt + " : " + first.get(nt));
        }

        System.out.println("\n=== FOLLOW Sets ===");
        for (String nt : follow.keySet()) {
            System.out.println(nt + " : " + follow.get(nt));
        }
    }

    // Parsing
    public void parse() {
        stack.push("$");
        stack.push("E");

        while (!stack.isEmpty()) {
            String top = stack.peek();
            String currentToken = (index < tokens.length) ? tokens[index] : "$";

            if (top.equals("$")) {
                if (currentToken.equals("$")) {
                    System.out.println("Parsing successful!");
                    return; // Finished parsing
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
                String[] prod = parsingTable.get(top).get(currentToken);
                if (prod != null) {
                    stack.pop();
                    for (int i = prod.length - 1; i >= 0; i--) {
                        if (!prod[i].equals("")) {
                            stack.push(prod[i]);
                        }
                    }
                    System.out.println(top + " -> " + String.join(" ", prod));
                } else {
                    System.out.println("Error: no rule for " + top + " with token " + currentToken);
                    return;
                }
            }
        }
    }

    public static void main(String[] args) {
        ArithmeticLL1ParserWithFirstFollow parser = new ArithmeticLL1ParserWithFirstFollow("i + i * i");
        parser.displayFirstFollow();
        System.out.println("\n=== Parsing ===");
        parser.parse();
    }
}
