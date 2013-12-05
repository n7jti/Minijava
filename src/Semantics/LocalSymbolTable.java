package Semantics;

import java.util.*;

public class LocalSymbolTable {
    //instace variables
     public Map<String, SemanticType> localMap;
     public Map<String, SemanticType> formalMap;
     public String name;
     
     // constructor
     public LocalSymbolTable() {
         // Setup the classes
         localMap = new LinkedHashMap<String, SemanticType>();
         formalMap = new LinkedHashMap<String, SemanticType>();
     }

     
     public void putLocal(String id, SemanticType type){
         localMap.put(id, type);
     }

     public SemanticType getLocal(String id){
         return localMap.get(id);
     }

     public void putFormal(String id, SemanticType type){
         formalMap.put(id, type);
     }

     public SemanticType getFormal(String id){
         return formalMap.get(id);
     }
     
     public SemanticType get(String id){
    	 SemanticType type = getFormal(id);
    	 if(type == null)
    		 type = getLocal(id);
    	 
    	 return type;
     }

     int Offset(String id, Map<String, SemanticType> map)
     {
        int pos = 0;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            if ( id.equals((String)pairs.getKey())  )
            {
                break;
            }
            pos++;
        }

        return pos;
     }

     public int getOffsetOf(String id){
         int offset = 0;
         if (null != getFormal(id))
         {

             // some formals are above rbp, and some are below.  
             // the 'this' pointer and the first five are below, the rest are above.
             offset = Offset(id, this.formalMap);
             if (offset < 5)
             {
                 // These are below rbp, below the locals, in order from left to right
                 // The -2 comes from the fact that the locals start one slot below rbp
                 // and the 'this' pointer is the first register variable.
                 int countOfLocals = this.localMap.size();
                 offset = -2 - offset - countOfLocals;
             }
             else
             {
                 // These are on the stack, above ebp, pushed from right to left
                 // We add two to the offset, to get past two things pushed on 
                 // the stack during the call
                 offset = offset - 5;
                 offset = offset + 2;
             }
         }
         else
         {
             // locals are below rbp;
             offset = -(Offset(id, this.localMap) + 1);
         }
         return offset * 8;
     }

     public boolean isLocal(String id)
     {
         return (null != this.get(id));
     }
     
     public void accept(Semantics.Visitor v) {
    	    v.visit(this);
	  }
}