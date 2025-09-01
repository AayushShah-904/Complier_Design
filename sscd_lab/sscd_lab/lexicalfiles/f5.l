%{
#include <stdio.h>
#include <string.h>
int token_count = 0;
%}

%%
[a-zA-Z_][a-zA-Z0-9_]* {
    printf("Token: %s, Length: %lu\n", yytext, yyleng);
    token_count++;
}

[0-9]+ {
    printf("Token: %s, Length: %lu\n", yytext, yyleng);
    token_count++;
}

"." {
    printf("Token: %s, Length: %lu\n", yytext, yyleng);
    token_count++;
}

[+\-*/=] {
    printf("Token: %s, Length: %lu\n", yytext, yyleng);
    token_count++;
}

[:;] {
    printf("Token: %s, Length: %lu\n", yytext, yyleng);
    token_count++;
}

[ \t\n]+  ;

. {
    printf("Unknown token: %s\n", yytext);
}
%%

int main() {
    printf("Enter the input statements (Ctrl+Z to end):\n");
    yylex();
    printf("\nTotal number of tokens: %d\n", token_count);
    return 0;
}

int yywrap() {
    return 1;
}
