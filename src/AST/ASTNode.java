package AST;
import AST.Visitor.Visitor;
import Semantics.*;

abstract public class ASTNode {
  // Instance variables
  
  // The line number where the node is in the source file, for use
  // in printing error messages about this AST node
  public int line_number;
  
  // Semantic type represented by the AST node
  public SemanticType type;
  
  // Constructor
  public ASTNode(int ln) {
    line_number = ln;
    type = null;
  }
}
