%{
#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>

int is_prime(int n) {
    if (n <= 1) return 0;
    for (int i = 2; i*i <= n; i++) {
        if (n % i == 0) return 0;
    }
    return 1;
}
%}

%option noyywrap

%%
[0-9]+  {
            int num = atoi(yytext);
            if (num == 2)
                printf("%d is neither prime nor not prime", num);
            else if (is_prime(num))
                printf("%d is prime\n", num);
            else
                printf("%d is not prime\n", num);
        }

[ \t\n]+  

.    

%%

int main() {
    printf("Enter numbers (Ctrl+D to end):\n");
    yylex();
    return 0;
}