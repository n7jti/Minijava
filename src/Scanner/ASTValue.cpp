//
// ASTValue.cpp
//

#include "astvalue.h"
ASTValue::ASTValue(int value, int lineNumber)
    : ASTNode(lineNumber)
    , _value(value)
{

}

ASTValue::~ASTValue()
{
    
}
