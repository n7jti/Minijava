//
// ast.h -- root of inheritence for AST 
//
#pragma once

class ASTNode
{
public:
    ASTNode(int lineNumber);
    virtual ~ASTNode();

private:
    int _lineNumber; 
};
