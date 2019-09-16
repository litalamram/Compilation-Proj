package AST;
import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;
import MIPS.*;


public class AST_EXP_ID_EXP_LIST extends AST_EXPREG
{
	/************************/
	/* simple variable name */
	/************************/
	public AST_EXP_LIST list;
	TYPE_FUNCTION funcToRun = null;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_EXP_ID_EXP_LIST(AST_EXP_LIST list, String name)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();

		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== exp -> ID( %s )(listExp)\n",name);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
		this.list = list;
		this.name = name;

	}

	/**************************************************/
	/* The printing message for a simple var AST node */
	/**************************************************/
	public void PrintMe()
	{
		/**********************************/
		/* AST NODE TYPE = AST SIMPLE VAR */
		/**********************************/
		System.out.format("AST NODE EXP ID( %s )(listExp)\n",name);
		if (this.list != null) list.PrintMe();

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
				SerialNumber,
				String.format("NODE\nEXP_ID_EXPLIST\n(%s)",name));
		if (list != null) AST_GRAPHVIZ.getInstance().logEdge(SerialNumber,list.SerialNumber);
	}

	public TYPE SemantMe()	{
		TYPE typeName, typeList;
		TYPE_FUNCTION function;

		TYPE_FUNCTION classMathod;
		TYPE classType;
		TYPE_CLASS insideClass, classInst;
		String className;


		/****************************/
		/* [1] Check If Type exists */
		/****************************/

		typeName = SYMBOL_TABLE.getInstance().find(name);


		if (typeName == null){

			// check if we are inside a class
			className = SYMBOL_TABLE.getInstance().getClassName();

			if (className != null){
				// Need to check for name as func in current class and ancestor classes
				classType = SYMBOL_TABLE.getInstance().find(className);

				if(!classType.isClass()){
					System.out.format("%s\n", "Error from : AST_EXP_ID_EXP_LIST");
					System.out.format(">> ERROR [%d] class in table is not a class \n",this.line);
					throw new MyRunTimeException(this.line);
				}
				insideClass = (TYPE_CLASS) classType;
				typeName = insideClass.doesAncestorsHaveMethod(name);

				// function is not declared in class
				if(typeName == null){
					// checking for a recurtion inside a class
					typeName = SYMBOL_TABLE.getInstance().find(this.name);
					if(typeName != null && typeName.isFunction()){
						classMathod = (TYPE_FUNCTION) typeName;
						if(!classMathod.isMethod){
							System.out.format("%s\n", "Error from : AST_EXP_ID_EXP_LIST");
							System.out.format(">> ERROR [%d] function %s does not exist in class %s\n", this.line, this.name, insideClass.name);
							throw new MyRunTimeException(this.line);
						}
					}
					else{
						System.out.format("%s\n", "Error from : AST_EXP_ID_EXP_LIST");
						System.out.format(">> ERROR [%d] function %s does not exist in class %s\n", this.line, this.name, insideClass.name);
						throw new MyRunTimeException(this.line);
					}
				}
			}
			else {

				System.out.format("%s\n", "Error from: AST_EXP_ID_EXP_LIST");
				System.out.format(">> ERROR [%d] non existing type %s\n", this.line, name);
				throw new MyRunTimeException(this.line);
			}
		}

		if (!typeName.isFunction()){
			System.out.format("%s\n","Error from: AST_EXP_ID_EXP_LIST");
			System.out.format(">> ERROR [%d] %s is not a function\n", this.line, name);
			throw new MyRunTimeException(this.line);
		}

		function = (TYPE_FUNCTION) typeName;
		funcToRun = function;

		if (list != null){
			list.isComparedTypes = true;
			list.typeListToCompare = function.params;
			typeList = list.SemantMe();
		}
		else{
			if (function.params != null){
				System.out.format("%s\n","Error from: AST_EXP_ID_EXP_LIST");
				System.out.format(">> ERROR [%d] function %s expects arguments\n", this.line, name);
				throw new MyRunTimeException(this.line);
			}
		}
		return typeName;
	}

	public TEMP IRme()
    	{
    		TEMP returnedValue = null;
    		TEMP object = TEMP_FACTORY.getInstance().getFreshTEMP();
    		TEMP t=null;
    		int stackPointerOffset = 0;
    		int offsetToFrame = 36 +4;// the final +4 is for the old frame pointer saving on stack
    		TEMP objectToPass;

    		if(name.equals("PrintTrace")){
    			if (list != null) { list.isSysCall = true; t = list.IRme(); }
    			IR.getInstance().Add_IRcommand(new IRcommandPrintTrace(t));
    			return null;
    		}

    		if(name.equals("PrintInt")){
    			if (list != null) { list.isSysCall = true; t = list.IRme(); }
    			IR.getInstance().Add_IRcommand(new IRcommandPrintInt(t));
    			return null;
    		}

    		if(name.equals("PrintString")){
    			if (list != null) { list.isSysCall = true; t = list.IRme(); }
    			IR.getInstance().Add_IRcommand(new IRcommandPrintString(t));
    			return null;
    		}

    		if(list!= null){
    			/*offsetToFrame += (list.numOfParams)*4;*/
    			offsetToFrame += (funcToRun.numOfParams)*4;

    			IR.getInstance().Add_IRcommand(new IRcommand_LoadFromStack(object, 4));
    			IR.getInstance().Add_IRcommand(new IRcommand_Increase_StackPointer(-(offsetToFrame+4+4)));
    			IR.getInstance().Add_IRcommand(new IRcommand_StoreToStack(object, 4));
    			// save params to frame
    			list.IRme();
    			IR.getInstance().Add_IRcommand(new IRcommand_Increase_StackPointer(offsetToFrame+4+4));

    		}

    		stackPointerOffset = (funcToRun.localVarNum+1+1)*4+offsetToFrame; //last +1 is for saving pointer to object if this is a method called
    		IR.getInstance().Add_IRcommand(new IRcommand_Prolog(object, offsetToFrame,stackPointerOffset, true, false, name));


    		// Calling Function
    		//if this is a method that is called (recursivly) from a method (of object)
    		if (funcToRun.isMethod){
    			// Calling the function
    			IR.getInstance().Add_IRcommand(new IRcommand_CallClassFunction(object, funcToRun.offset*4));
    		}
    		//if this is a call of a "global" function
    		else{
    			// Calling the function
    			IR.getInstance().Add_IRcommand(new IRcommand_CallFunction(funcToRun.labelName));
    		}


    		returnedValue = TEMP_FACTORY.getInstance().getFreshTEMP();
    		IR.getInstance().Add_IRcommand(new IRcommand_Epilog(returnedValue, offsetToFrame,stackPointerOffset));

    		return returnedValue;
    	}
}
