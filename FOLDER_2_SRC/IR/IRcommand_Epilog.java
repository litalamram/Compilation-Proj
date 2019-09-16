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

public class IRcommand_Epilog extends IRcommand
{
	public TEMP returnedValue;
	public int offsetToFrame;
	public int stackPointerOffset;
	
	public IRcommand_Epilog(TEMP returnedValue, int offsetToFrame, int stackPointerOffset)
	{
		this.returnedValue = returnedValue;
		this.offsetToFrame = offsetToFrame;
		this.stackPointerOffset = stackPointerOffset;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		// move frame pointer back to this frame
		sir_MIPS_a_lot.getInstance().addiToFp(this.offsetToFrame);
		// move stack pointer back tp frame pointer
		sir_MIPS_a_lot.getInstance().addiToSp(this.stackPointerOffset);	
		// restoring registers values - OR - changed to load back to temps
		sir_MIPS_a_lot.getInstance().LoadMachineRegistersFromFrame();
		sir_MIPS_a_lot.getInstance().LoadRegisterFromFrame("$ra",-32);
		//move back sp
		sir_MIPS_a_lot.getInstance().addiToSp(4);	
		// getting the returned value of the function	
		sir_MIPS_a_lot.getInstance().loadFromFrame(returnedValue,-36);
		// for register allocation
		sir_MIPS_a_lot.getInstance().move(returnedValue,returnedValue);
		// returnedValue contain the returened value of the function
		//load old frame pointer possition to current fp
		sir_MIPS_a_lot.getInstance().loadOldFpToStack("$fp",0);
	}
}
