%option noyywrap
%{
#include <stdio.h>
FILE *resultFile;
%}

%%

\/\/[^\n]*                   { fprintf(yyout, "%s\n", yytext); }  
\/\*([^*]|\*+[^*/])*\*+\/    { fprintf(yyout, "%s\n", yytext); }  
[^\n]+                       { fprintf(resultFile, "%s", yytext); } 
\n                           { fprintf(resultFile, "\n"); }      

%%

int main() {
    yyin = fopen("highlevel.c", "r");
    if (!yyin) {
        perror("Error opening input file highlevel.c");
        return 1;
    }

    yyout = fopen("comments.txt", "w");
    if (!yyout) {
        perror("Error opening output file comments.txt");
        return 1;
    }

    resultFile = fopen("input.c", "w");
    if (!resultFile) {
        perror("Error opening output file input.c");
        return 1;
    }

    yylex();

    fclose(yyin);
    fclose(yyout);
    fclose(resultFile);

    return 0;
}
