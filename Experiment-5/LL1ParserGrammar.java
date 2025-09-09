
import java.util.*;

public class LL1ParserGrammar {

    // Input string, index pointer, parsing stack
    private String input;
    private int index;
    private Stack<String> stack;

    // Parsing table: maps non-terminal and terminal to production
    private Map<String, Map<String, String[]>> parsingTable;

    // Grammar rules: maps non-terminal to list of its productions
    private Map<String, List<String[]>> grammar;

    // Set of terminals and non-terminals
    private Set<String> terminals;
    private Set<String> nonTerminals;

    // FIRST and FOLLOW sets
    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;

    // Constructor initializes variables
    public LL1ParserGrammar(String input) {
        this.input = input;
        this.index = 0;
        this.stack = new Stack<>();
        this.grammar = new HashMap<>();
        this.terminals = new TreeSet<>();
        this.nonTerminals = new HashSet<>();
        this.parsingTable = new HashMap<>();
    }

    // Initialize grammar rules, terminals, and non-terminals
    public void initGrammar() {
        // Define non-terminals
        nonTerminals.add("E");
        nonTerminals.add("E'");
        nonTerminals.add("T");
        nonTerminals.add("T'");
        nonTerminals.add("F");

        // Define terminals
        terminals.add("+");
        terminals.add("-");
        terminals.add("*");
        terminals.add("/");
        terminals.add("(");
        terminals.add(")");
        terminals.add("i");
        terminals.add("$");

        // Define grammar productions
        grammar.put("E", Arrays.asList(new String[][]{{"T", "E'"}}));
        grammar.put("E'", Arrays.asList(
                new String[]{"+", "T", "E'"},
                new String[]{"-", "T", "E'"},
                new String[]{"ε"}
        ));
        grammar.put("T", Arrays.asList(new String[][]{{"F", "T'"}}));
        grammar.put("T'", Arrays.asList(
                new String[]{"*", "F", "T'"},
                new String[]{"/", "F", "T'"},
                new String[]{"ε"}
        ));
        grammar.put("F", Arrays.asList(
                new String[]{"(", "E", ")"},
                new String[]{"i"}
        ));
    }

    // Compute FIRST and FOLLOW sets
    public void computeFirstFollow() {
        this.first = computeFirstSets();
        this.follow = computeFollowSets();
    }

    // Compute FIRST set of a given production
    private Set<String> computeFirstOfProduction(String[] prod) {
        Set<String> result = new HashSet<>();
        if (prod.length == 0 || (prod.length == 1 && prod[0].equals("ε"))) {
            result.add("");
            return result;
        }
        for (String symbol : prod) {
            if (!nonTerminals.contains(symbol)) {
                result.add(symbol); // Terminal symbol added directly
                break;
            } else {
                Set<String> symbolFirst = first.get(symbol);
                result.addAll(symbolFirst);
                if (!symbolFirst.contains("")) {
                    break;
                } else {
                    result.remove("");
                }
            }
        }
        return result;
    }

