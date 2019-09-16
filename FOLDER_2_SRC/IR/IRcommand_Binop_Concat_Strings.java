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

public class IRcommand_Binop_Concat_Strings extends IRcommand
{
	public TEMP t1;
	public TEMP t2;
	public TEMP dst;
	
	public IRcommand_Binop_Concat_Strings(TEMP dst,TEMP t1,TEMP t2)
	{
		this.dst = dst; 	//temp that contain the address
		this.t1 = t1; 	//string 1
		this.t2 = t2;	//string 2
	}
	/***************/
	/* MIPS me !!! */
	/***************/
	public void MIPSme()
	{
		/******************************************************/
		/* [0] Allocate a fresh temporary t4 for the addition */
		/******************************************************/
		/****************************************************/
		/* [1] Allocate a fresh labels */
		/****************************************************/
		String length_of_string_one 			= getFreshLabel("length_of_string_one");
		String length_of_string_two 			= getFreshLabel("length_of_string_two");
		String length_string_end 			= getFreshLabel("length_string_end");
		String length_string_end2 			= getFreshLabel("length_string_end_two");
		String string_copy_first 				= getFreshLabel("string_copy_first");
		String string_copy_second 				= getFreshLabel("string_copy_second");
		String string1_copy_end 				= getFreshLabel("string1_copy_end");
		String string2_copy_end 				= getFreshLabel("string2_copy_end");
		/******************************************************/
		/* [1.1]/
		/******************************************************/
		TEMP count = TEMP_FACTORY.getInstance().getFreshTEMP();			 	//count num of characters
		sir_MIPS_a_lot.getInstance().li(count,0); 							//count = 0 
		TEMP copy_of_str = TEMP_FACTORY.getInstance().getFreshTEMP(); 		//copy_of_str = dup the str so we wont change the old one
		sir_MIPS_a_lot.getInstance().addi_fromTempToTemp(copy_of_str,t1,0); 	//copy_of_str = t1
		
		/***********************************************/
		/* [2.1] First String - length_of_string_one labels */
		/*********************************************/
		sir_MIPS_a_lot.getInstance().label(length_of_string_one);
		TEMP char_from_str = TEMP_FACTORY.getInstance().getFreshTEMP();						//read one char from str
		sir_MIPS_a_lot.getInstance().lb(char_from_str,copy_of_str,0); 						//read one byte from str1 temp_for_str1=mem[$copy_of_str+offset]
		sir_MIPS_a_lot.getInstance().beq(char_from_str,char_from_str,length_string_end,true);	// if (t1 = zero) than jump to length_string_end
		sir_MIPS_a_lot.getInstance().addi(count,1);    
		sir_MIPS_a_lot.getInstance().addi(copy_of_str,1) 	;									//copy_of_str = copy_of_str+1 
		sir_MIPS_a_lot.getInstance().jump(length_of_string_one);


		/**************************************************/
		/* [2.2] First String - length_of_string_one labels End*/
		/*************************************************/
		// lenghtOfStringEnd label
		sir_MIPS_a_lot.getInstance().label(length_string_end);
		
		
		TEMP length_of_string1 = TEMP_FACTORY.getInstance().getFreshTEMP(); //len of str1
		sir_MIPS_a_lot.getInstance().addi_fromTempToTemp(length_of_string1,count,0); //length_of_string1 = count
		
		// ------> find length of second string <-----
		sir_MIPS_a_lot.getInstance().li(count,0);						   //count = 0 
		sir_MIPS_a_lot.getInstance().addi_fromTempToTemp(copy_of_str,t2,0); //copy_of_str = t2
		
		
		/************************************************/
		/* [2.3] Second String - length_of_string_one labels */
		/**********************************************/
		sir_MIPS_a_lot.getInstance().label(length_of_string_two);
		TEMP char_from_str2 = TEMP_FACTORY.getInstance().getFreshTEMP();							//read one char from str
		sir_MIPS_a_lot.getInstance().lb(char_from_str2,copy_of_str,0); 							//read one byte from str1 temp_for_str1=mem[$copy_of_str+offset]
		sir_MIPS_a_lot.getInstance().beq(char_from_str2,char_from_str2,length_string_end2,true);	// if (t1 = zero) than jump to length_string_end
		sir_MIPS_a_lot.getInstance().addi(count,1);   
		sir_MIPS_a_lot.getInstance().addi(copy_of_str,1) 	;										//copy_of_str = copy_of_str+1 
		sir_MIPS_a_lot.getInstance().jump(length_of_string_two);


		/**************************************************/
		/* [2.2] Second String - length_of_string_one labels End*/
		/*************************************************/
		sir_MIPS_a_lot.getInstance().label(length_string_end2);
		TEMP length_of_string2 = TEMP_FACTORY.getInstance().getFreshTEMP();					//len of str2
		sir_MIPS_a_lot.getInstance().addi_fromTempToTemp(length_of_string2,count,0); 			//length_of_string2 = count

		//malloc on the heap
		TEMP final_length = TEMP_FACTORY.getInstance().getFreshTEMP();						// final string length
		sir_MIPS_a_lot.getInstance().add(final_length,length_of_string1,length_of_string2);
		sir_MIPS_a_lot.getInstance().addi(final_length,1); 									// the str final size 
		sir_MIPS_a_lot.getInstance().allocateStrTemp(dst,final_length);						//dst = new allocate address for the new string
		TEMP save_addr =  TEMP_FACTORY.getInstance().getFreshTEMP();							//save the addr of the string
		sir_MIPS_a_lot.getInstance().addi_fromTempToTemp(save_addr,dst,0); 					//length_of_string1 = count- replace with mv?		
		/****************************************************/
		/* [2.1] string_copy_first labels */
		/****************************************************/
		sir_MIPS_a_lot.getInstance().label(string_copy_first);
		TEMP char_from_str3 = TEMP_FACTORY.getInstance().getFreshTEMP();
		sir_MIPS_a_lot.getInstance().lb(char_from_str3,t1,0); 							//copy_from_str = char from origin t1
		sir_MIPS_a_lot.getInstance().beq(char_from_str3,char_from_str3,string1_copy_end,true); 	//is the char is 0 so we are done coping
		sir_MIPS_a_lot.getInstance().sb(char_from_str3,dst,0);							 //move char to the current address
		sir_MIPS_a_lot.getInstance().addi(t1,1);											//$t1=$t1+1
		sir_MIPS_a_lot.getInstance().addi(dst,1);										//$t1=$t1+1
		sir_MIPS_a_lot.getInstance().jump(string_copy_first);
		

		/****************************************************/
		/* [2.1] sCopyEndlabels */
		/****************************************************/
		sir_MIPS_a_lot.getInstance().label(string1_copy_end);

		/****************************************************/
		/* [2.2] string_copy_second label */
		/****************************************************/
		sir_MIPS_a_lot.getInstance().label(string_copy_second);
		sir_MIPS_a_lot.getInstance().lb(char_from_str3,t2,0);								 //copy_from_str = char from origin t2
		sir_MIPS_a_lot.getInstance().beq(char_from_str3,char_from_str3,string2_copy_end,true); 	//is the char is 0 so we are done coping
		sir_MIPS_a_lot.getInstance().sb(char_from_str3,dst,0);							//move char to the current address
		sir_MIPS_a_lot.getInstance().addi(t2,1);											// string t2= string t2+1
		sir_MIPS_a_lot.getInstance().addi(dst,1);
		sir_MIPS_a_lot.getInstance().jump(string_copy_second);
		
		/****************************************************/
		/* [2.2] string2_copy_end label */
		/****************************************************/
		sir_MIPS_a_lot.getInstance().label(string2_copy_end);
		TEMP tempZero = TEMP_FACTORY.getInstance().getFreshTEMP(); 						//str address
		sir_MIPS_a_lot.getInstance().li(tempZero, 0);
		sir_MIPS_a_lot.getInstance().sb(tempZero,dst,0); 									//terminate string 	- nned to put zero!	
		sir_MIPS_a_lot.getInstance().move(dst,save_addr);									//dst = save_addr
	}
}
