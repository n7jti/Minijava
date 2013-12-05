package Semantics;

import java.util.*;
import Semantics.Visitor;

public class PrintSymbolTableVisitor implements Visitor{

    public PrintSymbolTableVisitor()
    {

    }

    public void visit(GlobalSymbolTable gst)
    {
        indentedPrint("BASE TYPES:");
        Iterator it = gst.baseMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            indentedPrint("  " + pairs.getKey());
        }

        indentedPrint("CLASSES:");
        it = gst.classMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            indentedPrint((String)pairs.getKey());
            increaseIndent();
            ((SemanticClassType)pairs.getValue()).accept(this);
            decreaseIndent();
        }
    }

    public void visit(SemanticBaseType sbt)
    {
        indentedPrint(sbt.typeName);
    }

    public void visit(SemanticClassType sct)
    {
        Iterator it = sct.data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            indentedPrint("DATA " + ((SemanticType)pairs.getValue()).typeName + " " + (String)pairs.getKey());
        }

        it = sct.methods.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            String methodName = (String)pairs.getKey();
            String className = ((SemanticMethodType)pairs.getValue()).sct.typeName;
            indentedPrint("METHOD (" + className +") "+ ((SemanticType)pairs.getValue()).typeName + " " + methodName);

            Iterator itParam = ((SemanticMethodType)pairs.getValue()).params.entrySet().iterator();
            increaseIndent();
            while (itParam.hasNext()){
                Map.Entry paramPairs = (Map.Entry)itParam.next();
                String paramIdentifier = (String)paramPairs.getKey();
                indentedPrint("PARAM " +((SemanticType)paramPairs.getValue()).typeName + " " + paramIdentifier);
            }
            decreaseIndent();
            /*
            LocalSymbolTable lst = sct.getMethodSymbolTable(methodName);
            if(lst != null) {
            	lst.accept(this);
            }
            else
            {
            	indentedPrint(methodName + " does not have a local symbol table.");
            }
            */
        }
    }

    public void visit(SemanticMethodType smt)
    {
    }

    public void visit(SemanticType st)
    {
        indentedPrint(st.typeName);
    }

    public void visit(LocalSymbolTable lst)
    { 	
    	increaseIndent();
    	Iterator it = lst.formalMap.entrySet().iterator();

    	while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            indentedPrint("METHOD PARAM " + ((SemanticType)pairs.getValue()).typeName +": " + (String)pairs.getKey());
        }
    	
    	it = lst.localMap.entrySet().iterator();

    	while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            indentedPrint("METHOD VAR " + ((SemanticType)pairs.getValue()).typeName +": " + (String)pairs.getKey());
        }
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
