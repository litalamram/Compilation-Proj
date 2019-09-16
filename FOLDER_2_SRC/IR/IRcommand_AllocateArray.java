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

public class IRcommand_AllocateArray extends IRcommand
{
	TEMP argumentsNum;
	TEMP arrayAddress;
	boolean isClassArray;
	
	public IRcommand_AllocateArray(TEMP arrayAddress,TEMP argumentsNum, boolean isClassArray)
	{
		this.argumentsNum = argumentsNum;
		this.arrayAddress = arrayAddress;
		this.isClassArray = isClassArray;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP tempByte = TEMP_FACTORY.getInstance().getFreshTEMP();
		TEMP arraySize = TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().li(tempByte,4);
		sir_MIPS_a_lot.getInstance().addi(argumentsNum, 1);
		sir_MIPS_a_lot.getInstance().mult(arraySize,argumentsNum ,tempByte);
		sir_MIPS_a_lot.getInstance().addi(argumentsNum, -1);
		sir_MIPS_a_lot.getInstance().allocateArray(arrayAddress, arraySize, argumentsNum);
		if(isClassArray){
			String nullStartLabel = getFreshLabel("start_null");
			String nullEndLabel = getFreshLabel("end_null");
			TEMP arrayStore = TEMP_FACTORY.getInstance().getFreshTEMP();
			sir_MIPS_a_lot.getInstance().add(arrayStore, arrayAddress,arraySize);
			sir_MIPS_a_lot.getInstance().addi(arrayStore, -4);
			sir_MIPS_a_lot.getInstance().addi(arraySize, -4);
			sir_MIPS_a_lot.getInstance().label(nullStartLabel);
			sir_MIPS_a_lot.getInstance().blez(arraySize, nullEndLabel);
			sir_MIPS_a_lot.getInstance().storeZero(arrayStore);
			sir_MIPS_a_lot.getInstance().addi(arrayStore, -4);
			sir_MIPS_a_lot.getInstance().addi(arraySize, -4);
			sir_MIPS_a_lot.getInstance().jump(nullStartLabel);
			sir_MIPS_a_lot.getInstance().label(nullEndLabel);
		}
	}
}
