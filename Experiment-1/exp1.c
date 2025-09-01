#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_LINE_LENGTH 1024
#define MAX_TOKENS 100

const char *keywords[] = {
    "int", "float", "double", "char", "void", "if", "else", "while", "for", "do",
    "switch", "case", "break", "continue", "return", "class", "public", "private",
    "new", "delete", "try", "catch"
};
int keywordCount = sizeof(keywords) / sizeof(keywords[0]);

const char *operators[] = {
    "+","-","*","/","%","=","==","!=","<",">","<=",">=","++","--","+=","-=",
    "*=","/=","%=","&&","||","!","&","|"
};
int operatorCount = sizeof(operators) / sizeof(operators[0]);

int isKeyword(const char *token) {
    for (int i = 0; i < keywordCount; i++) {
        if (strcmp(token, keywords[i]) == 0)
            return 1;
    }
    return 0;
}

int isOperator(const char *token) {
    for (int i = 0; i < operatorCount; i++) {
        if (strcmp(token, operators[i]) == 0)
            return 1;
    }
    return 0;
}

int isIdentifier(const char *token) {
    if (!isalpha(token[0]) && token[0] != '_')
        return 0;
    for (int i = 1; token[i]; i++) {
        if (!isalnum(token[i]) && token[i] != '_')
            return 0;
    }
    return 1;
}

int isNumber(const char *token) {
    for (int i = 0; token[i]; i++) {
        if (!isdigit(token[i]))
            return 0;
    }
    return 1;
}

int isStringLiteral(const char *token) {
    int len = strlen(token);
    return len >= 2 && token[0] == '"' && token[len - 1] == '"';
}

void tokenize(const char *line, char tokens[MAX_TOKENS][MAX_LINE_LENGTH], int *count) {
    char token[MAX_LINE_LENGTH] = "";
    int inString = 0, index = 0;
    *count = 0;

    for (int i = 0; line[i]; i++) {
        char c = line[i];

        if (inString) {
            token[index++] = c;
            if (c == '"') {
                token[index] = '\0';
                strcpy(tokens[(*count)++], token);
                index = 0;
                token[0] = '\0';
                inString = 0;
            }
        } else {
            if (c == '"') {
                if (index > 0) {
                    token[index] = '\0';
                    strcpy(tokens[(*count)++], token);
                    index = 0;
                    token[0] = '\0';
                }
                token[index++] = c;
                inString = 1;
            } else if (isalnum(c) || c == '_') {
                token[index++] = c;
            } else if (!isspace(c)) {
                if (index > 0) {
                    token[index] = '\0';
                    strcpy(tokens[(*count)++], token);
                    index = 0;
                    token[0] = '\0';
                }
                char single[2] = {c, '\0'};
                strcpy(tokens[(*count)++], single);
            } else {
                if (index > 0) {
                    token[index] = '\0';
                    strcpy(tokens[(*count)++], token);
                    index = 0;
                    token[0] = '\0';
                }
            }
        }
    }

    if (index > 0) {
        token[index] = '\0';
        strcpy(tokens[(*count)++], token);
    }
}

int main() {
    FILE *file = fopen("exp1_input.txt", "r");
    if (!file) {
        printf("Error: Cannot open file!\n");
        return 1;
    }

    char line[MAX_LINE_LENGTH];
    char tokens[MAX_TOKENS][MAX_LINE_LENGTH];
    int totalTokens = 0, identcnt = 0, keycnt = 0, opcnt = 0, numcnt = 0, strcnt = 0, othercnt = 0;

    while (fgets(line, sizeof(line), file)) {
        int count = 0;
        tokenize(line, tokens, &count);

        for (int i = 0; i < count; i++) {
            char *tok = tokens[i];
            totalTokens++;

            if (isKeyword(tok)) {
                printf("%s -> keyword\n", tok);
                keycnt++;
            } else if (isIdentifier(tok)) {
                printf("%s -> identifier\n", tok);
                identcnt++;
            } else if (isNumber(tok)) {
                printf("%s -> number\n", tok);
                numcnt++;
            } else if (isOperator(tok)) {
                printf("%s -> operator\n", tok);
                opcnt++;
            } else if (isStringLiteral(tok)) {
                printf("%s -> string literal\n", tok);
                strcnt++;
            } else {
                printf("%s -> other\n", tok);
                othercnt++;
            }
        }
    }

    printf("\nIdentifiers Tokens: %d\n", identcnt);
    printf("Keywords Tokens: %d\n", keycnt);
    printf("String Tokens: %d\n", strcnt);
    printf("Operator Tokens: %d\n", opcnt);
    printf("Number Tokens: %d\n", numcnt);
    printf("Other Tokens: %d\n", othercnt);
    printf("Total Tokens: %d\n", totalTokens);

    fclose(file);
    return 0;
}
