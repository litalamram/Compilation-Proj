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

public class IRcommand_CallFunction extends IRcommand
{
	public String funcName;
	
	public IRcommand_CallFunction(String funcName)
	{
		this.funcName = funcName;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().callFunction(this.funcName);		
	}
}
