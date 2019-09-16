package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_VARDEC_EXP extends AST_VAR {
    public AST_EXP exp;
    public String type;
    public String varName;
    public TYPE_CLASS fatherClass;
    public int typeLine;
    public int varNameLine;
    public int offsetGlobal = 0;
    boolean isInClassScope = false;

    /******************/
    /* CONSTRUCTOR(S) */

    /******************/
    public AST_VARDEC_EXP(String type, String varName, AST_EXP exp, int typeLine, int varNameline) {
        /******************************/
        /* SET A UNIQUE SERIAL NUMBER */
        /******************************/
        SerialNumber = AST_Node_Serial_Number.getFresh();

        /***************************************/
        /* PRINT CORRESPONDING DERIVATION RULE */
        /***************************************/
        /***************************************/
        if (exp != null)
            System.out.format("====================== varDec -> ID(%s) ID(%s) = exp ; \n", type, varName);
        else System.out.format("====================== varDec -> ID(%s) ID(%s) ; \n", type, varName);
        /*******************************/
        /* COPY INPUT DATA NENBERS ... */
        /*******************************/
        this.type = type;
        this.varName = varName;
        this.exp = exp;
        this.fatherClass = null;
        this.typeLine = typeLine;
        this.varNameLine = varNameline;

    }

    /*****************************************************/
    /* The printing message for a subscript var AST node */

    /*****************************************************/
    public void PrintMe()
    {
        /*************************************/
        /* AST NODE TYPE = AST SUBSCRIPT VAR */
        /*************************************/
        System.out.print("AST NODE VARDEC EXP\n");

        /****************************************/
        /* RECURSIVELY PRINT VAR + SUBSRIPT ... */
        /****************************************/
        System.out.println(type);
        System.out.println(varName);
        if(this.exp != null) exp.PrintMe();

        /***************************************/
        /* PRINT Node to AST GRAPHVIZ DOT file */
        /***************************************/

        AST_GRAPHVIZ.getInstance().logNode(
                SerialNumber,
                "VARDEC []\nEXP\n");

        /****************************************/
        /* PRINT Edges to AST GRAPHVIZ DOT file */
        /****************************************/
        if (this.exp != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
    }


    public TYPE SemantMe() {
        TYPE t, expressionType, testedType;
        TYPE_CLASS fatherClass, sonClass;
        TYPE_ARRAY arrayArg = null, arrayArg2 = null;
        TYPE_VAR_DEC varDecType;
        TYPE_FUNCTION functionType;
        SYMBOL_TABLE_ENTRY entry;
        isInClassScope = SYMBOL_TABLE.getInstance().isInClassScope();
        IsInFunctionScope = SYMBOL_TABLE.getInstance().isInFunctionScope();
        if(SYMBOL_TABLE.getInstance().getScopeCount() == 0){
            isGlobal = true;
            sir_MIPS_a_lot.globalsNum++;
            this.offsetGlobal = sir_MIPS_a_lot.globalsNum-1;
        }
        /**********************************************/
        /* [1] Check If Type exists and get exp type */
        /********************************************/
        if (AST_Node.isReservedWord(varName)) {
            System.out.format("%s\n", "Error from : AST_VARDEC_EXP");
            System.out.format(">> ERROR [%d] cannot declar variable of reserved name %s\n", this.varNameLine, varName);
            throw new MyRunTimeException(this.varNameLine);
        }

        t = SYMBOL_TABLE.getInstance().find(type);
        if (t == null) {
            System.out.format("%s\n", "Error from : AST_VARDEC_EXP");
            System.out.format(">> ERROR [%d] non existing type %s\n", this.typeLine, type);
            throw new MyRunTimeException(this.typeLine );
        }
        //check the type is one of: class,array,int,string
        if(!t.isClass() &&
                !t.isArray() &&
                !t.isEquals(TYPE_INT.getInstance()) &&
                !t.isEquals(TYPE_STRING.getInstance())
        ) {
            System.out.format("%s","ERROR FROM AST_VARDEC_EXP");
            System.out.format(">> ERROR [%d]  %s is not a type\n", this.typeLine, type);
            throw new MyRunTimeException(this.typeLine);
        }

        /**************************************/
        /* [2] Check That Name does NOT exist */
        /**************************************/
        entry = SYMBOL_TABLE.getInstance().findEntry(varName);
        // scope check
        if (entry != null) {
            //check name is not class name or array type name
            if (SYMBOL_TABLE.getInstance().isNameTakenByClassOrArray(varName)) {
                System.out.format("Error from : AST_VARDEC_EXP\n");
                System.out.format(">> ERROR [%d] varName %s is a class\n",this.varNameLine,varName);
                throw new MyRunTimeException(this.varNameLine);
            }

            if (entry.scopeCount == SYMBOL_TABLE.getInstance().getScopeCount()) {
                System.out.format("%s\n", "Error from : AST_VARDEC_EXP");
                System.out.format(">> ERROR [%d] variable %s already exists in this scope \n", this.varNameLine, varName);
                throw new MyRunTimeException(this.varNameLine);
            }

        }


        if (isInClassScope && !IsInFunctionScope) {
            this.fatherClass = SYMBOL_TABLE.getInstance().getTypeClass().father;
            if (this.fatherClass != null) {
                if (this.fatherClass.getInheritedFieldType(varName) != null || this.fatherClass.doesAncestorsHaveMethod(varName) != null) {
                    System.out.format("%s\n", "Error from : AST_VARDEC_EXP");
                    System.out.format(">> ERROR [%d] variable %s already exists in father class\n", this.varNameLine, varName);
                    throw new MyRunTimeException(this.varNameLine );
                }
            }
        }
        /*******************/
        /* [3] Semant exp */
        /******************/
        if (exp != null) {
            if (isInClassScope && !IsInFunctionScope){
                if(!(exp instanceof AST_EXP_INT)
                        && !(exp instanceof AST_EXP_NEGATIVE_NUM)
                        && !(exp instanceof AST_EXP_STRING)
                        && !(exp instanceof  AST_EXP_NIL)){
                    System.out.format("Error from : AST_VARDEC_EXP\n");
                    System.out.format(">> ERROR [%d] illigal assignment for class var in AST_VARDEC_EXP\n", exp.line);
                    throw new MyRunTimeException(exp.line);
                }
            }

            expressionType = exp.SemantMe();
            if (expressionType == null) {
                System.out.format("Error from : AST_VARDEC_EXP\n");
                System.out.format(">> ERROR [%d] illigal exp in VARDEC_EXP\n", exp.line);
                throw new MyRunTimeException(exp.line);
            }
            if(expressionType.isString())
            {
                isStringVar = true;
            }

            /**************************************/
            /* [4] Check types match */
            /**************************************/
            testedType = expressionType.getTypeToBeCompared();
            t = t.getTypeToBeCompared();


            if (!t.isEquals(testedType)) {
                // inheritance is legal
                if (t.isClass() && testedType.isClass()) {
                    fatherClass = (TYPE_CLASS) t;
                    sonClass = (TYPE_CLASS) testedType;
                    if (!(fatherClass.gotFromAncestor(sonClass))) {
                        System.out.format("Error from : AST_VARDEC_EXP\n");
                        System.out.format(">> ERROR [%d] father doesn't derive son %s,%s\n", exp.line, t.name, testedType.name);
                        throw new MyRunTimeException(exp.line);
                    }
                } else if (t.isArray() && testedType.isArray()) {
                    arrayArg = (TYPE_ARRAY) t;
                    arrayArg2 = (TYPE_ARRAY) testedType;
                    if (testedType.name.equals("newID") && arrayArg.t.isEquals(arrayArg2.t)) {
                    } else {
                        System.out.format("Error from : AST_VARDEC_EXP\n");
                        System.out.format(">> ERROR [%d] NOT EQUALS TYPES %s,%s\n", exp.line, t.name, "ARRAY");
                        throw new MyRunTimeException(exp.line);
                    }
                }
                // Assigning nil is allowed.
                else if (testedType.isEquals(TYPE_NIL.getInstance()) && (t.isClass() || t.isArray())) {
                } else {
                    System.out.format("Error from : AST_VARDEC_EXP\n");
                    System.out.format(">> ERROR [%d] NOT EQUALS TYPES %s,%s\n", exp.line, t.name, testedType.name);
                    throw new MyRunTimeException(exp.line);
                }
            }
        }

        /***************************************************/
        /* [5] Enter the Function Type to the Symbol Table */
        /***************************************************/

        varDecType = new TYPE_VAR_DEC(varName, t);
        if (IsInFunctionScope){
            varDecType.isLocal = true;
        }
        varDecType.offset = this.offset;
        if (isGlobal){
            varDecType.offsetGlobal = this.offsetGlobal;
            varDecType.isGlobal = true;
        }
        // Initial value
        if(exp == null){
            if(type.equals("int"))
                exp = new AST_EXP_INT(0);
            else
                exp = new AST_EXP_NIL();
        }
        SYMBOL_TABLE.getInstance().enter(varName, varDecType);
        return varDecType;

    }

    public TEMP IRme()
    {
        TEMP t = null;
        if (exp != null)
        {
            int stackPointerOffset;
            if (isGlobal){
                // move stack pointer
                stackPointerOffset = (1+1)*4;
                IR.getInstance().Add_IRcommand(new IRcommand_Increase_StackPointer(-stackPointerOffset));
            }
            t = exp.IRme();
            if (IsInFunctionScope){
                IR.getInstance().Add_IRcommand(new IRcommand_StoreToFrame(t, -(this.offset+1)*4));
            }
            else if (isGlobal){
                IR.getInstance().Add_IRcommand(new IRcommand_StoreGlobal(t, this.offsetGlobal*4));
            }
            else if(isInClassScope){
                IR.getInstance().Add_IRcommand(new IRcommand_StoreField(t, (this.offset+1)*4));
            }
            if (isGlobal){
                // move stack pointer
                stackPointerOffset = (1+1)*4;
                IR.getInstance().Add_IRcommand(new IRcommand_Increase_StackPointer(stackPointerOffset));
            }
        }
        return t;
    }

}
