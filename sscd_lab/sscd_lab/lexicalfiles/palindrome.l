%{
#include <stdio.h>
#include <string.h>
#include <ctype.h>

int is_palindrome(const char *str) {
    int len = strlen(str);
    int i = 0, j = len - 1;
    while (i < j) {
        if (tolower(str[i]) != tolower(str[j]))
            return 0;
        i++; j--;
    }
    return 1;
}

int is_pali(int digit) {
        int a=digit;
        int temp=0;
        int dig=0;
        while (a != 0) {
                dig = a % 10;
                temp = temp * 10 + dig;
                a = a / 10;
        }
        return (temp == digit);
}
%}

%option noyywrap

%%

[0-9]+ {
        int val=atoi(yytext);
        if (is_pali(val))
                printf("\"%s\" is a palindrome\n", yytext);
    else
        printf("\"%s\" is not a palindrome\n", yytext);
}

[a-zA-Z]+  {
    if (is_palindrome(yytext))
        printf("\"%s\" is a palindrome\n", yytext);
    else
        printf("\"%s\" is not a palindrome\n", yytext);
}

[ \t\n]+   /* ignore whitespace */

.          /* ignore other characters */
%%

int main() {
    printf("Enter words (Ctrl+D to finish):\n");
    yylex();
}