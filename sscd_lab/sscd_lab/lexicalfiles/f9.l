%option noyywrap
%{
#include <stdio.h>
%}

%%
SELECT         { printf("SQL Keyword Found: SELECT\n"); }
INSERT         { printf("SQL Keyword Found: INSERT\n"); }
UPDATE         { printf("SQL Keyword Found: UPDATE\n"); }
DELETE         { printf("SQL Keyword Found: DELETE\n"); }
CREATE         { printf("SQL Keyword Found: CREATE\n"); }
DROP           { printf("SQL Keyword Found: DROP\n"); }
FROM           { printf("SQL Keyword Found: FROM\n"); }
WHERE          { printf("SQL Keyword Found: WHERE\n"); }

"<"[^>]+">"    { printf("HTML Tag Found: %s\n", yytext); }

[ \t\n]+;

. { printf("Unrecognized Token: %s\n", yytext); }
%%

int main() {
    yyin = fopen("input.txt", "r");
    if (!yyin) {
        perror("Error opening input file input.txt");
        return 1;
    }

    yylex();
    fclose(yyin);
    return 0;
}
