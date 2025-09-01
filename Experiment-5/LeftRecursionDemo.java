// public class LeftRecursionEliminator {

//     // Simulates a parser for the grammar: E -> E + T | T
//     // This grammar is left-recursive.
    
//     // The rewritten, non-left-recursive grammar is:
//     // E  -> T E_prime
//     // E_prime -> + T E_prime | epsilon
    
//     private String input;
//     private int index;

//     public LeftRecursionEliminator(String input) {
//         this.input = input;
//         this.index = 0;
//     }

//     public void parse() {
//         System.out.println("Parsing input: " + input);
//         if (E()) {
//             System.out.println("Parsing successful!");
//         } else {
//             System.out.println("Parsing failed.");
//         }
//     }

//     // Corresponds to the non-left-recursive rule E -> T E_prime
//     private boolean E() {
//         if (T()) {
//             return E_prime();
//         }
//         return false;
//     }

//     // Corresponds to the rule E_prime -> + T E_prime | epsilon
//     private boolean E_prime() {
//         if (index < input.length() && input.charAt(index) == '+') {
//             index++;
//             if (T()) {
//                 return E_prime();
//             }
//             return false;
//         }
//         // This is the epsilon case, which means we've successfully completed the recursion.
//         return true;
//     }

//     // Corresponds to the rule T -> number (for this simple example)
//     private boolean T() {
//         if (index < input.length() && Character.isDigit(input.charAt(index))) {
//             System.out.println("Matched T: " + input.charAt(index));
//             index++;
//             return true;
//         }
//         return false;
//     }

//     public static void main(String[] args) {
//         // Example with no recursion
//         LeftRecursionEliminator parser1 = new LeftRecursionEliminator("1+2+3");
//         parser1.parse();
//         System.out.println("--------------------");

//         // Example with a single term
//         LeftRecursionEliminator parser2 = new LeftRecursionEliminator("5");
//         parser2.parse();
//     }
// }



public class LeftRecursionDemo {

    private String input;
    private int index;

    public LeftRecursionDemo(String input) {
        this.input = input;
        this.index = 0;
    }

    public static void main(String[] args) {
        System.out.println("=== Simulating left-recursive parser ===");
        LeftRecursionDemo leftRecursive = new LeftRecursionDemo("1+2+3");
        try {
            leftRecursive.parseLeftRecursive();
        } catch (StackOverflowError e) {
            System.out.println("Parsing failed due to left recursion (infinite loop).");
        }

        System.out.println("\n=== Corrected non-left-recursive parser ===");
        LeftRecursionEliminator corrected = new LeftRecursionEliminator("1+2+3");
        corrected.parse();
    }

    // Simulates left-recursive grammar: E -> E + T | T
    private boolean parseLeftRecursive() {
        System.out.println("Parsing input: " + input);
        return E_left();
    }

    private boolean E_left() {
        // Left-recursive: E -> E + T | T
        // This naive implementation will cause infinite recursion for expressions like "1+2+3"
        if (E_left()) {  // left recursion
            if (index < input.length() && input.charAt(index) == '+') {
                index++;
                if (T()) {
                    return true;
                }
                return false;
            }
            return true;
        }
        return T();
    }

    private boolean T() {
        if (index < input.length() && Character.isDigit(input.charAt(index))) {
            System.out.println("Matched T: " + input.charAt(index));
            index++;
            return true;
        }
        return false;
    }
}

// Corrected grammar parser (non-left-recursive)
class LeftRecursionEliminator {

    private String input;
    private int index;

    public LeftRecursionEliminator(String input) {
        this.input = input;
        this.index = 0;
    }

    public void parse() {
        System.out.println("Parsing input: " + input);
        if (E()) {
            System.out.println("Parsing successful!");
        } else {
            System.out.println("Parsing failed.");
        }
    }

    // Non-left-recursive: E -> T E_prime
    private boolean E() {
        if (T()) {
            return E_prime();
        }
        return false;
    }

    // E_prime -> + T E_prime | epsilon
    private boolean E_prime() {
        if (index < input.length() && input.charAt(index) == '+') {
            index++;
            if (T()) {
                return E_prime();
            }
            return false;
        }
        return true; // epsilon
    }

    // T -> number
    private boolean T() {
        if (index < input.length() && Character.isDigit(input.charAt(index))) {
            System.out.println("Matched T: " + input.charAt(index));
            index++;
            return true;
        }
        return false;
    }
}
