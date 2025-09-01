%{
    #include <stdio.h>
    char l;
    void E();
    void E_prime();
    void T();
    void T_prime();
    void F();
    void match(char t);
    /*
    E -> TE'
    E'-> +TE' | -TE' | epsilon
    T -> FT'
    T' -> *FT' | /FT' | epsilon
    F -> (E) | i
    */
    
%}

%%
.       {l=yytext[0];}
%%


void E()
{
    T();
    E_prime();
}

void E_prime()
{
    if(l=='+')
    {
        match('+');
        T();    
        E_prime();
    }
    else if(l=='-')
    {
        match('-');
        T();
        E_prime();
    }
    else
    {
        return;
    }
}

void T()
{
   F();
   T_prime();
}

void T_prime()
{
    if(l=='*')
    {
        match('*');
        F();
        T_prime();
    }
    else if(l=='/')
    {
        match('/');
        F();
        T_prime();
    }
    else
    {
        return ;
    }
}

void F()
{
    if(l=='(')
    {
        match('(');
        E();
        if(l==')')
        {
            match(')');
        }
        else
        {
            printf("Error");
        }
    }
    else if(l=='i')
    {
        match('i');
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