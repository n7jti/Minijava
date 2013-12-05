package AST;
import AST.Visitor.Visitor;

public class MainClass extends ASTNode{
  public Identifier i1,i2;
  public VarDeclList vl; 
  public StatementList sl;

  public MainClass(Identifier ai1, Identifier ai2, VarDeclList av, StatementList as, int ln) {
    super(ln);
    i1=ai1; i2=ai2; sl=as; vl=av;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}

