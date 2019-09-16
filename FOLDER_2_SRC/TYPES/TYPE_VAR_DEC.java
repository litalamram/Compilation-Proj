package TYPES;
import AST.*;


public class TYPE_VAR_DEC extends TYPE
{
    public TYPE t;
    //save offset for global array
    public int offsetGlobal = -1;
    public AST_CFIELD dataMem;

    public TYPE_VAR_DEC(String name,TYPE type)
    {
        this.name = name;
        this.t = type;
    }
    public boolean isVarDec(){
        return true;
    }
    public TYPE getTypeToBeCompared() {

        return this.t;}

}
