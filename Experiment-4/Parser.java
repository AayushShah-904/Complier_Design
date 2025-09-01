import java.io.IOException;
/*
    E -> TE'
    E'-> +TE' | -TE' | epsilon
    T -> FT'
    T' -> *FT' | /FT' | epsilon
    F -> (E) | i
*/
public class Parser {

    static char l;

    // E -> TE'
    public static void E() throws IOException {
        T();
        E_prime();
    }

    // E'-> +TE' | -TE' | epsilon
    public static void E_prime() throws IOException {
        if (l == '+') {
            match('+');
            T();
            E_prime();
        } else if (l == '-') {
            match('-');
            T();
            E_prime();
        }
    }

    // T -> FT'
    public static void T() throws IOException {
        F();
        T_prime();
    }

    // T' -> *FT' | /FT' | epsilon
    public static void T_prime() throws IOException {
        if (l == '*') {
            match('*');
            F();
            T_prime();
        } else if (l == '/') {
            match('/');
            F();
            T_prime();
        }
    }

    // F -> (E) | i
    public static void F() throws IOException {
        if (l == '(') {
            match('(');
            E();
            if (l == ')') {
                match(')');
            } else {
                System.out.println("Error");
            }
        } else if (l == 'i') {
            match('i');
        } else {
            return;
        }
    }

    public static void match(char t) throws IOException {
        if (l == t) {
            l = (char) System.in.read();
        } else {
            System.out.println("Error");
        }
    }

    public static void main(String[] args) {
        try {
            l = (char) System.in.read();
            E();
            if (l == '$') {
                System.out.println("Parsing done");
            } else {
                System.out.println("Not a Valid string");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading input.");
        }
    }
}