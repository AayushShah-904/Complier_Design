public class LeftFactoringEliminator {

    private String[] tokens;
    private int index;

    public LeftFactoringEliminator(String input) {
        // Split input into tokens (for simplicity)
        this.tokens = input.split("\\s+");
        this.index = 0;
    }

    public void parse() {
        System.out.println("Parsing input: " + String.join(" ", tokens));
        if (A()) {
            if (index == tokens.length) {
                System.out.println("Parsing successful!");
            } else {
                System.out.println("Parsing failed: Extra tokens remaining.");
            }
        } else {
            System.out.println("Parsing failed.");
        }
    }

    // A -> if E then S A'
    private boolean A() {
        if (match("if")) {
            if (E()) {
                if (match("then")) {
                    if (S()) {
                        return A_prime();
                    }
                }
            }
        }
        return false;
    }

    // A' -> else S | epsilon
    private boolean A_prime() {
        if (index < tokens.length && tokens[index].equals("else")) {
            index++; // consume 'else'
            return S();
        }
        return true; // epsilon
    }

    // E -> condition (simplified as a single token "cond")
    private boolean E() {
        return match("cond");
    }

    // S -> statement (simplified as a single token "stmt")
    private boolean S() {
        return match("stmt");
    }

    // Helper method to match a token
    private boolean match(String expected) {
        if (index < tokens.length && tokens[index].equals(expected)) {
            System.out.println("Matched: " + tokens[index]);
            index++;
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        // Example 1: if E then S
        LeftFactoringEliminator parser1 = new LeftFactoringEliminator("if cond then stmt");
        parser1.parse();
        System.out.println("--------------------");

        // Example 2: if E then S else S
        LeftFactoringEliminator parser2 = new LeftFactoringEliminator("if cond then stmt else stmt");
        parser2.parse();
    }
}
