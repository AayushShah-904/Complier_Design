%{
#include <stdio.h>
%}

%%
[0-9]*      { printf("Number: %s\n", yytext); }
[a-zA-Z]+   { printf("Word: %s\n", yytext); }
[\n\t ]+    { /* ignore whitespace */ }
.           { printf("Symbol: %s\n", yytext); }
%%

int main() {
    yylex();  // call the lexer
    return 0;
}

int yywrap() {
    return 1; // required to avoid linker error
}
