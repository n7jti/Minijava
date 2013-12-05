package AST.Visitor;

import AST.*;

// Sample print visitor from MiniJava web site with small modifications for UW CSE.
// HP 10/11

public class PrettyPrintVisitor implements Visitor {

    // MainClass m;indentedPrint
    // ClassDeclList cl;
    public void visit(Program n) {
        indentedPrint("PROGRAM");
        increaseIndent();
        n.m.accept(this);
        for ( int i = 0; i < n.cl.size(); i++ ) {
            n.cl.get(i).accept(this);
        }
        decreaseIndent();
    }
  
    // Identifier i1,i2;
    // Statement s;
    public void visit(MainClass n) {
        indentedPrint("MAINCLASS");
        increaseIndent();
        n.i1.accept(this);
        n.i2.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.get(i).accept(this);
        }
        decreaseIndent();
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
        indentedPrint("CLASS");
        increaseIndent();
        n.i.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.ml.size(); i++ ) {
            n.ml.get(i).accept(this);
        }
        decreaseIndent();
    }
 
    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
        indentedPrint("CLASSDECLEXTENDS ");
        increaseIndent();
        n.i.accept(this);
        n.j.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.ml.size(); i++ ) {
            n.ml.get(i).accept(this);
        }
        decreaseIndent();
    }

    // Type t;
    // Identifier i;
    public void visit(VarDecl n) {
        indentedPrint("VARDECL");
        increaseIndent();
        n.t.accept(this);
        n.i.accept(this);
        decreaseIndent();
    }

    // Type t;
    // Identifier i;
    // FormalList fl;
    // VarDeclList vl;
    // StatementList sl;
    // Exp e;
    public void visit(MethodDecl n) {
        indentedPrint("METHODDECL");
        increaseIndent();
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
        decreaseIndent();
    }

    // Type t;
    // Identifier i;
    public void visit(Formal n) {
        indentedPrint("FORMAL");
        increaseIndent();
        n.t.accept(this);
        n.i.accept(this);
        decreaseIndent();
    }

    public void visit(IntArrayType n) {
        indentedPrint("INTARRAYTYPE");
    }

    public void visit(BooleanType n) {
        indentedPrint("BOOLEANTYPE");
    }

    public void visit(IntegerType n) {
        indentedPrint("INTEGERTYPE");
    }

    // String s;
    public void visit(IdentifierType n) {
        indentedPrint("IDENTIFIERTYPE");
        increaseIndent();
        indentedPrint(n.s);
        decreaseIndent();
    }

    // StatementList sl;
    public void visit(Block n) {
        indentedPrint("BLOCK");
        increaseIndent();
        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.get(i).accept(this);
        }
        decreaseIndent();
    }

    // Exp e;
    // Statement s1,s2;
    public void visit(If n) {
        indentedPrint("IF");
        increaseIndent();
        n.e.accept(this);
        n.s1.accept(this);
        n.s2.accept(this);
        decreaseIndent();
    }

    // Exp e;
    // Statement s;
    public void visit(While n) {
        indentedPrint("WHILE");
        increaseIndent();
        n.e.accept(this);
        n.s.accept(this);
        decreaseIndent();
    }

    // Exp e;
    public void visit(Print n) {
        indentedPrint("PRINT");
        increaseIndent();
        n.e.accept(this);
        decreaseIndent();
    }
  
    // Identifier i;
    // Exp e;
    public void visit(Assign n) {
        indentedPrint("ASSIGN");
        increaseIndent();
        n.i.accept(this);
        n.e.accept(this);
        decreaseIndent();
    }

    // Identifier i;
    // Exp e1,e2;
    public void visit(ArrayAssign n) {
        indentedPrint("ARRAYASSIGN");
        increaseIndent();
        n.i.accept(this);
        n.e1.accept(this);
        n.e2.accept(this);
        decreaseIndent();
    }

    // Exp e1,e2;
    public void visit(And n) {
        indentedPrint("AND");
        increaseIndent();
        n.e1.accept(this);
        n.e2.accept(this);
        decreaseIndent();
    }

    // Exp e1,e2;
    public void visit(LessThan n) {
        indentedPrint("LESSTHAN");
        increaseIndent();
        n.e1.accept(this);
        n.e2.accept(this);
        decreaseIndent();
    }

    // Exp e1,e2;
    public void visit(Plus n) {
        indentedPrint("PLUS");
        increaseIndent();
        n.e1.accept(this);
        n.e2.accept(this);
        decreaseIndent();
    }

    // Exp e1,e2;
    public void visit(Minus n) {
        indentedPrint("MINUS");
        increaseIndent();
        n.e1.accept(this);
        n.e2.accept(this);
        decreaseIndent();
    }

    // Exp e1,e2;
    public void visit(Times n) {
        indentedPrint("TIMES");
        increaseIndent();
        n.e1.accept(this);
        n.e2.accept(this);
        decreaseIndent();
    }

    // Exp e1,e2;
    public void visit(ArrayLookup n) {
        indentedPrint("ARRAYLOOKUP");
        increaseIndent();
        n.e1.accept(this);
        n.e2.accept(this);
        decreaseIndent();
    }

    // Exp e;
    public void visit(ArrayLength n) {
        indentedPrint("ARRAYLENGTH");
        increaseIndent();
        n.e.accept(this);
        decreaseIndent();
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public void visit(Call n) {
        indentedPrint("CALL");
        increaseIndent();
        n.e.accept(this);
        n.i.accept(this);
        for ( int i = 0; i < n.el.size(); i++ ) {
            n.el.get(i).accept(this);
        }
        decreaseIndent();
    }

    // int i;
    public void visit(IntegerLiteral n) {
        indentedPrint("INTEGERLITERAL");
        increaseIndent();
        indentedPrint(Integer.toString(n.i));
        decreaseIndent();
    }

    public void visit(True n) {
        indentedPrint("TRUE");
    }

    public void visit(False n) {
        indentedPrint("FALSE");
    }

    // String s;
    public void visit(IdentifierExp n) {
        indentedPrint("IDENTIFIEREXP");
        increaseIndent();
        indentedPrint(n.s);
        decreaseIndent();
    }

    public void visit(This n) {
        indentedPrint("THIS");
    }

    // Exp e;
    public void visit(NewArray n) {
        indentedPrint("NEWARRAY");
        increaseIndent();
        n.e.accept(this);
        decreaseIndent();
    }

    // Identifier i;
    public void visit(NewObject n) {
        indentedPrint("NEWOBJECT");
        increaseIndent();
        indentedPrint(n.i.s);
        decreaseIndent();
    }

    // Exp e;
    public void visit(Not n) {
        indentedPrint("NOT");
        increaseIndent();
        n.e.accept(this);
        decreaseIndent();
    }

    // String s;
    public void visit(Identifier n) {
        indentedPrint("IDENTIFIER");
        increaseIndent();
        indentedPrint(n.s);
        decreaseIndent();
    }
  
    private void indentedPrint(String n)
    {    
        for(int i=0; i<indentation; i++)
        {
            n = "  " + n;
        }
      
        System.out.println(n);
    }
  
    private void increaseIndent()
    {
        indentation++;
    }
  
    private void decreaseIndent()
    {
        indentation--;
    }
  
    private int indentation = 0;
}
