%{
    #include <stdio.h>


    extern FILE *yyin;

%}


%%

^<[^>]+> {printf("HTML tag found: %s\n",yytext);}

SELECT {printf("SQL Keyword found: SELECT\n");}
INSERT {printf("SQL Keyword found: INSERT\n");}
UPDATE {printf("SQL Keyword found: UPDATE\n");}
DELETE {printf("SQL Keyword found: DELETE\n");}
CREATE {printf("SQL Keyword found: CREATE\n");}
DROP {printf("SQL Keyword found: DROP\n");}
FROM {printf("SQL Keyword found: FROM\n");}
WHERE {printf("SQL Keyword found: WHERE\n");}

[ \t\n]+  ;
.   {printf("Unrecognized Token %s\n",yytext);}

%%

int main()
{
    

    yyin=fopen("input.txt","r");
    if(!yyin)
    {
        perror("Error opening input file input.txt");
        return 1;
    }

    yylex();
    fclose(yyin);
    
    return 0;
}

int yywrap()
{
    return 1;
}