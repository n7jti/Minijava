package AST.Visitor;
import AST.*;
import Semantics.*;

// Builds a local symbol table for methods.  Should be called to visit MethodDecl nodes
public class LocalSymbolVisitor implements AST.Visitor.Visitor {
    //instance variables
    public LocalSymbolTable localSymbolTable;
    public GlobalSymbolTable globalSymbolTable;
    private boolean throwErrors = true;
    
    public LocalSymbolVisitor(LocalSymbolTable lTable, GlobalSymbolTable gTable){
        localSymbolTable = lTable;
        globalSymbolTable = gTable;
    }
    
    // MainClass m;indentedPrint
    // ClassDeclList cl;
    public void visit(Program n) {
        // Do nothing, should only be called on MethodDecls
    }
  
    // Identifier i1,i2;
    // Statement s;
    public void visit(MainClass n) {
    	// Do nothing, should only be called on MethodDecls
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
    	// Do nothing, should only be called on MethodDecls
	}
 
    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
    	// Do nothing, should only be called on MethodDecls
    }

    // Type t;
    // Identifier i;
    public void visit(VarDecl vd) {
        if (vd.t instanceof IdentifierType)
        {
            IdentifierType it = (IdentifierType)vd.t;
            SemanticClassType cType = globalSymbolTable.getClassType(it.s);
            if (null == cType)
            {
                cType = globalSymbolTable.getClassType("$UNKNOWN");
                printError("Undefined type \"" + it.s + "\"",vd.line_number);
            }
            localSymbolTable.putLocal(vd.i.s, cType);
        }
        else
        {
            SemanticBaseType bt;
            if(vd.t instanceof IntegerType)
            {
                bt = globalSymbolTable.baseMap.get("int");
            } 
            else if(vd.t instanceof IntArrayType)
            {
                bt = globalSymbolTable.baseMap.get("intArray");
            }
            else
            {
                bt = globalSymbolTable.baseMap.get("boolean");
            }
            localSymbolTable.putLocal(vd.i.s, bt);
        } 	
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public void visit(MethodDecl n) {
    	localSymbolTable.name = n.i.s;
    	// Only need to get parameters and variables
        for ( int i = 0; i < n.fl.size(); i++ ) {
            n.fl.get(i).accept(this);
        }
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.sl.size(); i++) {
        	n.sl.get(i).accept(this);
        }
    }

    // Type t;
    // Identifier i;
    public void visit(Formal n) {
        if (n.t instanceof IdentifierType)
        {
            IdentifierType it = (IdentifierType)n.t;
            SemanticClassType cType = globalSymbolTable.getClassType(it.s);
            if (null == cType)
            {
                cType = globalSymbolTable.getClassType("$UNKNOWN");
                printError("Undefined type \"" + it.s + "\"", n.line_number);
            }
            localSymbolTable.putFormal(n.i.s, cType);
        }
        else
        {
            SemanticBaseType bt;
            if(n.t instanceof IntegerType)
            {
                bt = globalSymbolTable.baseMap.get("int");
            } 
            else if(n.t instanceof IntArrayType)
            {
                bt = globalSymbolTable.baseMap.get("intArray");
            }
            else
            {
                bt = globalSymbolTable.baseMap.get("boolean");
            }
            localSymbolTable.putFormal(n.i.s, bt);
        }
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

    }

    // Exp e;
    // Statement s1,s2;
    public void visit(If n) {
    
    }

    // Exp e;
    // Statement s;
    public void visit(While n) {

    }

    // Exp e;
    public void visit(Print n) {

    }
  
    // Identifier i;
    // Exp e;
    public void visit(Assign n) {

    }

    // Identifier i;
    // Exp e1,e2;
    public void visit(ArrayAssign n) {

    }

    // Exp e1,e2;
    public void visit(And n) {

    }

    // Exp e1,e2;
    public void visit(LessThan n) {

    }

    // Exp e1,e2;
    public void visit(Plus n) {

    }

    // Exp e1,e2;
    public void visit(Minus n) {

    }

    // Exp e1,e2;
    public void visit(Times n) {

    }

    // Exp e1,e2;
    public void visit(ArrayLookup n) {

    }

    // Exp e;
    public void visit(ArrayLength n) {

    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public void visit(Call n) {

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

    }

    // Identifier i;
    public void visit(NewObject n) {

    }

    // Exp e;
    public void visit(Not n) {

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
    		System.out.println("Semantic Error line:" + lineNumber + " - " + message);
    	}
    }
}
