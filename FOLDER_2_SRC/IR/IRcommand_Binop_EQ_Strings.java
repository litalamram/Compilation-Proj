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

public class IRcommand_Binop_EQ_Strings extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;

	public IRcommand_Binop_EQ_Strings(TEMP dst,TEMP t1,TEMP t2)
	{
		this.dst = dst;
		this.t1 = t1; //str1
		this.t2 = t2; //str2 
	}
	
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		/*******************************/
		/* [1] Allocate 2 fresh labels */
		/*******************************/
		String label_end = getFreshLabel("end");
		String label_set_to_zero_string2 = getFreshLabel("set_to_zero_string2"); //str2 is EOF
		String label_set_to_zero_string1 = getFreshLabel("set_to_zero_string1"); //str1 is EOF
		String label_set_to_zero = getFreshLabel("set_to_zero"); //str1 is EOF
		String label_check_if_equal = getFreshLabel("check_if_equal");
		String label_set_to_one = getFreshLabel("set_to_one");
		
		/******************************************/
		/* [2] get str1 and str2 copies  */
		/******************************************/
		TEMP copy_of_str1 = TEMP_FACTORY.getInstance().getFreshTEMP(); 		//copy_of_str1 = dup the str1 so we wont change the old one
		sir_MIPS_a_lot.getInstance().addi_fromTempToTemp(copy_of_str1,t1,0); 	//copy_of_str1 = t1
		TEMP copy_of_str2 = TEMP_FACTORY.getInstance().getFreshTEMP(); 		//copy_of_str2 = dup the str2 so we wont change the old one
		sir_MIPS_a_lot.getInstance().addi_fromTempToTemp(copy_of_str2,t2,0); 	//copy_of_str2 = t2
		

		/************************/
		/* [3] label_check_if_equal: 	*/
		/*                      */
		/*   check for equality */
		/*         goto end;    */
		/*                      */
		/************************/
		sir_MIPS_a_lot.getInstance().label(label_check_if_equal);
		TEMP char_from_str1 = TEMP_FACTORY.getInstance().getFreshTEMP();			//read one char from str1
		sir_MIPS_a_lot.getInstance().lb(char_from_str1,copy_of_str1,0); 			//read one byte from str1 temp_for_str1=mem[$copy_of_str+1offset]
		TEMP char_from_str2 = TEMP_FACTORY.getInstance().getFreshTEMP();			//read one char from str2
		sir_MIPS_a_lot.getInstance().lb(char_from_str2,copy_of_str2,0); 			//read one byte from str2 temp_for_str2=mem[$copy_of_str2+offset]
		sir_MIPS_a_lot.getInstance().bne(char_from_str1,char_from_str2,label_set_to_one);	// if (t1 != t2) than jump to label_set_to_zero
		sir_MIPS_a_lot.getInstance().beq(char_from_str2,char_from_str2,label_set_to_zero_string2,true);	// if (t2 == EOF) than jump to label_set_to_zero_string2
		sir_MIPS_a_lot.getInstance().beq(char_from_str1,char_from_str1,label_set_to_zero_string1,true);	// if (t1 == EOF) than jump to label_set_to_zero_string1
		sir_MIPS_a_lot.getInstance().addi(copy_of_str1,1);						// string t2= string t2+1
		sir_MIPS_a_lot.getInstance().addi(copy_of_str2,1);						// string t2= string t1+1
		sir_MIPS_a_lot.getInstance().jump(label_check_if_equal); 						//jump to the begining of the loop


		/*************************/
		/* [4] label_set_to_one:  */
		/*                        */
		/*         dst := 0       */
		/*         goto end;      */
		/*                        */
		/*************************/
		sir_MIPS_a_lot.getInstance().label(label_set_to_one);
		sir_MIPS_a_lot.getInstance().li(dst,1); 							// dst = 1 since str1 and str2 are not equals
		sir_MIPS_a_lot.getInstance().jump(label_end);

		/*********************************/
		/* [5] label_set_to_zero_string2: 		*/
		/*                       		*/
		/*         if str1 := 0 dst :=1  */
		/*         else dst := 0;        */
		/* 			goto end;     		*/
		/*      				            */
		/*********************************/
		sir_MIPS_a_lot.getInstance().label(label_set_to_zero_string2);
		sir_MIPS_a_lot.getInstance().beq(char_from_str1,char_from_str1,label_set_to_zero, true);
		sir_MIPS_a_lot.getInstance().li(dst,1);
		sir_MIPS_a_lot.getInstance().jump(label_end);
		
		/*********************************/
		/* [6] label_set_to_zero_string1: 	*/
		/*                       		*/
		/*         if str2 := 0 dst := 1 */
		/*         else dst := 0;        */
		/* 			goto end;     		*/
		/*      				            */
		/*********************************/
		sir_MIPS_a_lot.getInstance().label(label_set_to_zero_string1);
		sir_MIPS_a_lot.getInstance().beq(char_from_str2,char_from_str2,label_set_to_one, true);
		sir_MIPS_a_lot.getInstance().li(dst,1);
		sir_MIPS_a_lot.getInstance().jump(label_end);
		
		
		/*************************/
		/* [7] label_set_to_zero: */
		/*                       */
		/*         dst := 1      */
		/*         goto end;     */
		/*                       */
		/*************************/
		sir_MIPS_a_lot.getInstance().label(label_set_to_zero); //if we got here str1 = str2
		sir_MIPS_a_lot.getInstance().li(dst,0);             	//dst = 0
		sir_MIPS_a_lot.getInstance().jump(label_end);	
		
		/******************/
		/* [8] label_end: */
		/******************/
		sir_MIPS_a_lot.getInstance().label(label_end);

	}
}
