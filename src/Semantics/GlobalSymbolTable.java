package Semantics;

import java.util.*;

import AST.IdentifierType;
import AST.IntArrayType;
import AST.IntegerType;
import Semantics.Visitor;

public class GlobalSymbolTable {
    //instace variables
     public Map<String, SemanticClassType> classMap;
     public Map<String, SemanticBaseType> baseMap;

    // constructor
  public GlobalSymbolTable() {
      // Setup the classes
      classMap = new LinkedHashMap<String, SemanticClassType>();

      // Add the "unknown" type to use for Errors
      SemanticClassType cType = new SemanticClassType("$UNKNOWN", null);
      this.put(cType.typeName, cType);

      // Setup the base types
      baseMap = new HashMap<String, SemanticBaseType>();

      // Add the base types
      SemanticBaseType typeVoid = new SemanticBaseType("void");
      SemanticBaseType typeInt = new SemanticBaseType("int");
      SemanticBaseType typeBoolean = new SemanticBaseType("boolean");
      SemanticBaseType typeIntArray = new SemanticBaseType("intArray");

      baseMap.put(typeVoid.typeName, typeVoid);
      baseMap.put(typeInt.typeName, typeInt);
      baseMap.put(typeBoolean.typeName,typeBoolean);
      baseMap.put(typeIntArray.typeName, typeIntArray);
  }

   public void put(String id, SemanticClassType type){
      classMap.put(id, type);
  }

  public SemanticClassType getClassType(String id){
      return classMap.get(id);
  }
  
  // Returns the semantic type or null if not exist
  public SemanticType get(String id)
  {
	SemanticType target = null;
	
	target = baseMap.get(id);
	if(target == null)
	{
		target = classMap.get(id);
	}
	
	return target;
  }

  public SemanticType getSemanticType(AST.Type t)
  {
  	SemanticType type;
  	
      if (t instanceof IdentifierType)
      {
          IdentifierType it = (IdentifierType)t;
          type = this.getClassType(it.s);
      }
      else
      {
          if(t instanceof IntegerType)
          {
              type = this.baseMap.get("int");
          } 
          else if(t instanceof IntArrayType)
          {
              type = this.baseMap.get("intArray");
          }
          else //(vd.t instanceof BooleanType)
          {
              type = this.baseMap.get("boolean");
          }
      }
      
      return type;
  }

  public void fixupInheritance()
  {
    // Once the entire symbol table is filled, then we need to rationalize 
    // the inheritance of classes.  Classes that derive from other classes 
    // need to have all the methods and variables of their base classes show up
    // in their symbol tables.  
    Iterator it = this.classMap.entrySet().iterator();
    while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry)it.next();
        //paris.getKey();
        ((SemanticClassType)pairs.getValue()).fixupInheritance();
    }
  }
  
  public void accept(Semantics.Visitor v) {
    v.visit(this);
  }
}
