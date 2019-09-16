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

public class IRcommand_ExitProgram extends IRcommand
{
	
	public IRcommand_ExitProgram()
	{
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{

		sir_MIPS_a_lot.getInstance().exitProgram();		
	}
}
