import Scanner.*;
import Parser.*;
import AST.*;
import AST.Visitor.*;
import Semantics.*;
import java_cup.runtime.Symbol;
import java.util.*;

public class TestSemantics {
    public static void main(String [] args) {
        try {
            // create a scanner on the input file
            scanner s = new scanner(System.in);
            parser p = new parser(s);
            Symbol root;
            root = p.parse();
            Program program = (Program)root.value;
            List<String> errors = new ArrayList<String>(); 

            // build the global symbol table
            GlobalSymbolTable globalSymbolTable = new GlobalSymbolTable();
            program.accept(new GlobalSymbolVisitorPass1(globalSymbolTable, errors));
            program.accept(new GlobalSymbolVisitorPass2(globalSymbolTable, errors));
            globalSymbolTable.fixupInheritance();

            // walk the tree
            program.accept(new SemanticVisitor(globalSymbolTable, errors));

            if (0 == errors.size())
            {
                // dump the global symbol table
                PrintSymbolTableVisitor visitor = new Semantics.PrintSymbolTableVisitor();
                globalSymbolTable.accept(visitor);
            }
            else
            {
                System.out.println("Errors Detected:");
                 for ( int i = 0; i < errors.size(); i++ ) {
                     System.out.println(errors.get(i));
                 }
            }
            
            System.out.print("\n");
            System.out.print("\nSemantic Checks Completed"); 

        } catch (Exception e) { 
            // yuck: some kind of error in the compiler implementation
            // that we're not expecting (a bug!)
            System.err.println("Unexpected internal compiler error: " + 
                               e.toString());
            // print out a stack dump
            e.printStackTrace();
        }
    }
}

