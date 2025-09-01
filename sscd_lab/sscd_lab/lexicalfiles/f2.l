%{
int lineno = 1;
int yywrap(void) { return 1; }
%}

line .*\n

%%
{line} { printf("%5d  %s", lineno++, yytext); }
%%

int main(int argc, char *argv[])
{
    extern FILE *yyin;
    yyin = fopen(argv[1], "r");
    if (!yyin) {
        perror("fopen");
        return 1;
    }
    yylex();
    return 0;
}
