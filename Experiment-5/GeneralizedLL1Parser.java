import java.util.*;

public class GeneralizedLL1Parser {

    private String input;
    private int index;
    private Stack<String> stack;
    private Map<String, List<String[]>> grammar;
    private Map<String, Map<String, String[]>> parsingTable;

    public GeneralizedLL1Parser(String input, Map<String, List<String[]>> grammar) {
        this.input = input;
        this.index = 0;
        this.stack = new Stack<>();
        this.grammar = grammar;
        eliminateLeftFactoring();
        Map<String, Set<String>> first = computeFirstSets();
        Map<String, Set<String>> follow = computeFollowSets(first);
        buildParsingTable(first, follow);
    }

    // ========= Left Factoring =========
    private void eliminateLeftFactoring() {
        Map<String, List<String[]>> newGrammar = new HashMap<>();
        int newNTIndex = 1;

        for (String nt : grammar.keySet()) {
            List<String[]> prods = grammar.get(nt);
            Map<String, List<String[]>> prefixGroups = new HashMap<>();

            for (String[] prod : prods) {
                String prefix = (prod.length > 0) ? prod[0] : "ε";
                prefixGroups.computeIfAbsent(prefix, k -> new ArrayList<>()).add(prod);
            }

            boolean factored = false;
            for (String prefix : prefixGroups.keySet()) {
                List<String[]> group = prefixGroups.get(prefix);

                if (group.size() > 1 && !prefix.equals("ε")) {
                    factored = true;
                    String newNT = nt + "_LF" + (newNTIndex++);
                    newGrammar.computeIfAbsent(nt, k -> new ArrayList<>())
                              .add(new String[]{prefix, newNT});

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
                    newGrammar.computeIfAbsent(nt, k -> new ArrayList<>()).addAll(group);
                }
            }

            if (!factored) {
                newGrammar.putIfAbsent(nt, prods);
            }
        }
        grammar = newGrammar;
    }

    // ========= FIRST Sets =========
    private Map<String, Set<String>> computeFirstSets() {
        Map<String, Set<String>> first = new HashMap<>();
        for (String nt : grammar.keySet()) first.put(nt, new HashSet<>());

        boolean changed;
        do {
            changed = false;
            for (String nt : grammar.keySet()) {
                for (String[] prod : grammar.get(nt)) {
                    if (prod[0].equals("")) {
                        if (first.get(nt).add("")) changed = true;
                    } else {
                        String symbol = prod[0];
                        if (grammar.containsKey(symbol)) {
                            for (String s : first.get(symbol)) {
                                if (!s.equals("") && first.get(nt).add(s)) changed = true;
                            }
                            if (first.get(symbol).contains("") && prod.length > 1) {
                                String next = prod[1];
                                for (String s : first.getOrDefault(next, new HashSet<>())) {
                                    if (!s.equals("") && first.get(nt).add(s)) changed = true;
                                }
                            } else if (first.get(symbol).contains("") && prod.length == 1) {
                                if (first.get(nt).add("")) changed = true;
                            }
                        } else {
                            if (first.get(nt).add(symbol)) changed = true;
                        }
                    }
                }
            }
        } while (changed);

        return first;
    }

    // ========= FOLLOW Sets =========
    private Map<String, Set<String>> computeFollowSets(Map<String, Set<String>> first) {
        Map<String, Set<String>> follow = new HashMap<>();
        for (String nt : grammar.keySet()) follow.put(nt, new HashSet<>());
        String start = grammar.keySet().iterator().next(); // first NT = start
        follow.get(start).add("$");

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
                                        if (!s.equals("")) trailer.add(s);
                                    }
                                    if (first.get(next).contains("")) trailer.addAll(follow.get(nt));
                                } else {
                                    trailer.add(next);
                                }
                            } else {
                                trailer.addAll(follow.get(nt));
                            }
                            if (follow.get(symbol).addAll(trailer)) changed = true;
                        }
                    }
                }
            }
        } while (changed);

        return follow;
    }

    // ========= Parsing Table =========
    private void buildParsingTable(Map<String, Set<String>> first, Map<String, Set<String>> follow) {
        parsingTable = new HashMap<>();
        for (String nt : grammar.keySet()) parsingTable.put(nt, new HashMap<>());

        for (String nt : grammar.keySet()) {
            for (String[] prod : grammar.get(nt)) {
                Set<String> firstSet = new HashSet<>();
                if (prod[0].equals("")) {
                    firstSet.addAll(follow.get(nt)); // epsilon → FOLLOW
                } else if (grammar.containsKey(prod[0])) {
                    firstSet.addAll(first.get(prod[0]));
                    if (first.get(prod[0]).contains("")) {
                        firstSet.remove("");
                        firstSet.addAll(follow.get(nt));
                    }
                } else {
                    firstSet.add(prod[0]); // terminal
                }

                for (String terminal : firstSet) {
                    parsingTable.get(nt).put(terminal, prod);
                }
            }
        }
    }

    private boolean isTerminal(String symbol) {
        return !grammar.containsKey(symbol);
    }

    // ========= Parsing =========
    public void parse() {
        stack.push("$");
        String start = grammar.keySet().iterator().next(); // first NT
        stack.push(start);

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

    // ========= Main =========
    public static void main(String[] args) {
        // Example grammar (Expression Grammar)
        /*
         *  E -> TE'
            E'-> +TE' | -TE' | epsilon
            T -> FT'
            T' -> *FT' | /FT' | epsilon
            F -> (E) | i
         */
        Map<String, List<String[]>> grammar = new LinkedHashMap<>();
        grammar.put("E", Arrays.asList(new String[][]{{"T", "E'"}}));
        grammar.put("E'", Arrays.asList(new String[][]{{"+", "T", "E'"}, {"-", "T", "E'"}, {""}}));
        grammar.put("T", Arrays.asList(new String[][]{{"F", "T'"}}));
        grammar.put("T'", Arrays.asList(new String[][]{{"*", "F", "T'"}, {"/", "F", "T'"}, {""}}));
        grammar.put("F", Arrays.asList(new String[][]{{"(", "E", ")"}, {"i"}}));
    

        String input = "i+i+i";  // input string
        GeneralizedLL1Parser parser = new GeneralizedLL1Parser(input, grammar);

        System.out.println("=== Parsing ===");
        parser.parse();
    }
}
