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

public class IRcommand_LoadRegisterToTemp extends IRcommand
{
	TEMP dst;
	String register;
	
	public IRcommand_LoadRegisterToTemp(TEMP dst,String register)
	{
		this.dst = dst;
		this.register = register;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		sir_MIPS_a_lot.getInstance().moveFromRegister(dst,register);
	}
}
