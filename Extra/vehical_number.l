%{
#include <stdio.h>
%}

%%

[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4} {
    printf("Valid Vehicle Number: %s\n", yytext);
}

.|\n  { printf("Invalid Number Plate: %s\n", yytext); }

%%

int main() {
    printf("Enter vehicle number (e.g., GJ05AB1234):\n");
    yylex();
    return 0;
}

int yywrap() { return 1; }
