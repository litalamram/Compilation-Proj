/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/
import java_cup.runtime.*;

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/

/*****************************************************/
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/*******************************************************************************/
/* Note that this has to be the EXACT same name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */
/*****************************************************************************/
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine() { return yyline + 1; }
	public int getCharPos() { return yycolumn; }

	/**********************************************/
	/* Enable token position extraction from main */
	/**********************************************/
	public int getTokenStartPosition() { return yycolumn + 1; }

	/*Checks if the String s represents a 16bit number*/
	public static boolean isShort (String s) {

		int res = 0;
		int i = 0;
		int sign = 1;
		if (s.charAt(0) == '-') {
			i++;
			sign = -1;
		}
		for ( ;i<s.length(); i++) {
			res = res*10 + (s.charAt(i)-'0')*sign;
			if (res > 32768 || res < -32768) {
				return false;
			}
		}

		return true;

	}
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/
LineTerminator		= \r|\n|\r\n
WhiteSpace		= {LineTerminator} | [ \t\f]

INTEGER			= 0 | [1-9][0-9]*
ErrINT			=  0+[0-9]+

ID			= [a-zA-Z][a-zA-Z0-9]*

Letter			= [a-zA-Z]
Digit			= [0-9]

/********COMMENTS**********/

//Single line Comments
InSingleComment			= [a-zA-Z0-9\ \t\(\)\{\}\[\]\!\?\+\-\*\/\.\;]
SingleComment			= "//"{InSingleComment}*{LineTerminator}?
GeneralComment			= "//".*{LineTerminator}?

//Multiline Comments
InMultiComment			= [a-zA-Z0-9\ \t\(\)\{\}\[\]\!\?\+\-\.\;\r\n]
MultiCommentBody		= "/" | {InMultiComment}|("*"+({InMultiComment}))
MultiComment			= "/*" ({MultiCommentBody})* "*"+"/"
UnclosedComment			= "/*" ([^\*] | (\*+[^*/]))*

Comment				= {SingleComment} | {MultiComment}


/*********STRING**********/

String		= \"{Letter}*\"
unClosedString	= \"{Letter}*

/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {

/**********Keywords*************/
"class"					{ return symbol(TokenNames.CLASS);}
"nil"					{ return symbol(TokenNames.NIL);}
"array"					{ return symbol(TokenNames.ARRAY);}
"while"					{ return symbol(TokenNames.WHILE);}
"extends"				{ return symbol(TokenNames.EXTENDS);}
"return"				{ return symbol(TokenNames.RETURN);}
"new"					{ return symbol(TokenNames.NEW);}
"if"					{ return symbol(TokenNames.IF);}


"+"					{ return symbol(TokenNames.PLUS);}
"-"					{ return symbol(TokenNames.MINUS);}
"*"					{ return symbol(TokenNames.TIMES);}
"/"					{ return symbol(TokenNames.DIVIDE);}
"("					{ return symbol(TokenNames.LPAREN);}
")"					{ return symbol(TokenNames.RPAREN);}
"["					{ return symbol(TokenNames.LBRACK);}
"]"					{ return symbol(TokenNames.RBRACK);}
"{"					{ return symbol(TokenNames.LBRACE);}
"}"					{ return symbol(TokenNames.RBRACE);}
","					{ return symbol(TokenNames.COMMA);}
"."					{ return symbol(TokenNames.DOT);}
";"					{ return symbol(TokenNames.SEMICOLON);}
":="					{ return symbol(TokenNames.ASSIGN);}
"="					{ return symbol(TokenNames.EQ);}
"<"					{ return symbol(TokenNames.LT);}
">"					{ return symbol(TokenNames.GT);}


{INTEGER}				{ if (isShort(yytext()))
						return symbol(TokenNames.INT, new Integer(yytext()));
					  else
					  	return symbol(TokenNames.error);
					}
{ErrINT}				{ return symbol(TokenNames.error);}
{ID}					{ return symbol(TokenNames.ID,     new String( yytext()));}
{String}				{ return symbol(TokenNames.STRING,     new String( yytext()));}
{unClosedString}			{ return symbol(TokenNames.error);}
{WhiteSpace}				{ /* just skip what was found, do nothing */ }
{Comment}				{ /* just skip what was found, do nothing */ }
{UnclosedComment}			{ return symbol(TokenNames.error); }
{GeneralComment}			{ return symbol(TokenNames.error); }
<<EOF>>					{ return symbol(TokenNames.EOF);}
.					{ return symbol(TokenNames.error);}
}

