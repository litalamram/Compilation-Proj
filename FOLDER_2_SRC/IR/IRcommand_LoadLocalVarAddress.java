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

//dont use it 

public class IRcommand_LoadLocalVarAddress extends IRcommand
{
	int localVarOffset;
	boolean isRight;
	boolean isParamFromStack;
	TEMP dst;
	
	public IRcommand_LoadLocalVarAddress(TEMP dst,int localVarOffset,boolean isRight,boolean isParamFromStack)
	{
		this.localVarOffset = localVarOffset;
		this.isRight = isRight;
		this.dst = dst;
		this.isParamFromStack = isParamFromStack;
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		
		TEMP varAddress = null;
		//v := v - this is the right side of the assignment - we want to load the value of v from memory
		if (isRight){
			if(isParamFromStack){
				// we want value from stack
				sir_MIPS_a_lot.getInstance().loadFromFrame(dst,(this.localVarOffset+1)*4);
			}
			//need to be modified
			else{
				sir_MIPS_a_lot.getInstance().loadFromFrame(dst,-(this.localVarOffset+1)*4);
			}
		}
		//left side - we want to get variable address into temp t 
		else{
			//variable is a parameter of a function
			if (isParamFromStack){
				varAddress = sir_MIPS_a_lot.getInstance().addressParamVar(this.localVarOffset);
			}
			//for now it represents local variable but need to be changed.... (data members, globals...) 
			else{
				varAddress = sir_MIPS_a_lot.getInstance().addressLocalVar(this.localVarOffset);
			}
			sir_MIPS_a_lot.getInstance().move(dst,varAddress);
		}
	}
}
