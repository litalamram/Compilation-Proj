package AST;

public abstract class AST_VAR extends AST_Node
{
    public boolean isRight = false;
    public boolean IsInFunctionScope = false;
    public boolean isFunctionParam = false;
    public boolean isFunctionLocalVar = false;
    public boolean isArrayVar = false;
    public boolean isStringVar = false;
    public boolean isGlobal = false;
    public boolean isClassFieldInFunc = false;
    public int offset = 0;
}
