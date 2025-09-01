//Write a LEX program to eliminate comment lines
    /*
    (single line and multiline) in a high-level program and copy the comments in
    comments.txt file and copy the resulting program into a separate file input.c.
    */

    #include <stdio.h>
    #include <stdlib.h>

    FILE* comment;
    FILE* output;

int main()
{
    printf("Seperating comments and source code ...\n");
    
    /* This is
   a multi-line
   comment */
   // This is a single line comment
   /* This is
   a multi-line
   comment */
    return 0;
}