package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_STMT_ASSIGN_EXP extends AST_STMT
{
	/***************/
	/*  var := exp */
	/***************/
	public AST_VAR var;
	public AST_EXP exp;

	/*******************/
	/*  CONSTRUCTOR(S) */
	/*******************/
	public AST_STMT_ASSIGN_EXP(AST_VAR var,AST_EXP exp)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.print("====================== stmt -> var ASSIGN exp SEMICOLON\n");

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.var = var;
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
		System.out.print("AST NODE ASSIGN STMT EXP\n");

		/***********************************/
		/* RECURSIVELY PRINT VAR + EXP ... */
		/***********************************/
		if (var != null) var.PrintMe();
		if (exp != null) exp.PrintMe();

		/***************************************/
		/* PRINT Node to AST GRAPHVIZ DOT file */
		/***************************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			"ASSIGN\nleft := right\n");
		
		/****************************************/
		/* PRINT Edges to AST GRAPHVIZ DOT file */
		/****************************************/
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,exp.SerialNumber);
	}

	/*********************************************************/
	/* The AST node semantic validation		 				 */
	/*********************************************************/
	public TYPE SemantMe()
	{
		TYPE varType = null, expType = null;
		TYPE_CLASS varClassType, expClassType;
		TYPE_ARRAY varTypeArray, expTypeArray;

		/**********************************************/
		/* [1] Check If Type exists and get exp type */
		/********************************************/

		if (var == null || exp == null){
			System.out.format("%s\n", "Error from : AST_STMT_ASSIGN");
			System.out.format(">> ERROR [%d] var or exp is null\n",this.line);
			throw new MyRunTimeException(this.line);
		}
		varType = var.SemantMe();
		if (varType == null){

			System.out.format("%s\n", "Error from : AST_STMT_ASSIGN");
			System.out.format(">> ERROR [%d] illigal var in STMT_ASSIGN - type of var is null\n",var.line);
			throw new MyRunTimeException(var.line);
		}
		expType = exp.SemantMe();
		if (expType == null){

			System.out.format("%s\n", "Error from : AST_STMT_ASSIGN");
			System.out.format(">> ERROR [%d] illigal exp in STMT_ASSIGN - type of exp is null\n", exp.line);
			throw new MyRunTimeException(exp.line);
		}

		/**************************************/
		/* [2] Check types match */
		/**************************************/


		varType = varType.getTypeToBeCompared();
		expType = expType.getTypeToBeCompared();

		if (!varType.isEquals(expType)){
			// inheritance is legal
			if (varType.isClass() && expType.isClass()){
				// cast varType and expType
				varClassType = (TYPE_CLASS) varType;
				expClassType = (TYPE_CLASS) expType;
				if (!(varClassType.gotFromAncestor(expClassType))){
					System.out.format("%s\n", "Error from : AST_STMT_ASSIGN");
					System.out.format(">> ERROR [%d] father %s doesn't have a son %s\n",exp.line, varType.name, expType.name);
					throw new MyRunTimeException(exp.line);
				}
			}
			else if(varType.isArray()){
				if (exp instanceof AST_NEW_EXP_NEWID_BRACKES){//(exp.getClass().getSimpleName().equals("AST_NEW_EXP_NEWID_BRACKES")){
					varTypeArray = (TYPE_ARRAY) varType;
					expTypeArray = (TYPE_ARRAY) expType;
					if (!varTypeArray.t.isEquals(expTypeArray.t)){
						System.out.format("%s\n", "Error from : AST_STMT_ASSIGN");
						System.out.format(">> ERROR [%d] NOT EQUALS TYPES %s, %s\n",exp.line, varTypeArray.t.name, expType.name);
						System.out.format("%s\n", varTypeArray.name);
						throw new MyRunTimeException(exp.line);
					}

				}
			}
			else if (!expType.isEquals(TYPE_NIL.getInstance()) || (!varType.isClass() && !varType.isArray())){
				System.out.format("%s\n", "Error from : AST_STMT_ASSIGN");
				System.out.println("Error from : AST_STMT_ASSIGN");
				System.out.format(">> ERROR [%d] NOT EQUALS TYPES %s,%s\n",exp.line ,varType.name, expType.name);
				throw new MyRunTimeException(exp.line);
			}
		}

		/*********************************************************/
		/* [4] Return TYPE */
		/*********************************************************/

		return varType;

	}
	public TEMP IRme()
	{
		TEMP varTemp = var.IRme(); // in arrays/strings: contain address in heap
		TEMP expTemp = exp.IRme();// from var simple: varTemp should contain var adrress
		if (var.isArrayVar || var.isStringVar){
			IR.getInstance().Add_IRcommand(new IRcommand_StoreToHeap(varTemp,expTemp));
		}
		else {
			IR.getInstance().Add_IRcommand(new IRcommand_Store(varTemp,expTemp));
		}
		return varTemp;
	}
}
