%{
#include <stdio.h>
#include <string.h>
int keyword_count=0;
int operator_count=0;
int identifier_count=0;
int number_count=0;
int stringliteral_count=0;

int token_count=0;

%}

%%

[a-zA-Z]*  {
    printf("Token: %s,Lenght: %lu\n",yytext,yyleng);
    token_count++; 

}


[0-9]*  {
    printf("Token: %s,Lenght: %lu\n",yytext,yyleng);
    token_count++;   
    number_count++; 
}


[\+\-\*\/\=]*  {
    printf("Token: %s,Lenght: %lu\n",yytext,yyleng);
    token_count++;    
    operator_count++;
}


%%

int main()
{
    printf("Enter input statement\n");
    yylex();
    printf("Total Number of %d\n",token_count);
    return 0;
}

int yywrap()
{
    return 1;
}