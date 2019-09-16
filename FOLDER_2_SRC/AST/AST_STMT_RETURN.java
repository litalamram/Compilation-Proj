package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_STMT_RETURN extends AST_STMT {
	
	/***************/
	/*  return exp; */
	/***************/
	public AST_EXP exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_RETURN(AST_EXP exp)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> return exp ;\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.exp = exp;
	}

	/*********************************************************/
	/* The printing message for an assign statement AST node */
	/*********************************************************/
	public void PrintMe()
	{
		/********************************************/
		/* AST NODE TYPE = AST ASSIGNMENT STATEMENT */
		/********************************************/
		System.out.print("AST NODE RETURN STMT\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"RETURN\nexp;\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		if (exp!=null)
			AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}

	
	public TYPE SemantMe()
	{
		TYPE returnType, tempType;

		returnType = SYMBOL_TABLE.getInstance().find(SYMBOL_TABLE.FUNCTION_SCOPE);

		if (returnType ==  null){
			System.out.format("%s\n","Error from : AST_STMT_RETURN");
			System.out.format(">> ERROR return statment without not in function scope [%d]\n", this.line);
			throw new MyRunTimeException(this.line);
		}

		returnType = returnType.getTypeToBeCompared();

		if (exp == null){
			if (!returnType.isEquals(TYPE_VOID.getInstance())){
				System.out.format("%s\n","Error from : AST_STMT_RETURN");
				System.out.format(">> ERROR return non void type on void function [%d]\n", this.line);
				throw new MyRunTimeException(this.line);
			}else return TYPE_VOID.getInstance();

		}
		
		
		tempType = exp.SemantMe();  
		tempType = tempType.getTypeToBeCompared();

		if (tempType == null){
			System.out.format("%s\n","Error from : AST_STMT_RETURN");
			System.out.format(">> ERROR in SemantMe exp() [%d]\n", exp.line);
			throw new MyRunTimeException(exp.line);
		}

		if(tempType.name.equals("void")){
			System.out.format("Error from : AST_STMT_RETURN\n");
			System.out.format(">> ERROR cannot return void from function with non void return type [%d]\n", this.line);
			throw new MyRunTimeException(this.line);

		}
		else if (!tempType.isEquals(returnType)){
			if(tempType.isClass() && returnType.isClass()){
				TYPE_CLASS fatherClass = (TYPE_CLASS) returnType;
				TYPE_CLASS sonClass = (TYPE_CLASS) tempType;

				if (!(fatherClass.gotFromAncestor(sonClass))){
					System.out.format("Error from : AST_STMT_RETURN\n");
					System.out.format(">> ERROR [%d] father %s doesn't have a son %s\n",this.line, tempType.name, returnType.name);
					throw new MyRunTimeException(this.line);
				}
			}

			else if (tempType.isArray() && returnType.isArray()){
				TYPE_ARRAY arrayArg = (TYPE_ARRAY) returnType;
				TYPE_ARRAY arrayArg2 = (TYPE_ARRAY) tempType;
				if(!(tempType.name.equals("newID") && arrayArg.t.isEquals(arrayArg2.t))){
					System.out.format("Error from : AST_STMT_RETURN\n");
					System.out.format(">> ERROR [%d] NOT EQUALS TYPES- %s doesn't have type ARRAY\n", this.line, tempType	.name);
					throw new MyRunTimeException(this.line);
				}
			}
			
			else if(tempType.isEquals(TYPE_NIL.getInstance()) && (returnType.isClass() || returnType.isArray())){
				return tempType;

			}

			else{
				System.out.format("%s\n", "Error from : AST_STMT_RETURN");
				System.out.format(">> ERROR [%d] expect %s to be returnrd but %s is being returned instead\n",this.line, returnType.name, tempType.name);
				throw new MyRunTimeException(this.line);
			}
		}

		return tempType;
	}

	public TEMP IRme()
	{
		TEMP t = null;
		boolean isMethod = SYMBOL_TABLE.getInstance().isInClassScope();
		if (exp != null){
			t = exp.IRme();
			//store return value before function parameters are stored (+1 is for going to allocated space of returned value in frame)
			IR.getInstance().Add_IRcommand(new IRcommand_StoreToFrame(t, (funcParamNum+1)*4));
		}
		if(!functionName.equals("main") || isMethod)
			IR.getInstance().Add_IRcommand(new IRcommand_Jump("$ra", true));
		else //we are in main
			IR.getInstance().Add_IRcommand(new IRcommand_Jump("Label_0_exit", false));
		return t;
	}

}
