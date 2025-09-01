%{
#include <stdio.h>
%}

%option noyywrap  

%%
abcd    { printf("%s\n", yytext); REJECT; }
abc     { printf("%s\n", yytext); REJECT; }
ab      { printf("%s\n", yytext); REJECT; }
a       { printf("%s\n", yytext); REJECT; }
.|\n    ;   /* Ignore other characters */
%%

int main(int argc, char *argv[])
{
    extern FILE *yyin;
    yyin = fopen(argv[1], "r");
    if (!yyin) {
        perror("File open failed");
        return 1;
    }
    yylex();
    return 0;
}
