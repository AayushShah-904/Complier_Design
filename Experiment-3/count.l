%{
    #include <stdio.h>

    int charCount=0;
    int wordCount=0;
    int lineCount=0;
%}


%%

\n  {lineCount++;}
.  {charCount++;}
[^\t\n ]+    { wordCount++; }  


%%

int main()
{
    yylex();
    printf("Lines: %d\nCharacters: %d\nWords: %d\n", lineCount, charCount,wordCount);
    return 0;
}

int yywrap()
{
    return 1;
}