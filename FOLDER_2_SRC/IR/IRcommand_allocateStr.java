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

public class IRcommand_allocateStr extends IRcommand
{
	TEMP taddr;
	String val;
	
	public IRcommand_allocateStr(TEMP taddr,String val)
	{
		this.taddr= taddr;
		this.val=val;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	/*
     * ASSEMBLY CODE :
     * li $a0 size
     * li $v0 9 //9 is for allocation syscall
     * syscall
     * addi $result $v0 0 //moving the adress from v0 to the result
     *
     */
	public void MIPSme()
	{
		int i = 0;
		int c;
		int stringLen = val.length() - 1;
		sir_MIPS_a_lot.getInstance().allocateStr(taddr, stringLen);
		TEMP strAddr = TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().move(strAddr, taddr);
		TEMP t0 = TEMP_FACTORY.getInstance().getFreshTEMP();
		for(i=1; i<val.length()-1; i++)
		{
			//get char
			c = val.charAt(i);
			// copy c to t0
			sir_MIPS_a_lot.getInstance().li(t0, c);
			// store to dst
			sir_MIPS_a_lot.getInstance().sb(t0,strAddr,0);
			// go to the next byte
			sir_MIPS_a_lot.getInstance().addi(strAddr,1);
		}
		sir_MIPS_a_lot.getInstance().li(t0, 0);
		sir_MIPS_a_lot.getInstance().sb(t0,strAddr,0);
        // In result we have the relevant str address!!
	}
}
