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


public class IRcommand_LoadLocalStringVar extends IRcommand
{
	int localVarOffset;
	boolean isRight;
	TEMP dst;
	TEMP varValue;
	TEMP stringIndex;
	
	public IRcommand_LoadLocalStringVar (TEMP dst,TEMP varValue,TEMP stringIndex,
			int localVarOffset,boolean isRight)
	{
		this.localVarOffset = localVarOffset;
		this.isRight = isRight;
		this.dst = dst;
		this.varValue = varValue;
		this.stringIndex = stringIndex;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{		
		//TEMP varAddress = sir_MIPS_a_lot.getInstance().addressLocalVar(this.localVarOffset);
		TEMP varAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP stringByte = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP wordSize = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP stringSize = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		sir_MIPS_a_lot.getInstance().li(wordSize, 4);
		//check if trying to get to illigal index - get array address on heap
		sir_MIPS_a_lot.getInstance().loadFromMemory(stringSize, varValue);
		if (!isRight){
			//get value of first cell
			sir_MIPS_a_lot.getInstance().loadFromMemory(stringSize, stringSize);
		}
		//v := v - this is the right side of the assignment - we want to load the value of v from memory
		if (isRight){
			// varValue contains the base address of the string 
			TEMP stringArgAddr = TEMP_FACTORY.getInstance().getFreshTEMP();
			sir_MIPS_a_lot.getInstance().add(stringArgAddr,varValue, stringByte);
			sir_MIPS_a_lot.getInstance().loadFromMemory(dst, stringArgAddr);
		}
		//left side - we want to get variable address into dst
		else {
			// varValue contains a memory address. 
			//This address contains the base address of the string.
			TEMP stringBaseAddr = TEMP_FACTORY.getInstance().getFreshTEMP();
			sir_MIPS_a_lot.getInstance().loadFromMemory(stringBaseAddr, varValue);			
			sir_MIPS_a_lot.getInstance().add(dst, stringBaseAddr, stringByte);
		}
	}
}
