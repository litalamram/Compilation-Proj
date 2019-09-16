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

public class IRcommand_StoreToHeap extends IRcommand
{
	TEMP arrayAddr;
	TEMP value;
	
	public IRcommand_StoreToHeap(TEMP arrayAddr,TEMP value)
	{
		this.value = value;
		this.arrayAddr = arrayAddr;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().storeToHeap(value, arrayAddr);
	}
}
