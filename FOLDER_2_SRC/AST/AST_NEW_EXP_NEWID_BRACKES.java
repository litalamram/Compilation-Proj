package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_NEW_EXP_NEWID_BRACKES extends AST_NEW_EXP
{
    public AST_EXP value;
    public String type;
    public boolean isClassArray = false;

    /******************/
    /* CONSTRUCTOR(S) */
    /******************/
    public AST_NEW_EXP_NEWID_BRACKES(String type, AST_EXP value)
    {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        System.out.format("====================== Exp -> new ID ( %s) [ exp ]\n", type);

        /*******************************/
        /* COPY INPUT DATA NENBERS ... */
        /*******************************/
        this.value = value;
        this.type = type;
    }

    /************************************************/
    /* The printing message for an INT EXP AST node */
    /************************************************/
    public void PrintMe()
    {
        /*******************************/
        /* AST NODE TYPE = AST INT EXP */
        /*******************************/
        System.out.print("AST NODE NEW EXP NEW ID BRACKS\n");
        if(value!=null) value.PrintMe();

        /*********************************/
        /* Print to AST GRAPHIZ DOT file */
        /*********************************/
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                "NEW EXP\nNEW ID BRACKS\n");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,value.SerialNumber);
    }

    public TYPE SemantMe()
    {
        TYPE t, valueType;


        /****************************/
        /* [1] Check If Type exists */
        /****************************/

        t = SYMBOL_TABLE.getInstance().find(type);
        if (t == null)
        {
            System.out.format(">> ERROR [%d] non existing type %s\n",this.line ,type);
            throw new MyRunTimeException(this.line);
        }
        isClassArray = t.isClass();
        valueType = value.SemantMe();
        if (valueType == null)
        {
            System.out.format(">> ERROR [%d] non existing type of var\n", this.line);
            throw new MyRunTimeException(this.line);
        }

        valueType = valueType.getTypeToBeCompared();
        if(!valueType.isEquals(TYPE_INT.getInstance())){
            System.out.format(">> ERROR [%d] NOT EQUALS TYPES %s,%s\n", value.line, t.name, valueType.name);
            throw new MyRunTimeException(value.line);
        }


        /***************************************************/
        /* [2] Enter the Function Type to the Symbol Table */
        /***************************************************/
        TYPE_ARRAY arrayType = new TYPE_ARRAY("newID", t, false);
        SYMBOL_TABLE.getInstance().enter("newID", arrayType);

        /*********************************************************/
        /* [3] Return value is irrelevant for class declarations */
        /*********************************************************/

        return arrayType;

    }

    public TEMP IRme()
    {
        TEMP arraySize = value.IRme();
        TEMP arrayAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
        IR.getInstance().Add_IRcommand(new IRcommand_AllocateArray(arrayAddress,arraySize,isClassArray));
        return arrayAddress;
    }
}