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

public class IRcommand_AddLabel extends IRcommand
{
	boolean isLabelStack;
	String labelName;
	
	public IRcommand_AddLabel(String labelName, boolean isLabelStack)
	{
		this.isLabelStack = isLabelStack;
		this.labelName = labelName;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		String newLabel = getFreshLabel(this.labelName);	
		sir_MIPS_a_lot.getInstance().label(newLabel);
		sir_MIPS_a_lot.getInstance().addLabelToStack(newLabel,isLabelStack);
	}
}
