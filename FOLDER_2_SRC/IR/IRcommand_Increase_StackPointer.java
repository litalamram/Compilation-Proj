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

public class IRcommand_Increase_StackPointer extends IRcommand
{
	public int offset;
	
	public IRcommand_Increase_StackPointer(int offset)
	{
		this.offset = offset;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{

		sir_MIPS_a_lot.getInstance().addiToSp(this.offset);		
	}
}
