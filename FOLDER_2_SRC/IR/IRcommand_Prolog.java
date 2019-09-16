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

public class IRcommand_Prolog extends IRcommand
{
	public TEMP object;
	public int offsetToFrame;
	public int stackPointerOffset;
	public boolean isFunctionProlog;
	public boolean isConstructor;
	public String funcName;

	public IRcommand_Prolog(TEMP object, int offsetToFrame, int stackPointerOffset, boolean isFunctionProlog, boolean isConstructor, String funcName)
	{
		this.object = object;
		this.offsetToFrame = offsetToFrame;
		this.stackPointerOffset = stackPointerOffset;
		this.isFunctionProlog = isFunctionProlog;
		this.isConstructor = isConstructor;
		this.funcName = funcName;
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		//store frame pointer to current stuck pointer
		sir_MIPS_a_lot.getInstance().storeOldFpToStack("$fp",0);

		//save old frame pointer
		TEMP old_fp = TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().moveFromRegister(old_fp, "$fp");
		//save func name
		TEMP funcName = TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().la(funcName, "string_func_"+this.funcName);
		sir_MIPS_a_lot.getInstance().storeToFrame(funcName,-4);


		//move stack pointer to sp -4
		sir_MIPS_a_lot.getInstance().addiToSp(-4);	
		//move frame pointer to stack pointer - 4
		sir_MIPS_a_lot.getInstance().moveRegisters("$fp", "$sp");
		// save $ra and machine registers to stack
		sir_MIPS_a_lot.getInstance().storeMachineRegistersToFrame();
		sir_MIPS_a_lot.getInstance().storeRegisterToFrame("$ra",-32);
		//load object on stack (if passed - we want to pass it to called function too in case needed)
		if(isFunctionProlog)
			sir_MIPS_a_lot.getInstance().loadFromStack(object,8);
		// all params saved to frame
		// move frame pointer to base pointer
		//offsetToFrame +=4;
		sir_MIPS_a_lot.getInstance().addiToFp(-this.offsetToFrame);

		//save old frame pointer to current frame pointer
		sir_MIPS_a_lot.getInstance().storeToFrame(old_fp,0);

		//move stack pointer to end of current frame
		sir_MIPS_a_lot.getInstance().addiToSp(-stackPointerOffset);
		if (!isConstructor){
			//store object on stack (if passed)
			sir_MIPS_a_lot.getInstance().storeToStack(this.object, 4);	
		}
		else{
			sir_MIPS_a_lot.getInstance().storeToStack(this.object, -4);
		}

	}
}
