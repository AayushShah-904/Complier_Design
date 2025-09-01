%{
#include <stdio.h>

int vowelCount = 0, consonantCount = 0;
%}

%%

[aeiouAEIOU]        { vowelCount++; }
[a-zA-Z]            { consonantCount++; }
.|\n                ;   // ignore everything else (digits, symbols, spaces)

%%

int main() {
    yylex();
    printf("Vowels: %d\n", vowelCount);
    printf("Consonants: %d\n", consonantCount);
    return 0;
}

int yywrap() { return 1; }
