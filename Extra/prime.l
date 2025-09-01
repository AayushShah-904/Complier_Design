%{
#include <stdio.h>
#include <stdlib.h>

int isPrime(int n) {
    if (n <= 1) return 0;
    if (n == 2) return 1;
    if (n % 2 == 0) return 0;
    for (int i = 3; i*i <= n; i += 2) {
        if (n % i == 0) return 0;
    }
    return 1;
}
%}

%%

[0-9]+    {
              int num = atoi(yytext);
              if (isPrime(num))
                  printf("%d is a Prime Number\n", num);
              else
                  printf("%d is NOT a Prime Number\n", num);
          }

[ \t\n]+   ;   // ignore whitespace

.          { printf("Invalid token: %s\n", yytext); }

%%

int main() {
    printf("Enter numbers (Ctrl+Z or Ctrl+D to end):\n");
    yylex();
    return 0;
}

int yywrap() {
    return 1;
}
