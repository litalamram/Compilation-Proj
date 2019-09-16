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

public class IRcommand_LoadRegistersFromFrame extends IRcommand
{

	
	public IRcommand_LoadRegistersFromFrame()
	{

	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().LoadMachineRegistersFromFrame();
		sir_MIPS_a_lot.getInstance().LoadRegisterFromFrame("$ra",-32);
	}
}
