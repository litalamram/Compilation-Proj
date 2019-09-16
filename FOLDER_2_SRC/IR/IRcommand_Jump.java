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

public class IRcommand_Jump extends IRcommand
{
	String destName;
	boolean isRegisterJump;
	
	public IRcommand_Jump(String destName,boolean isRegisterJump)
	{
		this.destName = destName;
		this.isRegisterJump = isRegisterJump;
	}

	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		if(isRegisterJump){
			sir_MIPS_a_lot.getInstance().jumpRegister(this.destName);
		}
		else{
			sir_MIPS_a_lot.getInstance().jump(this.destName);	
		}
		
	
	}
}
