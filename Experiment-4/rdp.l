%{
    #include <stdio.h>
    char l;
    void E();
    void E_prime();
    void match(char t);

    /*
    E → i E'
    E' → + i E' | ε
    */
    

%}

%%
.       {l=yytext[0];}
%%  


void E()
{
    if(l=='i')
    {
        match('i');
        E_prime();
    }
}



void E_prime()
{
    if(l=='+')
    {
        match('+');
        match('i');
        E_prime();
    }
    else
    {
        return;
    }
}

void match(char t)
{
    if(l==t)
    {
        l=getchar();
    }
    else
    {
        printf("Error");
    }
}


int main()
{
    l=getchar();
    E();
    if(l=='$')
    {
        printf("Parsing done");
    }
    else
    {
        printf("Not a Valid string");
    }
    
    return 0;
}

int yywrap()
{
    return 1;
}