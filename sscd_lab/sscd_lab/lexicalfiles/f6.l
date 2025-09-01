%option noyywrap

%{
#include <stdio.h>

int kw = 0, id = 0, num = 0, sl = 0, op = 0, punct = 0;
%}

DIGIT       [0-9]
LETTER      [a-zA-Z]
ID          {LETTER}({LETTER}|{DIGIT})*
NUMBER      {DIGIT}+
STRING      \"([^\"\n]*)\"
WHITESPACE  [ \t\n]+
KEYWORD     (int|float|for|while|if|else|return)

%%

{KEYWORD}           { printf("%s is Keyword\n", yytext); kw++; }
{ID}                { printf("%s is Identifier\n", yytext); id++; }
{NUMBER}            { printf("%s is Number\n", yytext); num++; }
{STRING}            { printf("%s is String Literal\n", yytext); sl++; }
[+\-*/=]            { printf("%s is Operator\n", yytext); op++; }
[;,\(\)\{\}]        { printf("%s is Punctuation\n", yytext); punct++; }
{WHITESPACE}        { /* Ignore whitespace */ }
.                   { printf("%s is Unknown symbol\n", yytext); }

%%

int main() {
    printf("Enter code (Ctrl+D to end on Linux / Ctrl+Z on Windows):\n\n");
    yylex();

    return 0;
}
