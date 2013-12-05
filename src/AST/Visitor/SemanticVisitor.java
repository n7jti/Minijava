package AST.Visitor;
import AST.*;
import Semantics.*;

import java.util.*;

public class SemanticVisitor implements Visitor{
	public boolean throwErrors = false;
    List<String> errors;

	//instance variables
    public GlobalSymbolTable globalSymbolTable;

    public SemanticVisitor(GlobalSymbolTable table, List<String> errors){
        globalSymbolTable = table;
        this.errors = errors;
    }
    
    private String currentClass = null;
    private String currentMethod = null;
    private String baseClass = null;
    
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
    	currentClass = "$MAIN";
                	
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.get(i).accept(this);
        }
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
    	String lastClass = currentClass;
    	currentClass = n.i.s;
    	
        n.i.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.ml.size(); i++ ) {
            n.ml.get(i).accept(this);
        }
        
        currentClass = lastClass;
    }
 
    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
    	String lastClass = currentClass;
    	currentClass = n.i.s;
    	baseClass = n.j.s;
    	if(globalSymbolTable.getClassType(baseClass) == null)
    	{
    		printError("Class \"" + currentClass + "\" extends an undefined base class \"" + baseClass + "\"", n.line_number);
    	}
    	else
    	{
	        n.i.accept(this);
	        n.j.accept(this);
	        for ( int i = 0; i < n.vl.size(); i++ ) {
	            n.vl.get(i).accept(this);
	        }
	        for ( int i = 0; i < n.ml.size(); i++ ) {
	            n.ml.get(i).accept(this);
	        }
    	}
        baseClass = null;
        currentClass = lastClass;
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
    public void visit(MethodDecl n){
    	String lastMethod = currentMethod;
    	currentMethod = n.i.s;
    	
    	n.t.accept(this);
    	n.type = n.t.type;
    	
    	// Generate local symbol table
    	LocalSymbolTable localSymbolTable = new LocalSymbolTable();
    	LocalSymbolVisitor localSymbolVisitor = new LocalSymbolVisitor(localSymbolTable, globalSymbolTable);
    	n.accept(localSymbolVisitor);
    	SemanticClassType thisClass = globalSymbolTable.getClassType(currentClass);
    	thisClass.addMethodSymbolTable(n.i.s, localSymbolTable);

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
    	
    	// Check that the return matches the return type
    	verifySemanticTypes(n.type, n.e.type, n.e.line_number);
    	
    	// If this is an overridden method, verify new method has same return and param list
    	if(baseClass != null)
    	{
    		SemanticClassType bc = globalSymbolTable.getClassType(baseClass);
    		SemanticMethodType bmt = (SemanticMethodType)bc.getMethod(currentMethod);
    		verifySemanticTypes(n.type, bmt, n.line_number, "Return type of overridden method does not match base method");
    		
    		SemanticMethodType tmt = (SemanticMethodType)thisClass.getMethod(currentMethod);
    		
    		if(bmt.params.size() != tmt.params.size())
    		{
    			printError("Overridden method does not have the same number of parameters as base method", n.line_number);
    		}
    		else
    		{
	            Iterator methodArgsIterator = tmt.params.entrySet().iterator();
	            Iterator baseMethodArgsIterator = bmt.params.entrySet().iterator();
	            
	        	while (methodArgsIterator.hasNext()) {
	                Map.Entry methodParamPairs = (Map.Entry)methodArgsIterator.next();
	                Map.Entry baseMethodParamPairs = (Map.Entry)baseMethodArgsIterator.next();
	                
	                verifySemanticTypes(((SemanticType)methodParamPairs.getValue()), ((SemanticType)baseMethodParamPairs.getValue()), n.line_number, "Overridden method parameter types do not match base method parameter types");
	        	}
    		}
    	}
    	
    	currentMethod = lastMethod;
    }

    // Type t;
    // Identifier i;
    public void visit(Formal n) {
    	n.t.accept(this);
    	n.type = n.t.type;
    	n.i.accept(this);
    }

    public void visit(IntArrayType n) {
    	n.type = globalSymbolTable.getSemanticType(n);
    }

    public void visit(BooleanType n) {
    	n.type = globalSymbolTable.getSemanticType(n);
    }

    public void visit(IntegerType n) {
    	n.type = globalSymbolTable.getSemanticType(n);
    }

    // String s;
    public void visit(IdentifierType n) {
    	n.type = globalSymbolTable.getSemanticType(n);
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
        verifySemanticTypes(globalSymbolTable.get("int"), n.e.type, n.line_number);
    }
  
    // Identifier i;
    // Exp e;
    public void visit(Assign n) {
        n.i.accept(this);
        n.e.accept(this);
        
        // CHECKTHIS: May need to do more of an "is assignable" than straight up type match
        verifySemanticTypes(n.i.type, n.e.type, n.line_number);

        n.type = n.i.type;
    }

    // Identifier i;
    // Exp e1,e2;
    public void visit(ArrayAssign n) {
        n.i.accept(this);
        n.e1.accept(this);
        n.e2.accept(this);
        
        verifySemanticTypes(globalSymbolTable.get("int"), n.e1.type, n.line_number);
        
        // CHECKTHIS: There are only int arrays, but we may want to make this more general?
        verifySemanticTypes(globalSymbolTable.get("int"), n.e2.type, n.line_number);

        n.type = globalSymbolTable.get("intArray");
    }

    // Exp e1,e2;
    public void visit(And n) {
        n.e1.accept(this);
        n.e2.accept(this);
        
        verifySemanticTypes(globalSymbolTable.get("boolean"), n.e1.type, n.line_number);
        verifySemanticTypes(globalSymbolTable.get("boolean"), n.e2.type, n.line_number);

        n.type = globalSymbolTable.get("boolean");
    }

    // Exp e1,e2;
    public void visit(LessThan n) {
        n.e1.accept(this);
        n.e2.accept(this);
        
        // CHECKTHIS: Assumes only int types
        verifySemanticTypes(globalSymbolTable.get("int"), n.e1.type, n.line_number);
        verifySemanticTypes(globalSymbolTable.get("int"), n.e2.type, n.line_number);

        n.type = globalSymbolTable.get("boolean");
    }

    // Exp e1,e2;
    public void visit(Plus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        
        verifySemanticTypes(globalSymbolTable.get("int"), n.e1.type, n.line_number);
        verifySemanticTypes(globalSymbolTable.get("int"), n.e2.type, n.line_number);

        n.type = globalSymbolTable.get("int");
    }

    // Exp e1,e2;
    public void visit(Minus n) {
        n.e1.accept(this);
        n.e2.accept(this);
        
        verifySemanticTypes(globalSymbolTable.get("int"), n.e1.type, n.line_number);
        verifySemanticTypes(globalSymbolTable.get("int"), n.e2.type, n.line_number);

        n.type = globalSymbolTable.get("int");
    }

    // Exp e1,e2;
    public void visit(Times n) {
        n.e1.accept(this);
        n.e2.accept(this);
        
        verifySemanticTypes(globalSymbolTable.get("int"), n.e1.type, n.line_number);
        verifySemanticTypes(globalSymbolTable.get("int"), n.e2.type, n.line_number);

        n.type = globalSymbolTable.get("int");
    }

    // Exp e1,e2;
    public void visit(ArrayLookup n) {
        n.e1.accept(this);
        n.e2.accept(this);
               
        // CHECKTHIS: There are only int arrays, but we may want to make this more general?
        verifySemanticTypes(globalSymbolTable.get("intArray"), n.e1.type, n.line_number);
        verifySemanticTypes(globalSymbolTable.get("int"), n.e2.type, n.line_number);

        n.type = globalSymbolTable.get("int");
    }

    // Exp e;
    public void visit(ArrayLength n) {
        n.e.accept(this);
        
        verifySemanticTypes(globalSymbolTable.get("intArray"), n.e.type, n.line_number);

        n.type = globalSymbolTable.get("int");
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public void visit(Call n) {
    	// Save current context
        String lastClass = currentClass;
        String lastMethod = currentMethod;

        // Process parameters against previous context
        for ( int i = 0; i < n.el.size(); i++ ) {
            n.el.get(i).accept(this);
        }
        
        // Set context to new class
    	// Get the class
        n.e.accept(this);
        currentClass = n.e.type.typeName;
        currentMethod = n.i.s;
               
        // Get the method
        n.i.accept(this);
        SemanticMethodType methodType = (SemanticMethodType) n.i.type;
        n.type = globalSymbolTable.get(methodType.typeName);
        
        
        // Verify class exists
        verifyIsClass(n.e.type, n.line_number);
        // Verify method exists
        SemanticClassType sct = (SemanticClassType)n.e.type;

        if(n.i.type == null){
        	printError("Class \"" + sct.typeName + "\" does not contain a method \"" + n.i.s + "\"", n.line_number);
        }

        // Iterate through the parameter list and the method in parallel and see if the types match
        // check the Arity?
        SemanticMethodType smt = (SemanticMethodType)n.i.type;
        if (n.el.size() != smt.params.size())
        {
            printError("Method \"" + smt.typeName + "\" does not have " + n.el.size() + " arguments", n.line_number);
        }
        else
        {
            // The arities match.  Now we can walk through and check types without worrying that the number of params don't match. 
            int index = 0;
            Iterator methodArgsIterator = smt.params.entrySet().iterator();
        	while (methodArgsIterator.hasNext()) {
        		Exp callExpression = n.el.get(index);
                index++;
                Map.Entry methodParamPairs = (Map.Entry)methodArgsIterator.next();
                verifySemanticTypes(callExpression.type, ((SemanticType)methodParamPairs.getValue()), n.line_number);
        	}
        }
        
        // Restore context
        currentClass = lastClass;
        currentMethod = lastMethod;
    }

    // int i;
    public void visit(IntegerLiteral n) {
    	n.type = globalSymbolTable.get("int");
    }

    public void visit(True n) {
    	n.type = globalSymbolTable.get("boolean");
    }

    public void visit(False n) {
    	n.type = globalSymbolTable.get("boolean");
    }

    // String s;
    public void visit(IdentifierExp n) {
    	n.type = resolveIdentifierType(n.s, n.line_number);
    }

    public void visit(This n) {
    	n.type = resolveIdentifierType(currentClass, n.line_number);
    }

    // Exp e;
    public void visit(NewArray n) {
    	n.type = globalSymbolTable.get("intArray");
        n.e.accept(this);
        verifySemanticTypes(globalSymbolTable.get("int"), n.e.type, n.line_number);
    }

    // Identifier i;
    public void visit(NewObject n) {
    	n.i.accept(this);
    	n.type = n.i.type;
    	verifyIsClass(n.type, n.line_number);
    }

    // Exp e;
    public void visit(Not n) {
        n.e.accept(this);
        n.type = n.e.type;
        verifySemanticTypes(globalSymbolTable.get("boolean"), n.type, n.line_number);
    }

    // String s;
    public void visit(Identifier n) {
    	n.type = resolveIdentifierType(n.s, n.line_number);
    }
    
    private void verifySemanticTypes(SemanticType i, SemanticType j, int lineNumber)
    {
    	if(!SemanticType.compare(i, j, this.globalSymbolTable))
    	{
    		String iType = "NULL";
    		String jType = "NULL";
    		
    		if(i != null)
    			iType = i.typeName;
    		
    		if(j != null)
    			jType = j.typeName;
    		
    		printError("type \"" + iType + "\" does not match \"" + jType + "\"", lineNumber);
    	}    	
    }
    
    private void verifySemanticTypes(SemanticType i, SemanticType j, int lineNumber, String message)
    {
    	if(!SemanticType.compare(i, j, this.globalSymbolTable))
    	{
    		String iType = "NULL";
    		String jType = "NULL";
    		
    		if(i != null)
    			iType = i.typeName;
    		
    		if(j != null)
    			jType = j.typeName;
    		
    		printError(message + " - type \"" + iType + "\" does not match \"" + jType + "\"", lineNumber);
    	}    	
    }
    
    private SemanticType resolveIdentifierType(String id, int lineNumber)
    {
    	SemanticType type = null;
    	SemanticClassType thisClass = null;
    	LocalSymbolTable thisLST = null;
    	
    	thisClass = globalSymbolTable.getClassType(currentClass);
    	
    	if(thisClass != null)
    		thisLST = thisClass.getMethodSymbolTable(currentMethod);
    
    	// CHECKTHIS: There is some badness here WRT constructors in class (class method) vs a class type
    	// need to figure out some solution here...  Should be ok so long as we only allow unique identifiers (no repeats)
    	
    	// Check locals
    	if(thisLST != null){
    		type = thisLST.get(id);
    	}
    	if(thisClass != null){
	    	// Check member vars
	    	if(type == null)
	    	{
	    		type = thisClass.getData(id);
	    	}
	    	// Check member methods
	    	if(type == null){
	    		type = thisClass.getMethod(id);
	    	}
    	}
    	// Check globals
    	if(type == null){
    		type = globalSymbolTable.get(id);
    	}
    	// Error
    	if(type == null){
    		printError("Undefined identifier \"" + id + "\"\n\tContext: " + currentClass + "." + currentMethod, lineNumber);
    		type = globalSymbolTable.get("$UNKNOWN");
    	}
    	
    	return type;
    }
    
    private void verifyIsClass(SemanticType t, int lineNumber) {
    	if(!(t instanceof SemanticClassType))
    	{
    		printError("\"" + t.typeName + "\" is not a type", lineNumber);
    	}
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
