package AST.Visitor;

import AST.*;
import Semantics.*;
import java.util.*;

public class GlobalSymbolVisitorPass2 implements Visitor {
	boolean throwErrors = false;
    List<String> errors;
	
    //instance variables
    public GlobalSymbolTable globalSymbolTable;

    public GlobalSymbolVisitorPass2(GlobalSymbolTable table, List<String> errors){
        globalSymbolTable = table;
        this.errors = errors;
    }
    
    // MainClass m;indentedPrint
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
        SemanticClassType cMain = globalSymbolTable.getClassType("$MAIN");
        
        n.i1.accept(this);
        n.i2.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.get(i).accept(this);
        }

    }

    SemanticType getSemanticType(Type t)
    {
        SemanticType sType;
        if (t instanceof IdentifierType)
        {
            IdentifierType it = (IdentifierType)t;
            SemanticClassType cType = globalSymbolTable.getClassType(it.s);
            if (null == cType)
            {
                cType = globalSymbolTable.getClassType("$UNKNOWN");
                printError("Identifier \"" + it.s + "\"", it.line_number);
            }
            sType = cType;
        }
        else
        {
            SemanticBaseType bt;
            if(t instanceof IntegerType)
            {
                bt = globalSymbolTable.baseMap.get("int");
            } 
            else if(t instanceof IntArrayType)
            {
                bt = globalSymbolTable.baseMap.get("intArray");
            }
            else //(vd.t instanceof BooleanType)
            {
                bt = globalSymbolTable.baseMap.get("boolean");
            }
            sType =  bt;
        }
        return sType;
    }

    void addVarDecl(SemanticClassType cClass, VarDecl vd){
        if (null != cClass.getData(vd.i.s))
        {
            printError("Duplicate Identifier \"" + vd.i.s + "\"", vd.line_number);
        }
        else
        {
        	cClass.addData(vd.i.s, getSemanticType(vd.t));
        }
    }

    void addMethodFormal(SemanticMethodType methodType, MethodDecl md)
    {
       //Iterate through the formal list and add it to the MethodClass
        for ( int i = 0; i < md.fl.size(); i++ ) {
            Formal fm = md.fl.get(i);
            methodType.params.put(fm.i.s, getSemanticType(fm.t));
        }
    }

    void addMethodDecl(SemanticClassType cClass, MethodDecl md){
        if (null != cClass.getMethod(md.i.s))
        {
            printError("Duplicate Identifier \"" + md.i.s + "\"", md.line_number);
        }
        
        SemanticMethodType method = new SemanticMethodType( getSemanticType(md.t).typeName, md.i.s, cClass );
        addMethodFormal(method, md);
        cClass.addMethod(md.i.s, method);
    }

    

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
        SemanticClassType cClass = globalSymbolTable.getClassType(n.i.s);
        n.i.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            addVarDecl(cClass,n.vl.get(i));
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.ml.size(); i++ ) {
            addMethodDecl(cClass,n.ml.get(i));
            n.ml.get(i).accept(this);
        }
    }
 
    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
        SemanticClassType cClass = globalSymbolTable.getClassType(n.i.s);
        SemanticClassType cBase = globalSymbolTable.getClassType(n.j.s);
        // check that the type was already defined
        if (cBase == null)
        {
            printError("Unknown class \"" + n.j.s + "\"", n.line_number);
            cBase = globalSymbolTable.getClassType("$UNKNOWN");
        }

        // Connect the base type to the class
        cClass.baseClassType = cBase;

        n.i.accept(this);
        n.j.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            addVarDecl(cClass,n.vl.get(i));
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.ml.size(); i++ ) {
            addMethodDecl(cClass,n.ml.get(i));
            n.ml.get(i).accept(this);
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
