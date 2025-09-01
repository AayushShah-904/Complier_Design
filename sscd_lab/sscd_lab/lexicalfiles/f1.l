%{
int lineno=1;
%}
%option noyywrap
line .*\n
%%
{line} {printf("%5d  %s", lineno++, yytext);}
%%
int main()
{
    yylex();
    return 0;
}

