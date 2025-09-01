%option noyywrap
%{
#include <stdio.h>
int s = 0;
%}

%%
she { s++; REJECT; } 
he  { s++; }
\n  ;
%%

int main(int argc, char *argv[])
{
    extern FILE *yyin;
    if (argc < 2) {
        printf("Usage: %s <filename>\n", argv[0]);
        return 1;
    }

    yyin = fopen(argv[1], "r");
    if (!yyin) {
        perror("fopen");
        return 1;
    }

    yylex();
    printf("\nNo of occurrence of 'he' (including in 'she') = %d\n", s);
    fclose(yyin);
    return 0;
}
