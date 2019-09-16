package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_CFIELD_FUNCDEC extends AST_CFIELD{

	public AST_DEC_FUNCDEC funcDec;
	boolean errSemantSignature = false;
	public int offset;


	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_CFIELD_FUNCDEC(AST_DEC_FUNCDEC funcDec)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== cField -> funcDec\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.funcDec = funcDec;
	}

	/***********************************************/
	/* The default message for an exp var AST node */
	/***********************************************/
	public void PrintMe()
	{
		/************************************/
		/* AST NODE TYPE = EXP VAR AST NODE */
		/************************************/
		System.out.print("AST NODE CFIELD FUNCDEC\n");

		/*****************************/
		/* RECURSIVELY PRINT var ... */
		/*****************************/
		if (funcDec != null){
			funcDec.PrintMe();
		}

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				"CFIELD\nFUNCDEC");

		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,funcDec.SerialNumber);

	}

	public TYPE SemantSignature(){

		funcDec.fatherClass = this.fatherClass;
		funcDec.className = this.className;
		return funcDec.SemantSignature();
	}

	public TYPE SemantBody(){
		return funcDec.SemantMe();
	}

    public TEMP IRme(){
        if (funcDec != null){
            return funcDec.IRme();
        }

        else return null;
    }

}
