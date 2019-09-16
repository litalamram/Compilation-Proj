package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;

public class AST_EXP_VAR_DOT_ID_EXP_LIST extends AST_EXPREG
{
	/************************/
	/* simple variable name */
	/************************/
	public AST_EXP_LIST list;
	public AST_VAR var;
	TYPE_FUNCTION funcToRun;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_VAR_DOT_ID_EXP_LIST(AST_VAR var, String name, AST_EXP_LIST list)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== exp -> var.ID( %s )(listExp)\n",name);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.list = list;
		this.name = name;
		this.var = var;
		
	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void PrintMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE EXP var.ID( %s )(listExp)\n",name);
		if(this.var!=null) var.PrintMe();
		if(this.list!=null) list.PrintMe();

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("NODE\nEXP_VAR_DOT_ID_EXPLIST\n(%s)",name));
		if (var != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,var.SerialNumber);
		if (list != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,list.SerialNumber);
	}

	public TYPE SemantMe()
	{
		TYPE classFunc,t = null;
		TYPE_CLASS classArg,classArg2,tc = null;
		TYPE_FUNCTION first=null, funcArg, classMathod;
		TYPE_ARRAY arrayArg, arrayArg2;
		TYPE_LIST expList,it,paramsArg;
		TYPE expArg=null, paramArg=null;


		/******************************/
		/* [1] Recursively semant var */
		/******************************/
		if( var == null){
			System.out.format("%s\n", "Error from : AST_EXP_VAR_DOT_ID_EXP_LIST");
			System.out.format(">> ERROR [%d] var is null\n", this.line);
			throw new MyRunTimeException(this.line);
		}

		var.isRight = true;
		t = var.SemantMe();

		/*********************************/
		/* [2] Check the type is a class */
		/*********************************/
		t = t.getTypeToBeCompared();
		if (t.isClass() == false)
		{
			System.out.format("%s\n", "Error from : AST_EXP_VAR_DOT_ID_EXP_LIST");
			System.out.format(">> ERROR [%d] access %s field of a non-class variable %s\n", var.line, this.name, t.name);
			throw new MyRunTimeException(var.line);
		}

		// t must be a class
		tc = (TYPE_CLASS) t;
		// get function type
		first = tc.doesAncestorsHaveMethod(this.name);

		/*********************************************/
		/* [3] name (functionName) does not exist in class var */
		/*********************************************/
		// function is not declared in class
		if(first == null){
			// checking for a recurtion inside a class
			classFunc = SYMBOL_TABLE.getInstance().find(this.name);
			if(classFunc != null && classFunc.isFunction()){
				classMathod = (TYPE_FUNCTION) classFunc;
				if(!classMathod.isMethod){
					System.out.format("%s\n", "Error from : AST_EXP_VAR_DOT_ID_EXP_LIST");
					System.out.format(">> ERROR [%d] function %s does not exist in class %s\n", this.line, this.name, t.name);
					throw new MyRunTimeException(this.line);
				}
				first = classMathod;
			}
			else{
				System.out.format("%s\n", "Error from : AST_EXP_VAR_DOT_ID_EXP_LIST");
				System.out.format(">> ERROR [%d] function %s does not exist in class %s\n", this.line, this.name, t.name);
				throw new MyRunTimeException(this.line);
			}

		}

		funcToRun = first;


		/*********************************************/
		/* [4] check that list is semanticaly valid  */
		/*********************************************/
		if(list != null){

			list.typeListToCompare = first.params;
			list.isComparedTypes = true;
			expList = (TYPE_LIST) list.SemantMe();
		}
		else{
			// check param types size match
			if(first.params != null){
				System.out.format("%s\n", "Error from : AST_EXP_VAR_DOT_ID_EXP_LIST");
				System.out.format(">> ERROR [%d] no parameters to function with params", this.line, this.col);
				throw new MyRunTimeException(this.line);

			}
		}
		return first.returnType;
	}

	public TEMP IRme()
	{
		TEMP object = null;
		TEMP t=null;
		TEMP returnedValue=null;
		TEMP objectToPass;
		int stackPointerOffset = 0;
		int offsetToFrame = 36 +4; // the final +4 is for the old frame pointer saving on stack

		if (var != null){
			//we always want the value (object and not address)
			var.isRight = true;
			object = var.IRme();
		}

		//check object is not null
		String label_illegal_pointer_ref = IRcommand.getFreshLabel("illegal_pointer_ref");
		IR.getInstance().Add_IRcommand(new IRcommand_IsClassRefLegal(object, label_illegal_pointer_ref, true));

		if(list!= null){
			offsetToFrame += (funcToRun.numOfParams)*4;
			objectToPass = TEMP_FACTORY.getInstance().getFreshTEMP();
			IR.getInstance().Add_IRcommand(new IRcommand_LoadFromStack(objectToPass, 4));
			//IR.getInstance().Add_IRcommand(new IRcommand_StoreOldFpToStack("$fp",0));
			IR.getInstance().Add_IRcommand(new IRcommand_Increase_StackPointer(-(offsetToFrame+4+4)));
			IR.getInstance().Add_IRcommand(new IRcommand_StoreToStack(objectToPass, 4));
			// save params to frame
			//list.offsetInFrame = offsetToFrame;
			list.IRme();
			IR.getInstance().Add_IRcommand(new IRcommand_Increase_StackPointer(offsetToFrame+4+4));
		}

		stackPointerOffset = (funcToRun.localVarNum+1+1)*4+offsetToFrame;
		IR.getInstance().Add_IRcommand(new IRcommand_Prolog(object, offsetToFrame,stackPointerOffset, false, false, name));

		// Calling the function
		IR.getInstance().Add_IRcommand(new IRcommand_CallClassFunction(object, funcToRun.offset*4));

		returnedValue = TEMP_FACTORY.getInstance().getFreshTEMP();
		IR.getInstance().Add_IRcommand(new IRcommand_Epilog(returnedValue, offsetToFrame,stackPointerOffset));

		IR.getInstance().Add_IRcommand(new IRcommand_IllegalPointerRef(label_illegal_pointer_ref));

		return returnedValue;
	}
}
