/*
 * JFlex specification for the lexical analyzer for a simple demo language.
 * Change this into the scanner for your implementation of MiniJava.
 * CSE 401/P501 Au11
 */


package Scanner;

import java_cup.runtime.*;
import Parser.sym;

%%

%public
%final
%class scanner
%unicode
%cup
%line
%column

/* Code copied into the generated scanner class.  */
/* Can be referenced in scanner action code. */
%{
  // Return new symbol objects with line and column numbers in the symbol 
  // left and right fields. This abuses the original idea of having left 
  // and right be character positions, but is   // is more useful and 
  // follows an example in the JFlex documentation.
  private Symbol symbol(int type) {
    return new Symbol(type, yyline+1, yycolumn+1);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline+1, yycolumn+1, value);
  }

  // Return a readable representation of symbol s (aka token)
  public String symbolToString(Symbol s) {
    String rep;
    switch (s.sym) {
      case sym.AND: return "AND";
      case sym.BECOMES: return "BECOMES";
      case sym.BOOLEAN: return "BOOLEAN";
      case sym.CLASS: return "CLASS";
      case sym.COMMA: return "COMMA";
      case sym.CONSTANT: return "CONSTANT(" + (String)s.value + ")";
      case sym.DOT: return "DOT";
      case sym.ELSE: return "ELSE";
      case sym.EOF: return "<EOF>";
      case sym.error: return "<ERROR>";
      case sym.EXTENDS: return "EXTENDS";
      case sym.FALSE: return "FALSE";
      case sym.IDENTIFIER: return "ID(" + (String)s.value + ")";
      case sym.IF: return "IF";
      case sym.INT: return "INT";
      case sym.LCURLY: return "LCURLY";
      case sym.LESSTHAN: return "LESSTHAN";
      case sym.LPAREN: return "LPAREN";
      case sym.LSQUARE: return "LSQUARE";
      case sym.LENGTH: return "LENGTH";
      case sym.MAIN: return "MAIN";
      case sym.MINUS: return "MINUS";
      case sym.TIMES: return "TIMES";
      case sym.NEW: return "NEW";
      case sym.NOT: return "NOT";
      case sym.PLUS: return "PLUS";
      case sym.PRINT: return "PRINT";
      case sym.PUBLIC: return "PUBLIC";
      case sym.RCURLY: return "RCURLY";
      case sym.RETURN: return "RETURN";
      case sym.RPAREN: return "RPAREN";
      case sym.RSQUARE: return "RSQUARE";
      case sym.SEMICOLON: return "SEMICOLON";
      case sym.STATIC: return "STATIC";
      case sym.STRING: return "STRING";
      case sym.THIS: return "THIS";
      case sym.TRUE: return "TRUE";
      case sym.VOID: return "VOID";
      case sym.WHILE: return "WHILE";
      
      default: return "<UNEXPECTED TOKEN " + s.toString() + ">";
    }
  }
%}

/* Helper definitions */
letter = [a-zA-Z]
digit = [0-9]
eol = [\r\n]
white = {eol}|[ \t]

%%

/* Token definitions */

/* reserved words */
/* (put here so that reserved words take precedence over identifiers) */
"boolean" 	{ return symbol(sym.BOOLEAN); }
"class"		{ return symbol(sym.CLASS); }
"extends" 	{ return symbol(sym.EXTENDS); }
"false"		{ return symbol(sym.FALSE); }
"if" 		{ return symbol(sym.IF); }
"else" 		{ return symbol(sym.ELSE); }
"int" 		{ return symbol(sym.INT); }
"length"    { return symbol(sym.LENGTH); }
"main" 		{ return symbol(sym.MAIN); }
"new"		{ return symbol(sym.NEW); }
"public" 	{ return symbol(sym.PUBLIC); }
"return" 	{ return symbol(sym.RETURN); }
"static" 	{ return symbol(sym.STATIC); }
"String" 	{ return symbol(sym.STRING); }
"this"		{ return symbol(sym.THIS); }
"true"		{ return symbol(sym.TRUE); }
"System.out.println" { return symbol(sym.PRINT); }
"void" 		{ return symbol(sym.VOID); }
"while"		{ return symbol(sym.WHILE); }

/* operators */
"+" { return symbol(sym.PLUS); }
"=" { return symbol(sym.BECOMES); }
"!" { return symbol(sym.NOT); }
"." { return symbol(sym.DOT); }
"&&" {return symbol(sym.AND); }
"-" { return symbol(sym.MINUS); }
"<" { return symbol(sym.LESSTHAN); }
"*" { return symbol(sym.TIMES); }

/* delimiters */
"(" { return symbol(sym.LPAREN); }
")" { return symbol(sym.RPAREN); }
"{" { return symbol(sym.LCURLY); }
"}" { return symbol(sym.RCURLY); }
"[" { return symbol(sym.LSQUARE); }
"]" { return symbol(sym.RSQUARE); }
"," { return symbol(sym.COMMA); }
";" { return symbol(sym.SEMICOLON); }

/* identifiers */
{letter} ({letter}|{digit}|_)* { return symbol(sym.IDENTIFIER, yytext()); }

/* numerical constants */
{digit}* {return symbol(sym.CONSTANT, yytext()); }

/* whitespace */
{white}+ { /* ignore whitespace */ }

/* comments */
"//".*{eol} { /* ignore C++ style comments */ }
"/*"~"*/" { /* ignore C style comments */ }
/* lexical errors (put last so other matches take precedence) */
. { System.err.println(
	"\nunexpected character in input: '" + yytext() + "' at line " +
	(yyline+1) + " column " + (yycolumn+1));
  }
