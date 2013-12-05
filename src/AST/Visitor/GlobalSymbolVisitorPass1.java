package AST.Visitor;

import AST.*;
import Semantics.*;
import java.util.*;

//In the first pass, we just want to get all of the class names. In Java
//You do not have to forward declare classes

public class GlobalSymbolVisitorPass1 implements Visitor {
	private boolean throwErrors = false;

    private List<String> errors;
	
    //instance variables
    public GlobalSymbolTable globalSymbolTable;

    public GlobalSymbolVisitorPass1(GlobalSymbolTable table, List<String> errors){
        globalSymbolTable = table;
        this.errors = errors; 
    }
    
    // MainClass indentedPrint
    // ClassDeclList cl;
    public void visit(Program n) {
        n.m.accept(this);
        for ( int i = 0; i < n.cl.size(); i++ ) {
            n.cl.get(i).accept(this);
        }
    }
  
    // Identifier i1,i2;
    // Statement s;
    public void visit(MainClass n) {
        SemanticClassType cMain = new SemanticClassType(n.i1.s, null);
        if (null != globalSymbolTable.getClassType("$MAIN"))
        {
            printError("Duplicate Main Detected", n.line_number);
        }
        else
        {
            globalSymbolTable.put("$MAIN", cMain);
	        n.i1.accept(this);
	        n.i2.accept(this);
	        for ( int i = 0; i < n.vl.size(); i++ ) {
	            n.vl.get(i).accept(this);
	        }
	        for ( int i = 0; i < n.sl.size(); i++ ) {
	            n.sl.get(i).accept(this);
	        }
        }
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
        if (null != globalSymbolTable.getClassType(n.i.s))
        {
            printError("Duplicate Class \"" + n.i.s + "\"", n.line_number);
        }
        else
        {
	        SemanticClassType cClass = new SemanticClassType(n.i.s, null);
	        globalSymbolTable.put(n.i.s, cClass);
	
	        n.i.accept(this);
	        for ( int i = 0; i < n.vl.size(); i++ ) {
	            n.vl.get(i).accept(this);
	        }
	        for ( int i = 0; i < n.ml.size(); i++ ) {
	            n.ml.get(i).accept(this);
	        }
        }
    }
 
    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
        if (null != globalSymbolTable.getClassType(n.i.s))
        {
            printError("Duplicate Class \"" + n.i.s + "\"", n.line_number);
        }
        else
        {
	        // get the base class type from the symbol table
	        //SemanticClassType cBase = globalSymbolTable.getClassType(n.j.s);
	        
	        // Create the new type
	        SemanticClassType cClass = new SemanticClassType(n.i.s, null);
	        
	        // Add it to the global symbol table
	        globalSymbolTable.put(n.i.s, cClass);
	
	        n.i.accept(this);
	        n.j.accept(this);
	        for ( int i = 0; i < n.vl.size(); i++ ) {
	            n.vl.get(i).accept(this);
	        }
	        for ( int i = 0; i < n.ml.size(); i++ ) {
	            n.ml.get(i).accept(this);
	        }
        }
    }

    // Type t;
    // Identifier i;
    public void visit(VarDecl n) {
        n.t.accept(this);
        n.i.accept(this);
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public void visit(MethodDecl n) {
        n.t.accept(this);
        n.i.accept(this);
        for ( int i = 0; i < n.fl.size(); i++ ) {
            n.fl.get(i).accept(this);
        }
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.get(i).accept(this);
        }
        n.e.accept(this);
    }

    // Type t;
    // Identifier i;
    public void visit(Formal n) {
        n.t.accept(this);
        n.i.accept(this);
    }

    public void visit(IntArrayType n) {

    }

    public void visit(BooleanType n) {

    }

    public void visit(IntegerType n) {

    }

    // String s;
    public void visit(IdentifierType n) {

    }

    // StatementList sl;
    public void visit(Block n) {
        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.get(i).accept(this);
        }
    }

    // Exp e;
    // Statement s1,s2;
    public void visit(If n) {
        n.e.accept(this);
        n.s1.accept(this);
        n.s2.accept(this);
    }

    // Exp e;
    // Statement s;
    public void visit(While n) {
        n.e.accept(this);
        n.s.accept(this);
    }

    // Exp e;
    public void visit(Print n) {
        n.e.accept(this);
    }
  
    // Identifier i;
    // Exp e;
    public void visit(Assign n) {
        n.i.accept(this);
        n.e.accept(this);
    }

    // Identifier i;
    // Exp e1,e2;
    public void visit(ArrayAssign n) {
        n.i.accept(this);
        n.e1.accept(this);
        n.e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(And n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(LessThan n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(Plus n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(Minus n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(Times n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    // Exp e1,e2;
    public void visit(ArrayLookup n) {
        n.e1.accept(this);
        n.e2.accept(this);
    }

    // Exp e;
    public void visit(ArrayLength n) {
        n.e.accept(this);
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public void visit(Call n) {
        n.e.accept(this);
        n.i.accept(this);
        for ( int i = 0; i < n.el.size(); i++ ) {
            n.el.get(i).accept(this);
        }
    }

    // int i;
    public void visit(IntegerLiteral n) {

    }

    public void visit(True n) {
    }

    public void visit(False n) {
    }

    // String s;
    public void visit(IdentifierExp n) {
    }

    public void visit(This n) {
    }

    // Exp e;
    public void visit(NewArray n) {
        n.e.accept(this);
    }

    // Identifier i;
    public void visit(NewObject n) {
    }

    // Exp e;
    public void visit(Not n) {
        n.e.accept(this);
    }

    // String s;
    public void visit(Identifier n) {
    }
    
    private void printError(String message, int lineNumber)
    {
    	if(throwErrors)
    	{
    		throw new SemanticErrorException("Semantic Error line:" + lineNumber + " - " + message);
    	}
    	else
    	{
    		//System.out.println("Semantic Error line:" + lineNumber + " - " + message);
            errors.add("Semantic Error line:" + lineNumber + " - " + message);
    	}
    }
}
