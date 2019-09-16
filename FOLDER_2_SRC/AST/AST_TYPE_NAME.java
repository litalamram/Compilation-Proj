/***********/
/* PACKAGE */
/***********/
package AST;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_TYPE_NAME extends AST_Node {
    /****************/
    /* DATA MEMBERS */
    /****************/
    public String type;
    public String name;
    public int offset = 0;

    /******************/
    /* CONSTRUCTOR(S) */

    /******************/
    public AST_TYPE_NAME(String type, String name) {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        this.type = type;
        this.name = name;
    }

    /*************************************************/
    /* The printing message for a type name AST node */

    /*************************************************/
    public void PrintMe() {
        /**************************************/
        /* AST NODE TYPE = AST TYPE NAME NODE */
        /**************************************/
        System.out.format("NAME(%s):TYPE(%s)\n", name, type);

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/
        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                String.format("NAME:TYPE\n%s:%s", name, type));
    }

    /*****************/
    /* SEMANT ME ... */

    /*****************/
    public TYPE SemantMe() {
        TYPE t = SYMBOL_TABLE.getInstance().find(type);
        SYMBOL_TABLE_ENTRY e;
        if (t == null) {
            /**************************/
            /* ERROR: undeclared type */
            /**************************/
            System.out.format("%s\n", "Error from : AST_TYPE_NAME");
            System.out.format(">> ERROR [%d] non existing type %s\n", this.line, type);
            throw new MyRunTimeException(this.line);
        }
        //check the type is one of: class,array,int,string
        if(!t.isClass() &&
                !t.isArray() &&
                !t.isEquals(TYPE_INT.getInstance()) &&
                !t.isEquals(TYPE_STRING.getInstance())
        ) {
            System.out.format("%s","ERROR FROM AST_TYPE_NAME");
            System.out.format(">> ERROR [%d]  %s is not a type\n", this.line, type);
            throw new MyRunTimeException(this.line);
        }

        /*************************************************/
        /* [2] Check If name exists in current scope  */
        /***********************************************/
        e = SYMBOL_TABLE.getInstance().findEntry(name);

        //check if there is already parameter to same function with same name
        if (e != null && e.scopeCount == SYMBOL_TABLE.getInstance().getScopeCount()) {
            System.out.format("%s\n", "Error from : AST_TYPE_NAME");
            System.out.format(">> ERROR [%d] variable %s already exists in this scope\n", this.line, name);
            throw new MyRunTimeException(this.line);
        }
        // checking reserved words
        if (AST_Node.isReservedWord(name)) {
            System.out.format("%s\n", "Error from : AST_TYPE_NAME");
            System.out.format(">> ERROR [%d] cannot declar variable of reserved name %s\n", this.line, name);
            throw new MyRunTimeException(this.line);
        }
        //check name is not class name or array type name
        if (SYMBOL_TABLE.getInstance().isNameTakenByClassOrArray(name)) {
            System.out.format("%s\n", "Error from : AST_TYPE_NAME");
            System.out.format(">> ERROR [%d] variable name %s already exists as class name or array type name\n", this.line, name);
            throw new MyRunTimeException(this.line);
        }

        /*******************************************************/
        /* Enter var with name=name and type=t to symbol table */
        /*******************************************************/

        TYPE varDecType = new TYPE_VAR_DEC(name, t);
        SYMBOL_TABLE.getInstance().enter(name, varDecType);

        e = SYMBOL_TABLE.getInstance().findEntry(name);
        //now idName exists in table
        e.offsetEntry = this.offset;
        e.isEntryParam = true;


        /****************************/
        /* return (existing) type varDecType */
        /****************************/
        return varDecType;
    }
}
