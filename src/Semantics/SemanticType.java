package Semantics;

import Semantics.Visitor;

abstract public class SemanticType {
    //instance variables
    public String typeName;

    // constructor
  public SemanticType(String typeName) {
      this.typeName = typeName;
  }

  public int getSize()
  {
      // by default we'll assume that we are 8 bytes in size
      // we'll overload this for larger types. 
      return 8;
  }

  public void accept(Semantics.Visitor v) {
    v.visit(this);
  }
  
  public static boolean compare(SemanticType i, SemanticType j, GlobalSymbolTable globalSymbolTable)
  {
	  if((i == null) || (j == null))
		  return false;
	  
	  if((i.typeName == "$UNKNOWN") || (j.typeName == "$UNKNOWN"))
		  return true;
	  
	  return i.compare(j, globalSymbolTable);
  }

  public boolean compare(SemanticType i, GlobalSymbolTable globalSymbolTable)
  {
      return typeName == i.typeName;
  }
}
