%option noyywrap
%{
#include<stdio.h>
    int charCount=0, wordCount=0, lineCount=0;
%}

word    [^ \t\n]+
eol     \n

%%
{word}  {wordCount++; charCount += yyleng; }
{eol}   {charCount++; lineCount++;}
.       charCount++;
%%

int main(){
    yyin=fopen("count.txt","r");
    yylex();
    printf("Line Count: %d, Word Count: %d, Char Count: %d\n", lineCount+1, wordCount, charCount);
    return 0;
}
