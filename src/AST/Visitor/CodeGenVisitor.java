package AST.Visitor;

import AST.*;
import Semantics.*;
import java.util.*;  

public class CodeGenVisitor implements Visitor {
    //instance variables
    public GlobalSymbolTable globalSymbolTable;
    int pushCount;
    int condLabelCount = -1;
    String condLabel = "L-1"; // Current conditional label for flow control
    SemanticClassType currentClass;
    SemanticMethodType currentMethod;
    String reg[];

    public CodeGenVisitor(GlobalSymbolTable table){
        globalSymbolTable = table;
        reg = new String[6];
        reg[0] = rdi;
        reg[1] = rsi;
        reg[2] = rdx;
        reg[3] = rcx;
        reg[4] = r8;
        reg[5] = r9;
    }

    // MainClass m;indentedPrint
    // ClassDeclList cl;
    public void visit(Program n) {

        genComment("Program");
        gen(".text");
        gen(".global asm_main");
        gen("");

        n.m.accept(this);
        for ( int i = 0; i < n.cl.size(); i++ ) {
            n.cl.get(i).accept(this);
        }
    }
  
    // Identifier i1,i2;
    // Statement s;
    public void visit(MainClass n) {
        genComment("##############################");
        genComment("");
        genComment("     MAIN");
        genComment("");
        genComment("##############################");
        genLabel("asm_main");
        genProlog(0, n.vl.size());

        n.i1.accept(this);
        n.i2.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }
        for ( int i = 0; i < n.sl.size(); i++ ) {
            n.sl.get(i).accept(this);
        }

