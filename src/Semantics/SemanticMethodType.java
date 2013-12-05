package Semantics;

import java.util.LinkedHashMap;
import java.util.Map;

public class SemanticMethodType extends SemanticType {
    //instance variables
	public Map<String, SemanticType> params;
    public SemanticClassType sct;
    public String name;
	
    // constructor
	public SemanticMethodType(String typeName, String name, SemanticClassType sct) {
		super(typeName);
        this.sct = sct;
        this.name = name;
		params = new LinkedHashMap<String, SemanticType>();
	}
  
	public void accept(Semantics.Visitor v) {
		v.visit(this);
	}
}
