%{

#include <stdio.h>
#include <stdlib.h>

FILE *comment;
FILE *output;
%}

%%

"//".*                  { fprintf(comment, "%s\n", yytext); }   /* single-line comments */

"/*"([^*]|\*+[^*/])*\*+"/"  { fprintf(comment, "%s\n", yytext); } /* multi-line comments */

.|\n                    { fprintf(output, "%s", yytext); }  /* everything else */

%%

int main() {
    yyin = fopen("comments.c", "r");     // input file
    comment = fopen("comment.txt", "w"); // comments output
    output = fopen("output.c", "w");   // uncommented code

    if (!yyin) {
        perror("Error opening source file");
        return 1;
    }
    if (!comment || !output) {
        perror("Error opening output files");
        return 1;
    }

    yylex();

    fclose(yyin);
    fclose(comment);
    fclose(output);
    return 0;
}

int yywrap() {
    return 1;
}
