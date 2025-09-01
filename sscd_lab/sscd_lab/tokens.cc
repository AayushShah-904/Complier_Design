#include <iostream>
#include <fstream>
#include <sstream>
#include <vector>
#include <cctype>
#include <unordered_set>
using namespace std;

unordered_set<string> keywords = {
   "int", "float", "double", "char", "void", "if", "else", "while", "for", "do", "switch", "case", "break", "continue", "return", "class", "public", "private", "new", "delete", "try", "catch"
};

unordered_set<string> operatorsSet = {  
    "+","-","*","/","%","=","==","!=","<",">","<=",">=","++","--","+=","-=","*=","/=","%=","&&","||","!","&","|"
};

bool isIdentifier(const string &token) {
    if (token.empty()) return false;
    if (!(isalpha(token[0]) || token[0] == '_')) return false;
    for (int i = 1; i < token.size(); i++) {
        if (!(isalnum(token[i]) || token[i] == '_'))
            return false;
    }
    return true;
}

bool isStringLiteral(const string &token) {
    return token.size() >= 2 && token.front() == '"' && token.back() == '"';
}

bool isOperator(const string &token) {
    return operatorsSet.count(token) > 0;
}

bool isNumber(const string &token) {
    if (token.empty()) return false;
    for (char c : token) {
        if (!isdigit(c)) return false;
    }
    return true;
}

vector<string> tokenize(const string &line) {
    vector<string> tokens;
    string token;
    bool inString = false;

    for (char c : line) {
        if (inString) {
            token += c;
            if (c == '"') {
                tokens.push_back(token);
                token.clear();
                inString = false;
            }
        } else {
            if (c == '"') { 
                if (!token.empty()) {
                    tokens.push_back(token);
                    token.clear();
                }
                token += c;
                inString = true;
            } else if (isalnum(c) || c == '_') {
                token += c;
            } else {
                if (!token.empty()) {
                    tokens.push_back(token);
                    token.clear();
                }
                if (!isspace(c)) {
                    string s(1, c);
                    tokens.push_back(s);
                }
            }
        }
    }

    if (!token.empty()) tokens.push_back(token);
    return tokens;
}

int main() {
    ifstream file("input.txt");
    if (!file) {
        cout << "Error: Cannot open file!" << endl;
        return 1;
    }

    string line;
    int totalTokens = 0; 
    int identcnt = 0; 
    int keycnt = 0; 
    int opcnt=0;
    int numcnt=0;
    int strcnt=0;
    int othercnt=0;

    while (getline(file, line)) {
        vector<string> tokens = tokenize(line);
        for (const string &tok : tokens) {
            totalTokens++;

            if (keywords.count(tok)) {
                cout << tok << " -> keyword" << endl;
                keycnt++;
            } else if (isIdentifier(tok)) {
                cout << tok << " -> identifier" << endl;
                identcnt++;
            } else if (isNumber(tok)) {
                cout << tok << " -> number" << endl;
                numcnt++;
            } else if (isOperator(tok)) {
                cout << tok << " -> operator" << endl;
                opcnt++;
            } else if (isStringLiteral(tok)) {
                cout << tok << " -> string literal" << endl;
                strcnt++;
            } else {
                cout << tok << " -> other" << endl;
                othercnt++;
            }
        }
    }

    cout << endl << "Indetifiers Tokens: " << identcnt << endl;
    cout << "Keywords Tokens: " << keycnt << endl;
    cout << "String Tokens: " << strcnt << endl;
    cout << "Operator Tokens: " << opcnt << endl;
    cout << "Number Tokens: " << numcnt << endl;
    cout << "Other Tokens: " << othercnt << endl;
    cout << "Total Tokens: " << totalTokens << endl;

    file.close();
    return 0;
}
