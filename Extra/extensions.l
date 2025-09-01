%{
#include <stdio.h>
%}

%%

.*\.txt   { printf("Text File: %s\n", yytext); }
.*\.pdf   { printf("PDF File: %s\n", yytext); }
.*\.docx  { printf("Word Document: %s\n", yytext); }
.*\.jpg   { printf("JPEG Image: %s\n", yytext); }
.*\.png   { printf("PNG Image: %s\n", yytext); }
.         { printf("Unknown File Type: %s\n", yytext); }

%%

int main() {
    printf("Enter file name:\n");
    yylex();
    return 0;
}

int yywrap() 
{ 
    return 1; 
}

