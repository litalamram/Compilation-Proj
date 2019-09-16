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


public class IRcommand_Increase_FramePointer extends IRcommand
{
	public int offset;
	
	public IRcommand_Increase_FramePointer(int offset)
	{
		this.offset = offset;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{

		sir_MIPS_a_lot.getInstance().addiToFp(this.offset);		
	}
}