        genEpilog(0, n.vl.size());
    }

    // Identifier i;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclSimple n) {
        SemanticClassType previousClass = currentClass;
        currentClass = globalSymbolTable.getClassType(n.i.s);

        n.i.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }

        genComment("##############################");
        genComment("");
        genComment("     " + n.i.s);
        genComment("");
        genComment("##############################");

         /* Generate VTable
         * Simple$$:
         *     .quad 0
         *     .quad Simple$Simple
         *     .quad Simple$one
         *     .quad Simple$two
         *     .quad Simple$three
         */
        // Create table label
        genData();       
        genLabel(asmVTable(n.i.s));
        genQuad("0");
        genQuad(asmConstructor(n.i.s));
        for ( int i = 0; i < n.ml.size(); i++ ) {
            genQuad(asmMethod(n.i.s, n.ml.get(i).i.s));
        }
        

        gen("");

       
        // Generate the Constructor
        genComment("");
        genComment("Constructor");
        genComment("");
        genLabel(n.i.s + "$" + n.i.s);
        genProlog(1,0); // one paremeter: this, no locals
        // TODO: Zero the memory
        // Assign the VTable to the first memory address
        genOp(lea, n.i.s + "$$", rax);
        // Set the vtable into the first position in the "this" pointer. 
        genOp(mov, rax, deref(rdi));
        genOp(mov, rdi, rax);
        genEpilog(1,0);
        

        MethodDecl md;
        SemanticMethodType smt;
        for ( int i = 0; i < n.ml.size(); i++ ) {
            md = n.ml.get(i);
            smt = currentClass.getMethod(md.i.s);

            //The method table for the method contains all the methods
            //from all the base classes. But we only need to generate
            //code for the methods from the current class. So, we're only
            //going to visit those methods that match the current class
            if(n.i.s == smt.sct.typeName)
            {
                this.currentMethod = smt;
                md.accept(this);
            }
        }

        currentClass = previousClass;
    }
 
    // Identifier i;
    // Identifier j;
    // VarDeclList vl;
    // MethodDeclList ml;
    public void visit(ClassDeclExtends n) {
        SemanticClassType previousClass = currentClass;
        currentClass = globalSymbolTable.getClassType(n.i.s);

        n.i.accept(this);
        for ( int i = 0; i < n.vl.size(); i++ ) {
            n.vl.get(i).accept(this);
        }

        genComment("##############################");
        genComment("");
        genComment("     " + n.i.s);
        genComment("");
        genComment("##############################");

         /* Generate VTable
         * Simple$$:
         *     .quad SimpleBase$$
         *     .quad Simple$Simple
         *     .quad Simple$one
         *     .quad SimpleBase$two
         *     .quad Simple$three
         */
        // Create table label
        genData();       
        genLabel(asmVTable(n.i.s));
        genQuad(asmVTable(currentClass.baseClassType.typeName));
        genQuad(asmConstructor(n.i.s));

        Iterator it = currentClass.methods.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            genQuad(asmMethod(
                        ((SemanticMethodType)pairs.getValue()).sct.typeName,
                        (String)pairs.getKey() ));                        
        }

        
        gen("");

        // Generate the Constructor
        genComment("");
        genComment("Constructor");
        genComment("");
        genLabel(n.i.s + "$" + n.i.s);
        genProlog(1,0); // one paremeter: this, no locals
        // TODO: Zero the memory
        // Assign the VTable to the first memory address
        genOp(lea, n.i.s + "$$", rax);
        // Set the vtable into the first position in the "this" pointer. 
        genOp(mov, rax, deref(rdi));
        genOp(mov, rdi, rax);
        genEpilog(1,0);
        

        MethodDecl md;
        SemanticMethodType smt;
        for ( int i = 0; i < n.ml.size(); i++ ) {
            md = n.ml.get(i);
            smt = currentClass.getMethod(md.i.s);

            //The method table for the method contains all the methods
            //from all the base classes. But we only need to generate
            //code for the methods from the current class. So, we're only
            //going to visit those methods that match the current class
            if(n.i.s == smt.sct.typeName)
            {
                this.currentMethod = smt;
                md.accept(this);
            }
        }

        currentClass = previousClass;
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
    public void visit(MethodDecl n) {
        SemanticMethodType previousMethod = currentMethod;
        currentMethod = currentClass.getMethod(n.i.s);

        // Generate the label for the method
        genLabel(asmMethod(currentClass.typeName, currentMethod.name));

        n.t.accept(this);
        n.i.accept(this);

        genComment("");
        genComment(n.i.s);
        genComment("");
        genLabel(currentMethod.sct.typeName + "$" + currentMethod.name);


        genProlog(n.fl.size() + 1, n.vl.size());
        
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

        genEpilog(n.fl.size() + 1, n.vl.size());
        gen("");

        currentMethod = previousMethod;
    }

    // Type t;
    // Identifier i;
    public void visit(Formal n) {
        n.t.accept(this);
        n.i.accept(this);
    }

    public void visit(IntArrayType n) {

    }

    public void visit(BooleanType n) {

    }

    public void visit(IntegerType n) {

    }

    // String s;
    public void visit(IdentifierType n) {

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
        // BUGBUG: Current implementation relies on sub-expressions to leave
        // a true/false in %rax which may lead to extra conditionals being
        // generated below just to set %rax appropriately.  Should try to
        // redesign if time allows to be more efficient.
        
        // Save the last conditional
        String oldCondLabel = condLabel;
        
        // This label must be restored by any callees that create new conditionals
        condLabel = getCondLabel();
        
        // Execute the expression, true/false stored in rax
        n.e.accept(this);

        genOp(cmp, "$0", rax);
        genOp(je, condLabel);
        // Execute if true
        n.s1.accept(this);
        genOp(jmp, condLabel + "DONE");

        // Execute if false
        genLabel(condLabel);
        n.s2.accept(this);
        
        // Done
        genLabel(condLabel + "DONE");
        
        // Restore conditional label
        condLabel = oldCondLabel;
    }

    // Exp e;
    // Statement s;
    public void visit(While n) {
        // BUGBUG: Current implementation relies on sub-expressions to leave
        // a true/false in %rax which may lead to extra conditionals being
        // generated below just to set %rax appropriately.  Should try to
        // redesign if time allows to be more efficient.
        
        // Save the last conditional
        String oldCondLabel = condLabel;
        
        // This label must be restored by any callees that create new conditionals
        condLabel = getCondLabel();
        
        
        genLabel(condLabel);             // Start of loop
        n.e.accept(this);                // Execute cond, result stored in rax
        genOp(cmp, "$0", rax);
        genOp(je, condLabel + "DONE"); // Jump to done if false
        n.s.accept(this);                // Execute while body
        genOp(jmp, condLabel);         // Loop
        genLabel(condLabel + "DONE");    // Done
        
        // Restore conditional label
        condLabel = oldCondLabel;
    }

    // Exp e;
    public void visit(Print n) {
        genPush(rdi); // preserve the 'this' pointer
        n.e.accept(this);
        genOp(mov, rax, rdi);
        genCall("put");
        genPop(rdi);
    }
  
    // Identifier i;
    // Exp e;
    public void visit(Assign n) {
        // Visit the expression and get that in rax
        n.e.accept(this);
        n.i.accept(this);

        // Now move rax into the variable
        LocalSymbolTable lst = currentClass.getMethodSymbolTable(currentMethod.name);
        if(lst.isLocal(n.i.s))
        {
            // if it is a local to the method then we store it there
            genOp(mov, rax, lst.getOffsetOf(n.i.s) + deref(rbp));
        }
        else
        {
            // if it is local to the class then the offset is from the "this" pointer
            genOp(mov, rax, currentClass.getOffset(n.i.s) + deref(rdi));
        }
    }

    // Identifier i;
    // Exp e1,e2;
    public void visit(ArrayAssign n) {
        n.i.accept(this);
        n.e1.accept(this);
        n.e2.accept(this);
        
        // Get the array variable into %rax
        LocalSymbolTable lst = currentClass.getMethodSymbolTable(currentMethod.name);
        if(lst.isLocal(n.i.s))
        {
            // if it is a local to the method then we get it from there
            genOp(mov, lst.getOffsetOf(n.i.s) + deref(rbp), rax);
        }
        else
        {
            // if it is local to the class then the offset is from the "this" pointer
            genOp(mov, currentClass.getOffset(n.i.s) + deref(rdi), rax) ;
        }
        
        genPush(rax);          // Save off the array ref
        n.e1.accept(this);     // Expression will yield array index in %rax
        genOp(add, "$1", rax); // Increment index by 1 to move past length
        genPush(rax);          // Save off index
        n.e2.accept(this);     // Expression value to store in %rax
        
        genPop(r10); // Index
        genPop(r11); // Array address
        
        // Store %rax into Array
        genOp(mov, rax, "0(" + r11 + "," + r10 + ", 8)");
    }

    // Exp e1,e2;
    public void visit(And n) {
        // Save the last conditional
        String oldCondLabel = condLabel;
        condLabel = getCondLabel();
        
        // Assume results are stored in %rax after each expression is evaluated
        n.e1.accept(this);
        genOp(cmp, "$0", rax);
        genOp(je, condLabel);
        n.e2.accept(this);
        genOp(cmp, "$0", rax);
        genOp(je, condLabel);
        // Made it here, so it must be true, and %rax != 0
        genOp(jmp, condLabel + "DONE");
        genLabel(condLabel);
        genOp(mov, "$0", rax); // default to false
        genLabel(condLabel + "DONE");
        
        // Restore conditional label
        condLabel = oldCondLabel;
    }

    // Exp e1,e2;
    public void visit(LessThan n) {
        // Save the last conditional
        String oldCondLabel = condLabel;
        condLabel = getCondLabel();
        
        // Assume results are stored in %rax after each expression is evaluated
        n.e2.accept(this);
        genPush(rax); // Store result
        n.e1.accept(this);
        genPop(r10);
        genOp(cmp, r10, rax);
        genOp(jl, condLabel); // condLabel set by control flow visitors
        genOp(mov, "$0", rax);
        genOp(jmp, condLabel + "DONE");
        genLabel(condLabel);
        genOp(mov, "$1", rax);
        genLabel(condLabel + "DONE");
        
        // Restore conditional label
        condLabel = oldCondLabel;
    }

    // Exp e1,e2;
    public void visit(Plus n) {
        n.e1.accept(this);

        genPush(rax);

        n.e2.accept(this);

        genPop(rdx);
        genOp(add, rdx, rax);
    }

    // Exp e1,e2;
    public void visit(Minus n) {
        // For some reason, the order for subtraction is backwards
        // so visit the 2nd one first. 
        n.e2.accept(this);
        genPush(rax);

        n.e1.accept(this);
        genPop(rdx);

        genOp(sub, rdx, rax);
    }

    // Exp e1,e2;
    public void visit(Times n) {
        n.e1.accept(this);
        genPush(rax);

        n.e2.accept(this);
        genPop(rdx);

        genOp(imul, rdx, rax);
    }

    // Exp e1,e2;
    public void visit(ArrayLookup n) {
        // Expression will yield address to array param in %rax
        n.e1.accept(this);
        genPush(rax); // Save off array
        
        // Expression will yield index in %rax
        n.e2.accept(this);
        
        // Increment index by 1 to move past length field
        genOp(add, "$1", rax);
        genPop(r10);
        
        // Read array value into rax
        genOp(mov, "0(" + r10 + "," + rax + ", 8)", rax);
    }

    // Exp e;
    public void visit(ArrayLength n) {
        // Expression will yield address to array param in %rax
        n.e.accept(this);
        
        // Get array length from first memory address in array
        genOp(mov, deref(rax), rax);
    }

    // Exp e;
    // Identifier i;
    // ExpList el;
    public void visit(Call n) {

        genComment("Calling: " + n.i.s);
        // preserve the 'this' pointer
        genPush(rdi);

        // calculate if we need an extra-push after we push and pop all of the parameter
        boolean fExtraPush = (0 != ((this.pushCount + n.el.size() - 5) % 2));
        if (fExtraPush)
        {
            genPush(r15);
        }

        // Visit all the expressions and push them onto the stack right to left
        for ( int i = n.el.size()-1; i >= 0; i-- ) {
            // visit the expression, the result will be in RAX
            genComment("Evaluate Parameter: " + (i + 1));
            n.el.get(i).accept(this);
            
            // Push it on the stack
            genComment("Push parameter: " + (i + 1));
            genPush(rax);
        }

        // Visit the expression, and the result will be in RAX
        genComment("Evaluate Call Expression");
        n.e.accept(this);

        // The first six parameters go in registers.  Note  that is 5 + this;
        // note, rax is preserved through all of this
        int paramsToPop = n.el.size();
        if (paramsToPop > 5)
        {
            paramsToPop = 5;
        }

        for (int i = 1; i <= paramsToPop; i++)
        {
            genComment("Popping Parameter: " + i );
            genPop(this.reg[i]);
        }

        // use the type information to find the class
        // and the identifier to find the VTable Slot
        SemanticClassType sct = (SemanticClassType)n.e.type;
        n.i.accept(this);
        int offset = sct.getVTableOffset(n.i.s);

        //Time to dispatch through the VTable.  
        // rax points at the object
        // stuff rax into rdi.  First parameter is 'this' pointer
        genComment("Set 'this' pointer");
        genOp(mov,rax,rdi);
        // deref rax once to get onto the vtable
        genOp(mov, deref(rax), rax);

        // Move from the start of the vtable to the correct slot
        genOp(add, asmInt(offset), rax);

        // Deref the function pointer;
        genOp(mov, deref(rax), rax);

        // make the call
        genCall("*" + rax);

        // Need to pop the rest of the params off the stack
        for(int i = paramsToPop; i < n.el.size(); i++)
        {
            genPop(rsi);
        }

        if (fExtraPush)
        {
            genPop(r15);
        }

        genPop(rdi);
    }

    // int i;
    public void visit(IntegerLiteral n) {
        genOp(mov, asmInt(n.i), rax );
    }

    public void visit(True n) {
        genOp(mov, asmInt(1), rax);
    }

    public void visit(False n) {
        genOp(mov, asmInt(0), rax);
    }

    // String s;
    public void visit(IdentifierExp n) {
        LocalSymbolTable lst = currentClass.methodLocalSymbolTables.get(currentMethod.name);
        int offset; 
        if(lst.isLocal(n.s))
        {
            offset = lst.getOffsetOf(n.s);
            // If it is local to the method, then get the offset from rbp
            if (offset == 0)
            {
                genOp(mov, rbp, rax);
            }
            else
            {
                genOp(mov, offset + deref(rbp), rax);
            }
        }
        else
        {
            // If it wasn't an offset to the method, then it is an offset from "this"
            genOp(mov, currentClass.getOffset(n.s) + deref(rdi), rax);
        }
        
    }

    public void visit(This n) {
        // put 'this' in rax
        genOp(mov, rdi, rax);
    }

    // Exp e;
    public void visit(NewArray n) {
        // Execute expression and assume int result in %rax
        n.e.accept(this);
        
        // Minijava only supports int arrays (element size is 8)
        // Reserve a space for length parameter (first element)
        // Leave pointer to array in %rax
        
        genPush(rdi);              // Save off %rdi
        genPush(rax);              // Save off original length
        genOp(add, "$1", rax);     // Create additional space for length
        // Allocate memory
        genOp(mov, rax, rdi);
        genCall("mjmalloc");
        genPop(r10);               // Pop length
        genOp(mov,r10, deref(rax)); // Store length
        genPop(rdi);               // Restore %rdi
    }

    // Identifier i;
    public void visit(NewObject n) {
        /*
        * From the lecture slides:
        * 
        * push   nBytesNeeded      ; obj size + 4
        * call   mallocEquiv      ; addr of bits returned in eax
        * add    esp,4           ; pop nBytesNeeded
        * lea    edx,One$$     ; get method table address
        * mov    [eax],edx        ; store vtab ptr at beginning of object
        * mov    ecx,eax          ; set up this for constructor
        * push   ecx          ; save ecx (constructor might clobber it)
        * <push constructor arguments>    ; arguments (if needed)
        * call   One$One        ; call constructor (no vtab lookup needed)
        * <pop constructor arguments> ; (if needed)
        * pop    eax          ; recover ptr to object
        * mov   [ebp+offsetone],eax   ; store object reference in variable one
        */

        SemanticClassType sct = (SemanticClassType) globalSymbolTable.getClassType(n.i.s);
        genPush(rdi); // preserve the 'this' pointer
        genOp(mov, asmInt(sct.getSize()), rdi);
        genCall("mjmalloc");
        genOp(mov, rax, rdi);
        //VTable Dispatch the Constructor Call. 
        genOp(lea, asmVTable(n.i.s), rax);
        genOp(mov, "8" + deref(rax), rax);
        genCall("*" + rax);
        genPop(rdi); // preserve the 'this' pointer

    }

    // Exp e;
    public void visit(Not n) {
        // Save the last conditional
        String oldCondLabel = condLabel;
        condLabel = getCondLabel();
        genComment("NOT");
        n.e.accept(this);
        genOp(cmp, "$0", rax);
        genOp(je, condLabel);
        genOp(mov, "$0", rax);
        genOp(jmp, condLabel + "DONE");
        genLabel(condLabel);
        genOp(mov, "$1", rax);
        genLabel(condLabel + "DONE");
        
        // Restore conditional label
        condLabel = oldCondLabel;
    }

    // String s;
    public void visit(Identifier n) {

    }

    int genPush(String s)
    {
        int out = pushCount;
        pushCount++;

        genOp(push, s);
        return out;
    }

    int genPop(String s)
    {
        genOp(pop, s);

        pushCount--;
        return pushCount;
    }

    void genCall(String label)
    {
        boolean fAligned = (pushCount %2 == 0);
        if (!fAligned)
        {
            genPush(r15);
        }
        
        genOp(call, label);

        if(!fAligned)
        {
            genPop(r15);
        }
    }

    String asmInt(int n)
    {
        return "$" + n;
    }

    String asmVTable(String s)
    {
        return (s + "$$");
    }

    String asmConstructor(String s)
    {
        return (s + "$" + s);
    }

    String asmMethod(String c, String m)
    {
        return (c + "$" + m);
    }

    String deref(String exp)
    {
        return "(" + exp + ")";
    }

    void gen(String s)
    {
        System.out.println(" " + s);
    }

    void genOp(String op, String operand)
    {
        System.out.println(" " + op + " " + operand);
    }

    void genOp(String op, String src, String dst)
    {
        System.out.println(" " + op + " " + src + ", " + dst);
    }

    void genLabel(String L)
    {
        System.out.println(L + ":");
    }
    
    String getCondLabel()
    {
        condLabelCount++;
        return "L" + condLabelCount;
    }

    void genData()
    {
        System.out.println(".data");
    }
    
    void genQuad(String Q)
    {
        System.out.println(".quad " + Q);
    }

    void genComment(String L)
    {
        System.out.println("# " + L);
    }

    void genProlog(int countOfParameters, int countOfLocals)
    {
        genComment("Prolog: " + countOfParameters + " Params, " + countOfLocals + " locals");
        genPush(rbp);
        genOp(mov, rsp, rbp);

        if (countOfParameters > 6)
        {
            countOfParameters = 6;
        }

        //Allocate extra space so that the RSP is always 16byte aligned
        int cb = (countOfLocals + countOfParameters) * 8;
        if(cb % 16 > 0)
        {
            cb += 16 - cb % 16;
        }

        if (cb > 0)
        {
            genComment("Move stack pointer to bottom + Alignment");
            genOp(sub, asmInt(cb), rsp );
        }

        //spil the parameters into the stack
        genComment("Spill the register parameters onto the stack, below the locals");
        for(int i = 0; i < countOfParameters; i++)
        {
            genOp(mov, reg[i], "-" + ((i+1) * 8 + 8 * countOfLocals) + deref(rbp));
        }
        
        genComment("End of Prolog");
    }

    void genEpilog(int countOfParameters, int countOfLocals)
    {
        genComment("Epilog");
        genOp(mov, rbp, rsp);
        genPop(rbp);
        gen(ret);
    }

    //constants for code gen

    // Registers
    final String rax = "%rax";
    final String rbx = "%rbx";
    final String rcx = "%rcx";
    final String rdx = "%rdx";
    final String rsi = "%rsi";
    final String rdi = "%rdi";
    final String rbp = "%rbp";
    final String rsp = "%rsp";
    final String r8  = "%r8";
    final String r9  = "%r9";
    final String r10 = "%r10";
    final String r11 = "%r11";
    final String r12 = "%r12";
    final String r13 = "%r13";
    final String r14 = "%r14";
    final String r15 = "%r15";

    // Assembly Neumnonics
    final String mov = "movq";
    final String push = "pushq";
    final String call = "call";
    final String ret = "ret";
    final String jmp = "jmp";
    final String je = "je";
    final String jl = "jl";
    final String sub = "subq";
    final String pop = "popq";
    final String imul = "imulq";
    final String add = "addq";
    final String cmp = "cmpq";
    final String lea = "leaq";
}
