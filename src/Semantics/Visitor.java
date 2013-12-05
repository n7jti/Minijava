package Semantics;

public interface Visitor{
    public void visit(GlobalSymbolTable gst);
    public void visit(SemanticBaseType sbt);
    public void visit(SemanticClassType sct);
    public void visit(SemanticMethodType smt);
    public void visit(SemanticType st);
    public void visit(LocalSymbolTable lst);
}
