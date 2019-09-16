/***********/
/* PACKAGE */
/***********/
package IR;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;
import MIPS.*;


public class IRcommand_LoadLocalArrayVar extends IRcommand
{
	int localVarOffset;
	boolean isRight;
	TEMP dst;
	TEMP varValue;
	TEMP arrayIndex;
	boolean isString;
	
	public IRcommand_LoadLocalArrayVar(TEMP dst,TEMP varValue,TEMP arrayIndex,
			int localVarOffset,boolean isRight, boolean isString)
	{
		this.localVarOffset = localVarOffset;
		this.isRight = isRight;
		this.dst = dst;
		this.varValue = varValue;
		this.arrayIndex = arrayIndex;
		this.isString = isString;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		String label_illegal_array_index         = getFreshLabel("illegal_array_index");
		String label_legal_array_index         	= getFreshLabel("legal_array_index");
		
		//TEMP varAddress = sir_MIPS_a_lot.getInstance().addressLocalVar(this.localVarOffset);
		TEMP varAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP arrayByte = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP wordSize = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP arraySize = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		sir_MIPS_a_lot.getInstance().li(wordSize, 4);
		//check if trying to get to illigal index
		//get array address on heap
		sir_MIPS_a_lot.getInstance().loadFromMemory(arraySize, varValue);
		
		if (!isRight){
			//get value of first cell
			sir_MIPS_a_lot.getInstance().loadFromMemory(arraySize, arraySize);
		}
		//check if number of cell is bigger then array size
		sir_MIPS_a_lot.getInstance().bge(arrayIndex,arraySize,label_illegal_array_index);
		sir_MIPS_a_lot.getInstance().bltz(arrayIndex,label_illegal_array_index);
		//below will happen if legal index
		
		//change index since on first cell we keep array size
		sir_MIPS_a_lot.getInstance().addi(arrayIndex, 1);
		sir_MIPS_a_lot.getInstance().mult(arrayByte, arrayIndex, wordSize);
		sir_MIPS_a_lot.getInstance().jump(label_legal_array_index);
		
		//if illegal
		sir_MIPS_a_lot.getInstance().label(label_illegal_array_index);
		sir_MIPS_a_lot.getInstance().printRuntimeErrorMsg("string_access_violation");
		sir_MIPS_a_lot.getInstance().exitProgram();
		
		//legal array index
		sir_MIPS_a_lot.getInstance().label(label_legal_array_index);
		//v := v - this is the right side of the assignment - we want to load the value of v from memory
		if (isRight){
			// varValue contains the base address of the array 
			TEMP arrayArgAddr = TEMP_FACTORY.getInstance().getFreshTEMP();
			sir_MIPS_a_lot.getInstance().add(arrayArgAddr,varValue, arrayByte);
			sir_MIPS_a_lot.getInstance().loadFromMemory(dst, arrayArgAddr);
		}
		//left side - we want to get variable address into dst
		else{
			// varValue contains a memory address. 
			//This address contains the base address of the array
			TEMP arrayBaseAddr = TEMP_FACTORY.getInstance().getFreshTEMP();
			sir_MIPS_a_lot.getInstance().loadFromMemory(arrayBaseAddr, varValue);
			sir_MIPS_a_lot.getInstance().add(dst, arrayBaseAddr, arrayByte);
		}
	}
}
