package AST;

import TYPES.*;
import SYMBOL_TABLE.*;
import TEMP.*;
import IR.*;

public class AST_VAR_SIMPLE extends AST_VAR
{
	/************************/
	/* simple variable name */
	/************************/
	public String name;
	
	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public AST_VAR_SIMPLE(String name)
	{
		/******************************/
		/* SET A UNIQUE SERIAL NUMBER */
		/******************************/
		SerialNumber = AST_Node_Serial_Number.getFresh();
	
		/***************************************/
		/* PRINT CORRESPONDING DERIVATION RULE */
		/***************************************/
		System.out.format("====================== var -> ID( %s )\n", name);

		/*******************************/
		/* COPY INPUT DATA NENBERS ... */
		/*******************************/
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
		System.out.format("AST NODE SIMPLE VAR( %s )\n",name);

		/*********************************/
		/* Print to AST GRAPHIZ DOT file */
		/*********************************/
		AST_GRAPHVIZ.getInstance().logNode(
			SerialNumber,
			String.format("SIMPLE\nVAR\n(%s)",name));
	}

	public TYPE SemantMe(){
		SYMBOL_TABLE_ENTRY entry;
		TYPE classType;
		TYPE_CLASS insideClass, classInst;
		String className;
		TYPE_ARRAY arr;
		TYPE_VAR_DEC varDec;
		TYPE_FUNCTION callingFunc = null;
		this.IsInFunctionScope = SYMBOL_TABLE.getInstance().isInFunctionScope();

		// check if we are inside a class
		className = SYMBOL_TABLE.getInstance().getClassName();
		entry = SYMBOL_TABLE.getInstance().findEntry(name);
		if (entry != null){
			this.isFunctionParam = entry.isEntryParam;
			if (entry.type.isVarDec()){ //if we are in var decleration
				varDec = (TYPE_VAR_DEC) entry.type;
				this.isFunctionLocalVar = entry.type.isLocal;
				this.isGlobal = varDec.isGlobal;
				if (this.isGlobal){
					this.offset = varDec.offsetGlobal;
				}
				else{
					this.offset = varDec.offset;
				}
				if(varDec.t.isArray()) //check that this is an array.
					this.isArrayVar = true;
				else if(varDec.t.isString())
					this.isStringVar = true;
			}

			if (isFunctionParam){
				offset = entry.offsetEntry;
			}
			else if (isFunctionLocalVar){
				offset = entry.type.offset;
			}

		}

		//check if there is a parameter in function which is a class field.
		this.isClassFieldInFunc = this.IsInFunctionScope && !this.isFunctionParam && !isFunctionLocalVar;
		if (className!=null){
			// Need to check for var as field in current class and fathers classes
			classType = SYMBOL_TABLE.getInstance().find(className);
			if(!classType.isClass()){
				System.out.format(">> ERROR [%d] \n",this.line);
				throw new MyRunTimeException(this.line);
			}
			insideClass = (TYPE_CLASS) classType;
			classType = insideClass.getInheritedFieldType(name);
			// we found name as field in classes.
			//we are inside class and this is a field - we need to load from object (after sp)

			if(classType != null) {
				varDec = (TYPE_VAR_DEC) classType;
				if (entry != null && entry.scopeCount == 0){
					this.offset = varDec.offset;
					return classType;
				}
				if (entry == null){
					this.offset = varDec.offset;
					return classType;
				}

			}
			// if t is null we will search inside symbol table for global vars
		}
		if ( entry == null)
		{
			System.out.println("Error from : AST_VAR_SIMPLE");
			System.out.format(">> ERROR [%d] variable %s is not defined\n",this.line, name);
			throw new MyRunTimeException(this.line);
		}

		else if((entry.type).isFunction()){
			System.out.println("Error from : AST_VAR_SIMPLE");
			System.out.format(">> ERROR [%d] variable %s is function - cannot be compared\n", this.line, name);
			throw new MyRunTimeException(this.line);
		}

		// checking reserved words
		if(AST_Node.isReservedWord(name)){
			System.out.format(">> ERROR [%d] cannot declar variable of reserved name %s\n", this.line, name);
			throw new MyRunTimeException(this.line);
		}
		if (entry.type.isArray()){
			arr = (TYPE_ARRAY) entry.type;
			if(entry.name.equals(arr.name)){
				System.out.format(">> ERROR [%d] %s\n",this.line, name);
				throw new MyRunTimeException(this.line);
			}
		}
		if (entry.type.isClass()){
			classInst = (TYPE_CLASS) entry.type;
			if(entry.name.equals(classInst.name)){
				System.out.format(">> ERROR [%d] %s\n",this.line, name);
				throw new MyRunTimeException(this.line);
			}
		}
		return entry.type;
	}

	public TEMP IRme()
	{
		TEMP temp = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP object = TEMP_FACTORY.getInstance().getFreshTEMP();

		if (isGlobal){
			IR.getInstance().Add_IRcommand(new IRcommand_LoadGlobalVar(	temp ,this.offset, this.isRight));

			return temp;
		}

		else if (this.IsInFunctionScope){

			//we need to treat differently a variable in function that is a class field (since its not local or parameter)
			if (isClassFieldInFunc){
				IR.getInstance().Add_IRcommand(new IRcommand_LoadFieldAddress(temp,object,(this.offset+1)*4, this.isRight));
			}
			else{
				boolean isParamFromStack = this.IsInFunctionScope && this.isFunctionParam;
				//v := v - this is the right side of the assignment - we want to load the value of v from memory
				IR.getInstance().Add_IRcommand(new IRcommand_LoadLocalVarAddress(temp,this.offset, this.isRight,isParamFromStack));
			}

		}
		return temp;
	}
}
