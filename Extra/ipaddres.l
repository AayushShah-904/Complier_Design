%{
#include <stdio.h>
%}

%%

([0-9]{1,3}\.){3}[0-9]{1,3} {
    printf("Possible IPv4 Address: %s\n", yytext);
}

.|\n   { printf("Invalid Input: %s\n", yytext); }

%%

int main() {
    printf("Enter an IPv4 Address:\n");
    yylex();
    return 0;
}

int yywrap() { return 1; }
