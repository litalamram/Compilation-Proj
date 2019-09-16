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

public class IRcommand_StoreField extends IRcommand
{
	TEMP src;
	int offset;
	
	public IRcommand_StoreField(TEMP src,int offset)
	{
		this.src = src;
		this.offset = offset;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		TEMP objectAddress = TEMP_FACTORY.getInstance().getFreshTEMP();
		
		//name of function is not good but we want to load from stack the object ('this')
		sir_MIPS_a_lot.getInstance().loadFromStack(objectAddress, -4);
		
		sir_MIPS_a_lot.getInstance().store(objectAddress, src, offset);
	}
}
