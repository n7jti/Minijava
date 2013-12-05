package Semantics;
import java.util.*;
import AST.Visitor.Visitor;
import AST.Visitor.SemanticVisitor;

//Contains a static description of the type from the Declearation;
public class SemanticClassType extends SemanticType {
    //instance variables
    public SemanticClassType baseClassType;
    public Map<String, SemanticType> data;
    public Map<String, SemanticType> methods;
    public Map<String, LocalSymbolTable> methodLocalSymbolTables;

    // constructor
    public SemanticClassType(String typeName, SemanticClassType baseClassType ) {
      super(typeName);
      this.baseClassType = baseClassType;
      data = new LinkedHashMap<String, SemanticType>();
      methods = new LinkedHashMap<String, SemanticType>();
      methodLocalSymbolTables = new LinkedHashMap<String, LocalSymbolTable>();
    }

    public int getSize()
    {
        //Calculate the size of the object
        int size = 8; // room for the VTable
        // every local is either an int, bool, or pointer.  Everyring is 8 bytes
        size += 8 * this.data.size();
        return size; 
    }

    public void addData(String id, SemanticType type)
    {
      data.put(id,type);
    }

    public SemanticType getData(String id)
    {
        return data.get(id);
    }

    // CHECKTHIS: Do we need to overload methods with different input param types/number of params?
    public void addMethod(String id, SemanticType type)
    {
        methods.put(id, type);
    }

    public SemanticMethodType getMethod(String id)
    {
        return (SemanticMethodType)methods.get(id);
    }
    
    public void addMethodSymbolTable(String id, LocalSymbolTable table)
    {
    	methodLocalSymbolTables.put(id, table);
    }
    
    public LocalSymbolTable getMethodSymbolTable(String id)
    {
    	return methodLocalSymbolTables.get(id);
    }

    public boolean compare(SemanticType i, GlobalSymbolTable globalSymbolTable)
    {
        boolean fMatch = false;
        if (i == null)
        {
            return false;
        }
        //Check for a trival match
        if (typeName == i.typeName)
        {
            return true;
        }
        
        //see if i is a class.  we have to get this from the symbol table because i might
        //be a method and it would be a "method type".  So we want to get the associated class
        SemanticClassType sct = globalSymbolTable.getClassType(i.typeName);
        if (null == sct)
        {
            // we are a class and we're compairing to something that isn't a class
            return false;
        }

        // i is a class, and it isn't a trival match, so now we just run up its inheritance chain
        // and look for a match. 
        while(null != sct.baseClassType)
        {
            sct = sct.baseClassType;
            if(this.typeName == sct.typeName)
            {
                return true;
            }
        }

        // so, no matches up the inheritance tree for sct. 
        // Let's recursively run up our own inheritance tree, if we have one.
        if(null != this.baseClassType)
        {
            return this.baseClassType.compare(i, globalSymbolTable);
        }

        return false;
    }

    public void fixupInheritance()
    {
        this.fixupMethods();
        this.fixupData();
    }

    private void mergeMethods(Map<String, SemanticType> meth)
    {
        // Get the base class methods
        if(null != this.baseClassType)
        {
            this.baseClassType.mergeMethods(meth);
        }

        //Merge our methods into the list
        Iterator it = this.methods.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry pairs = (Map.Entry)it.next();
            String name = (String)pairs.getKey();
            meth.put(name,(SemanticType)pairs.getValue());
        }
    }

    private void fixupMethods()
    {
        Map<String, SemanticType> met = new LinkedHashMap<String, SemanticType>();
        this.mergeMethods(met);
        this.methods = met;
    }

    private void mergeData(Map<String, SemanticType> dat)
    {
        // Get the base class methods
        if(null != this.baseClassType)
        {
            this.baseClassType.mergeData(dat);
        }

        //Merge our data into the list
        Iterator it = this.data.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry pairs = (Map.Entry)it.next();
            String name = (String)pairs.getKey();
            dat.put(name,(SemanticType)pairs.getValue());
        }
    }

    private void fixupData()
    {
        Map<String, SemanticType> dat = new LinkedHashMap<String, SemanticType>();
        this.mergeData(dat);
        this.data = dat;
    }

    public void accept(Semantics.Visitor v) {
        v.visit(this);
    }

    public int getVTableOffset(String identifier)
    {
        //Calculate the VTable Slot of the method identifier passed in
        int slot = 0; // slot 0 is the slot for the base pointer
        slot++;       // slot 1 is the slot for the constructor
        
        Iterator it = this.methods.entrySet().iterator();
        while(it.hasNext())
        {
            slot++; // Advance the slot number
            Map.Entry pairs = (Map.Entry)it.next();
            String method = (String)pairs.getKey();
            if (identifier.equals(method))
            {
                break;
            }
        }

        return slot * 8;
    }

    public boolean isLocal(String id)
    {
       return (null != getData(id));
    }

    public int getOffset(String id)
    {
        int slot = 0; // Offset 0 is the VTable
        
        Iterator it = this.data.entrySet().iterator();
        while(it.hasNext())
        {
            slot++; // Advance the slot number
            Map.Entry pairs = (Map.Entry)it.next();
            String method = (String)pairs.getKey();
            if (id.equals(method))
            {
                break;
            }
        }
        return slot * 8;
    }
}
