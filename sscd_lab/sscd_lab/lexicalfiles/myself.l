%option noyywrap
%{
#include <stdio.h>
int n, i, j, space;
%}

%%
[0-9]+ {
    n = atoi(yytext); 

    for (i = 1; i <= n; i++) {
        for (space = 1; space <= n - i; space++) {
            printf(" ");
        }
        for (j = 1; j <= 2 * i - 1; j++) {
            printf("*");
        }
        printf("\n");
    }
}
\n      ;   
.       ;   
%%

int main() {
    yylex();
    return 0;
}