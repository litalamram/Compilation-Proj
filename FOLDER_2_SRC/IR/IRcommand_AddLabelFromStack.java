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

public class IRcommand_AddLabelFromStack extends IRcommand
{
	boolean isLabelStack;
	
	public IRcommand_AddLabelFromStack(boolean isLabelStack)
	{
		this.isLabelStack = isLabelStack;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().AddLabelFromStack(this.isLabelStack);
		
	}
}
