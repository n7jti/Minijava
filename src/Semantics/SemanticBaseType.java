package Semantics;

import Semantics.Visitor;

public class SemanticBaseType extends SemanticType {
    //instace variables

    // constructor
  public SemanticBaseType(String typeName) {
      super(typeName);

  }

  public void accept(Semantics.Visitor v) {
    v.visit(this);
  }
}