    // Iterative algorithm to compute FIRST sets for all non-terminals
    private Map<String, Set<String>> computeFirstSets() {
        Map<String, Set<String>> first = new HashMap<>();
        for (String nt : nonTerminals) {
            first.put(nt, new HashSet<>());
        }
        boolean changed;
        do {
            changed = false;
            for (String nt : nonTerminals) {
                for (String[] prod : grammar.get(nt)) {
                    Set<String> firstSet = first.get(nt);
                    if (prod.length == 1 && prod[0].equals("ε")) {
                        if (firstSet.add("")) {
                            changed = true;
                        }
                    } else {
                        for (int i = 0; i < prod.length; i++) {
                            String symbol = prod[i];
                            if (!nonTerminals.contains(symbol)) {
                                if (firstSet.add(symbol)) {
                                    changed = true;
                                }
                                break;
                            } else {
                                Set<String> symbolFirst = first.get(symbol);
                                int before = firstSet.size();
                                for (String s : symbolFirst) {
                                    if (!s.equals("")) {
                                        firstSet.add(s);
                                    }
                                }
                                if (firstSet.size() > before) {
                                    changed = true;
                                }
                                if (!symbolFirst.contains("")) {
                                    break;
                                }
                                if (i == prod.length - 1) {
                                    if (firstSet.add("")) {
                                        changed = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } while (changed);
        return first;
    }

    // Iterative algorithm to compute FOLLOW sets for all non-terminals
    private Map<String, Set<String>> computeFollowSets() {
        Map<String, Set<String>> follow = new HashMap<>();
        for (String nt : nonTerminals) {
            follow.put(nt, new HashSet<>());
        }
        follow.get("E").add("$"); // Start symbol's FOLLOW set contains $
        boolean changed;
        do {
            changed = false;
            for (String nt : nonTerminals) {
                for (String[] prod : grammar.get(nt)) {
                    for (int i = 0; i < prod.length; i++) {
                        String symbol = prod[i];
                        if (nonTerminals.contains(symbol)) {
                            Set<String> trailer = new HashSet<>();
                            if (i + 1 < prod.length) {
                                String next = prod[i + 1];
                                if (!nonTerminals.contains(next)) {
                                    trailer.add(next);
                                } else {
                                    Set<String> firstNext = first.get(next);
                                    trailer.addAll(firstNext);
                                    trailer.remove("");
                                    if (firstNext.contains("")) {
                                        trailer.addAll(follow.get(nt));
                                    }
                                }
                            } else {
                                trailer.addAll(follow.get(nt));
                            }
                            int before = follow.get(symbol).size();
                            follow.get(symbol).addAll(trailer);
                            if (follow.get(symbol).size() > before) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);
        return follow;
    }

    // Build parsing table using FIRST and FOLLOW sets
    public void buildParsingTable() {
        parsingTable = new HashMap<>();
        for (String nt : nonTerminals) {
            parsingTable.put(nt, new HashMap<>());
        }
        for (String nt : nonTerminals) {
            Map<String, String[]> row = parsingTable.get(nt);
            for (String[] prod : grammar.get(nt)) {
                Set<String> firstSet = computeFirstOfProduction(prod);
                for (String terminal : firstSet) {
                    if (!terminal.equals("")) {
                        row.put(terminal, prod); // Insert rule in table
                    }
                }
                if (firstSet.contains("")) {
                    for (String terminal : follow.get(nt)) {
                        if (!row.containsKey(terminal)) {
                            row.put(terminal, prod); // Insert rule for nullable productions
                        }
                    }
                }
            }
        }
        // Add "sync" entries where no rule is defined but FOLLOW set includes terminal
        for (String nt : nonTerminals) {
            Map<String, String[]> row = parsingTable.get(nt);
            for (String terminal : terminals) {
                if (!row.containsKey(terminal) && follow.get(nt).contains(terminal)) {
                    row.put(terminal, new String[]{"sync"});
                }
            }
        }
    }

    // Display FIRST and FOLLOW sets
    public void printFirstFollow() {
        System.out.println("=== FIRST Sets ===");
        for (String nt : first.keySet()) {
            System.out.println(nt + " : " + first.get(nt));
        }
        System.out.println("\n=== FOLLOW Sets ===");
        for (String nt : follow.keySet()) {
            System.out.println(nt + " : " + follow.get(nt));
        }
    }

    // Display the parsing table neatly
    public void printParsingTable() {
        List<String> terminalList = new ArrayList<>(terminals);
        System.out.printf("%-8s", "NT/T");
        for (String terminal : terminalList) {
            System.out.printf("%-15s", terminal);
        }
        System.out.println();

        System.out.printf("%-8s", "--------");
        for (int i = 0; i < terminalList.size(); i++) {
            System.out.printf("%-15s", "----------");
        }
        System.out.println();

        for (String nt : nonTerminals) {
            System.out.printf("%-8s", nt);
            Map<String, String[]> row = parsingTable.get(nt);
            for (String terminal : terminalList) {
                String[] prod = row.get(terminal);
                String content = "";
                if (prod != null) {
                    if (prod.length == 1 && prod[0].equals("sync")) {
                        content = "sync";
                    } else if (prod.length == 1 && prod[0].equals("ε")) {
                        content = nt + " -> ε";
                    } else {
                        content = nt + " -> " + String.join(" ", prod);
                    }
                }
                System.out.printf("%-15s", content);
            }
            System.out.println();
        }
    }

    // Check if a symbol is terminal
    private boolean isTerminal(String symbol) {
        return !nonTerminals.contains(symbol);
    }

    // Display parsing steps
    private void printParsingStep(int step, String currentToken, String action) {
        System.out.printf("%-5d %-25s %-15s %-20s\n", step, stack.toString(), currentToken, action);
    }

    // LL(1) parsing algorithm with error recovery using sync entries
    public void parse() {
        stack.push("$");
        stack.push("E"); // Start symbol

        int step = 1;

        System.out.printf("%-5s %-25s %-15s %-20s\n", "Step", "Stack", "Input", "Action");
        System.out.println("---------------------------------------------------------------");

        while (!stack.isEmpty()) {
            String top = stack.peek();
            String currentToken = (index < input.length()) ? String.valueOf(input.charAt(index)) : "$";

            if (top.equals("$")) {
                if (currentToken.equals("$")) {
                    printParsingStep(step, currentToken, "Parsing successful!");
                    return;
                } else {
                    printParsingStep(step, currentToken, "Error: unexpected input");
                    return;
                }
            }

            if (isTerminal(top)) {
                if (top.equals(currentToken)) {
                    printParsingStep(step, currentToken, "Matched terminal: " + top);
                    stack.pop();
                    index++;
                } else {
                    printParsingStep(step, currentToken, "Error: expected " + top);
                    return;
                }
            } else {
                String[] production = parsingTable.get(top).get(currentToken);
                if (production != null) {
                    if (production.length == 1 && production[0].equals("sync")) {
                        printParsingStep(step, currentToken, "Error: sync, skipping " + currentToken);
                        index++;  // Skip input symbol on sync error
                    } else {
                        stack.pop();
                        for (int i = production.length - 1; i >= 0; i--) {
                            if (!production[i].equals("ε")) {
                                stack.push(production[i]);
                            }
                        }
                        String rhs = (production.length == 1 && production[0].equals("ε")) ? "ε" : String.join(" ", production);
                        printParsingStep(step, currentToken, top + " -> " + rhs);
                    }
                } else {
                    printParsingStep(step, currentToken, "Error: no rule");
                    return;
                }
            }
            step++;
        }
    }

    // Main function to execute parsing for example inputs
    public static void main(String[] args) {
        String input = "i+i*i$"; // example input

        LL1ParserGrammar parser = new LL1ParserGrammar(input);
        parser.initGrammar();
        parser.computeFirstFollow();
        parser.buildParsingTable();

        parser.printFirstFollow();
        System.out.println();
        parser.printParsingTable();
        System.out.println("\n=== Parsing ===");
        parser.parse();

        System.out.println("\n============================New String for Parsing.============================");

        String input1 = ")i*+i$"; // example input with errors

        LL1ParserGrammar parser1 = new LL1ParserGrammar(input1);
        parser1.initGrammar();
        parser1.computeFirstFollow();
        parser1.buildParsingTable();

        parser1.printFirstFollow();
        System.out.println();
        parser1.printParsingTable();
        System.out.println("\n=== Parsing ===");
        parser1.parse();
    }
}
