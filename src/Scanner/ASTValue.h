//
// ASTValue.h -- abstracts a number
//

#pragma once
#include "ast.h"

class ASTValue : public ASTNode
{
public:
    ASTValue(int value, int lineNumber);
    virtual ~ASTValue();

private:
    ASTValue(const ASTValue& other); // Delete the copy constructor
    int _value;
};
