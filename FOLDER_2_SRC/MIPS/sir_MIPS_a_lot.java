/***********/
/* PACKAGE */
/***********/
package MIPS;


import java.util.*;
/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;

/*******************/
/* PROJECT IMPORTS */
/*******************/
import TEMP.*;

public class sir_MIPS_a_lot
{
	
	private int WORD_SIZE=4;
	/***********************/
	/* The file writer ... */
	/***********************/
	public static int globalsNum = 0;
	public static String outputFileName; 
	public static String vtables = "";
	public static String funcNames = "string_func_null: .asciiz \"\"\n";
	private PrintWriter fileWriter;
	private Stack<String> labelsStack = new Stack<String>();
	private Stack<String> gotoStack = new Stack<String>();
	public ControlFlowGraph controlGraph;
	/***********************/
	/* The file writer ... */
	/***********************/
	public void finalizeFile()
	{
		jump("Label_0_main");
		label("Label_0_exit");
		fileWriter.print("\tli $v0,10\n");
		fileWriter.print("\tsyscall\n");
		fileWriter.close();
		controlGraph.createControlGraph();
		controlGraph.calcLiveness();
		InterferenceGraph interGraph = new InterferenceGraph(TEMP_FACTORY.getInstance().getTempCount());
		interGraph.AllocateRegisters(controlGraph);
		interGraph.replaceAllTemps();
	}
	
	public void syscallPrint()
	{
		fileWriter.print("\tsyscall\n");
	}

	public void print_int(TEMP t)
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\tmove $a0,Temp_%d\n",idx);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
			false, false, null,-1, idx, -1));
		ControlFlowGraph.serialNumber++;
		fileWriter.format("\tli $v0,1\n");
		fileWriter.format("\tsyscall\n");
		// print white space ....
		fileWriter.format("\tli $a0,32\n");
		fileWriter.format("\tli $v0,11\n");
		fileWriter.format("\tsyscall\n");
	}
	
	public void print_string(TEMP t) //t will contain the adrress in the heap
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\tmove $a0,Temp_%d\n",idx);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,-1, idx, -1));
		ControlFlowGraph.serialNumber++;
		fileWriter.format("\tli $v0,4\n");
		fileWriter.format("\tsyscall\n");
	}
	
	public void printRuntimeErrorMsg(String s){
		fileWriter.format("\tla $a0,%s\n",s);
		fileWriter.format("\tli $v0,4\n");
		fileWriter.format("\tsyscall\n");
	}
	
	public void exitProgram(){
		fileWriter.format("\tli $v0,10\n");
		fileWriter.format("\tsyscall\n");
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				true, false, null,-1, -1, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public TEMP addressLocalVar(int serialLocalVarNum)
	{
		TEMP t  = TEMP_FACTORY.getInstance().getFreshTEMP();
		int idx = t.getSerialNumber();

		fileWriter.format("\taddi Temp_%d,$fp,%d\n",idx,-(serialLocalVarNum+1)*WORD_SIZE);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,idx, -1, -1));
		ControlFlowGraph.serialNumber++;
		
		return t;
	}
	
	public void addLabelToStack(String label, boolean isLabelStack){
		if(isLabelStack)
			labelsStack.push(label);
		else gotoStack.push(label);
	}
	public String popLabelFromStack(boolean isLabelStack){
		if(isLabelStack && !labelsStack.empty())
			return labelsStack.pop();
		else if(!gotoStack.empty())
			return gotoStack.pop();
		return null;
	}
	
	public void AddLabelFromStack(boolean isLabelStack){
		String label1 = popLabelFromStack(isLabelStack);
		if(isLabelStack) label(label1);
		else jump(label1);
	}
	
	public TEMP addressParamVar(int serialParamVarNum)
	{
		TEMP t  = TEMP_FACTORY.getInstance().getFreshTEMP();
		int idx = t.getSerialNumber();

		fileWriter.format("\taddi Temp_%d,$fp,%d\n",idx,(serialParamVarNum+1)*WORD_SIZE);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,idx, -1, -1));
		ControlFlowGraph.serialNumber++;
		return t;
	}
	
	public void load(TEMP destination,TEMP source, int offset)
	{
		int index_dst = destination.getSerialNumber();
		int index_src = source.getSerialNumber();
		fileWriter.format("\tlw Temp_%d,%d(Temp_%d)\n", index_dst, offset, index_src);	
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,index_dst, index_src, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void loadGlobal(TEMP destination,TEMP address, int offset)
	{
		int index_dst = destination.getSerialNumber();
		int index_adr = address.getSerialNumber();
		
		loadGlobalAddress(address, offset);
		
		fileWriter.format("\tlw Temp_%d,0(Temp_%d)\n", index_dst, index_adr);	
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,index_dst, index_adr, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void loadGlobalAddress(TEMP destination, int offset)
	{
		int index_dst = destination.getSerialNumber();
		fileWriter.format("\tla Temp_%d,globals_array\n",index_dst);
		
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,index_dst, -1, -1));
		ControlFlowGraph.serialNumber++;
		// this needs to be here after the graph...
		addi(destination, offset);
	}
	
	public void loadFromFrame(TEMP destination, int offset)
	{
		int index_dst = destination.getSerialNumber();
		fileWriter.format("\tlw Temp_%d,%d($fp)\n",index_dst,offset);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,index_dst, -1, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void loadFromMemory(TEMP destination, TEMP address)
	{
		int index_dst = destination.getSerialNumber();
		int index_adr = address.getSerialNumber();
		fileWriter.format("\tlw Temp_%d,0(Temp_%d)\n", index_dst, index_adr);	
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null, index_dst, index_adr, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void storeToHeap(TEMP source,TEMP address)
	{
		int index_src = source.getSerialNumber(); 	//temp to insert
		int index_adr = address.getSerialNumber(); 	//temp to store 
		fileWriter.format("\tsw Temp_%d,0(Temp_%d)\n", index_src, index_adr);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,-1, index_src, index_adr));
		ControlFlowGraph.serialNumber++;
	}
	
	public void store(TEMP destination,TEMP source, int offset)
	{
		int index_dst = destination.getSerialNumber();
		int index_src = source.getSerialNumber();
		fileWriter.format("\tsw Temp_%d,%d(Temp_%d)\n",index_src, offset, index_dst);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,-1, index_dst, index_src));
		ControlFlowGraph.serialNumber++;
	}
	
	public void storeZero(TEMP destination)
	{
		int index_dst = destination.getSerialNumber();
		fileWriter.format("\tsw $zero,0(Temp_%d)\n", index_dst);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,-1, index_dst, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void storeToFrame(TEMP source, int offset)
	{
		int index_src = source.getSerialNumber();
		fileWriter.format("\tsw Temp_%d,%d($fp)\n", index_src, offset);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,-1, index_src, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void storeGlobal(TEMP globalsAddress, TEMP source, int offset)
	{
		int index_src = source.getSerialNumber();
		int index_adr = globalsAddress.getSerialNumber();
		
		loadGlobalAddress(globalsAddress, 0);
		fileWriter.format("\tsw Temp_%d,%d(Temp_%d)\n", index_src, offset, index_adr);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,-1, index_adr, index_src));
		ControlFlowGraph.serialNumber++;
	}
	
	public void storeToStack(TEMP source, int offset)
	{
		int index_src = source.getSerialNumber();
		fileWriter.format("\tsw Temp_%d,%d($sp)\n", index_src, offset);	
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,-1, index_src, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void loadFromStack(TEMP source, int offset)
	{
		int index_src = source.getSerialNumber();
		fileWriter.format("\tlw Temp_%d,%d($sp)\n", index_src, offset);	
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null, index_src, -1, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void storeOldFpToStack(String source, int offset)
	{
		fileWriter.format("\tsw %s,%d($sp)\n", source, offset);
	}
	
	public void loadOldFpToStack(String source, int offset)
	{
		fileWriter.format("\tlw %s,%d($sp)\n", source, offset);	
	}
	
	public void storeRegisterToFrame(String register, int offset)
	{
		fileWriter.format("\tsw %s,%d($fp)\n", register, offset);	
	}
	
	public void storeMachineRegistersToFrame()
	{
		// store all registers to stack
		int offset;
		for(int i=0; i<8; i++){
			offset = i*WORD_SIZE;
			fileWriter.format("\tsw $t%d,%d($fp)\n",i,-offset);
		}
				
	}
	
	public void allocateArray(TEMP arrayAddress,TEMP arraySize, TEMP argsNum){
		// Allocate array
		moveToRegister("$a0", arraySize);
		fileWriter.print("\tli $v0,9\n");
		fileWriter.print("\tsyscall\n");
		// save address to register arrayAddress
		moveFromRegister(arrayAddress,"$v0");
		//save array size in first cell
		store(arrayAddress, argsNum,0);
	}
	
	public void initArray(String exit,TEMP destination, TEMP arrayAddress,TEMP arraySize, TEMP index){
		//init array with zeros
		sub(destination, arraySize, index);
		blez(destination, exit);
		add(destination,arrayAddress, index);
		storeZero(destination);
		addi(index, 4);
	}
	
	public void allocateStr(TEMP strAddress,int strSize){
		// Allocate str
		fileWriter.format("\tli $a0,%d\n",strSize);	//$a0=str size
		fileWriter.print("\tli $v0,9\n");			//$v0 = 9
		fileWriter.print("\tsyscall\n"); 			//print syscall
		// save address to register strAddress
		moveFromRegister(strAddress,"$v0"); 			//strAdress = $v0
	}
	
	public void allocateStrTemp(TEMP strAddress,TEMP strSize){
		// Allocate str
		int i1=strSize.getSerialNumber();
		fileWriter.format("\tmove $a0,Temp_%d\n",i1);	//$a0=str size
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,-1, i1, -1));
		ControlFlowGraph.serialNumber++;
		fileWriter.print("\tli $v0,9\n");				//$v0 = 9
		fileWriter.print("\tsyscall\n"); 				//print syscall
		// save address to register strAddress
		moveFromRegister(strAddress,"$v0"); 				//strAdress = $v0
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,-1, i1, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void LoadMachineRegistersFromFrame()
	{
		// store all registers to stack
		int offset;
		for(int i=0; i<8; i++){
			offset = i*WORD_SIZE;
			fileWriter.format("\tlw $t%d,%d($fp)\n",i,-offset);
		}
				
	}
	
	public void LoadRegisterFromFrame(String register, int offset)
	{
		fileWriter.format("\tlw %s,%d($fp)\n",register,offset);		
	}	
	
	public void move(TEMP dst,TEMP src)
	{
		int idxdst=dst.getSerialNumber();
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tmove Temp_%d,Temp_%d\n",idxdst,idxsrc);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,idxdst, idxsrc, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void moveRegisters(String dst,String src)
	{
		fileWriter.format("\tmove %s,%s\n",dst,src);		
	}
	
	public void moveToRegister(String dst,TEMP src)
	{
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tmove %s,Temp_%d\n",dst,idxsrc);	
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,-1, idxsrc, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void moveFromRegister(TEMP dst,String src)
	{
		int idxdst=dst.getSerialNumber();
		fileWriter.format("\tmove Temp_%d,%s\n",idxdst,src);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,idxdst, -1, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void li(TEMP t,int value)
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\tli Temp_%d,%d\n",idx,value);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,idx, -1, -1));
		ControlFlowGraph.serialNumber++;
	}

	public void jr(String rt, boolean isTerminate)
	{
		fileWriter.format("\t%s %s%n", "jr", rt);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				isTerminate, false, null,-1, -1, -1));
		ControlFlowGraph.serialNumber++;
	}
	
	public void la(TEMP t,String str)
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\tla Temp_%d,%s\n",idx,str);
		// ---- control flow graph -----
		controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
				false, false, null,idx, -1, -1));
		ControlFlowGraph.serialNumber++;
	}
	//sb $t, offset($s) MEM[$s + offset] = (0xff & $t);- Anding an integer with 0xFF leaves only the least significant byte.
	 public void sb(TEMP rt, TEMP address,int offset) {
		 int i1=rt.getSerialNumber();
		 int i2=address.getSerialNumber();
		 fileWriter.format("\tsb Temp_%d,%d(Temp_%d)\n", i1,offset, i2);
		 // ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, false, null,-1, i1, i2));
		 ControlFlowGraph.serialNumber++;
	    }
	public void add(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tadd Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
		 // ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, false, null,dstidx, i1, i2));
		 ControlFlowGraph.serialNumber++;
	}
	
	public void sub(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tsub Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
		 // ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, false, null,dstidx, i1, i2));
		 ControlFlowGraph.serialNumber++;
	}
	
	public void addiToFp(int offset)
	{
		fileWriter.format("\taddi $fp,$fp,%d\n",offset);
	}
	
	public void addiToSp(int offset)
	{
		fileWriter.format("\taddi $sp,$sp,%d\n",offset);
	}
		
	public void addi(TEMP t,int num)
	{
		int i =t.getSerialNumber();
		fileWriter.format("\taddi Temp_%d,Temp_%d,%d\n",i,i,num);
		 // ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, false, null,i, i, -1));
		 ControlFlowGraph.serialNumber++;
	}
	
	public void addi_fromTempToTemp(TEMP t1,TEMP t2, int num) 
	{
		int i1 =t1.getSerialNumber();
		int i2 =t2.getSerialNumber();
		fileWriter.format("\taddi Temp_%d,Temp_%d,%d\n",i1,i2,num);
		 // ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, false, null,i1, i2, -1));
		 ControlFlowGraph.serialNumber++;
	}
	
	public void mult(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tmul Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
		 // ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, false, null,dstidx, i1, i2));
		 ControlFlowGraph.serialNumber++;
	}
	
	
	public void div(TEMP dst,TEMP oprnd1,TEMP oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tdiv Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
		 // ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, false, null,dstidx, i1, i2));
		 ControlFlowGraph.serialNumber++;
	}
	public void callFunction(String functionName)
	{
		fileWriter.format("\tjal %s\n",functionName);
		 // ---- control flow graph -----
		// No need 
	}
	public void jalr(TEMP t)
	{
		int i =t.getSerialNumber();
		fileWriter.format("\tjalr Temp_%d\n",i);
		 // ---- control flow graph -----
		// No need
	}
	public void label(String inlabel)
	{
		fileWriter.format("%s:\n",inlabel);
		// ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, true, inlabel,
					false, false, null,-1, -1, -1));
		 ControlFlowGraph.serialNumber++;
	}	
	public void jump(String inlabel)
	{
		fileWriter.format("\tj %s\n",inlabel);
		// ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, true, inlabel,-1, -1, -1));
		 ControlFlowGraph.serialNumber++;
	}
	public void jumpRegister(String inlabel)
	{
		fileWriter.format("\tjr %s\n",inlabel);
		// ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					true, false, null,-1, -1, -1));
		 ControlFlowGraph.serialNumber++;
	}	
	public void blt(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tblt Temp_%d,Temp_%d,%s\n",i1,i2,label);
		// ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, true, label,-1, i1, i2));
		 ControlFlowGraph.serialNumber++;
	}
	
	public void blez(TEMP res,String label)
	{
		int i1 =res.getSerialNumber();
		
		fileWriter.format("\tblez Temp_%d,%s\n",i1,label);
		// ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, true, label,-1, i1, -1));
		 ControlFlowGraph.serialNumber++;
	}
	
	public void bltz(TEMP res,String label)
	{
		int i1 =res.getSerialNumber();
		
		fileWriter.format("\tbltz Temp_%d,%s\n",i1,label);	
		// ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, true, label,-1, i1, -1));
		 ControlFlowGraph.serialNumber++;
	}
	
	public void bne(TEMP reg1,TEMP reg2, String label)
	{
		int i1 =reg1.getSerialNumber();
		int i2 =reg2.getSerialNumber();
		
		fileWriter.format("\tbne Temp_%d,Temp_%d,%s\n",i1,i2,label);
		// ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, true, label,-1, i1, i2));
		 ControlFlowGraph.serialNumber++;
	}
	
	public void beq(TEMP reg1,TEMP reg2,String label, boolean isZeroEqual)
	{
		int i1 =reg1.getSerialNumber();
		if(isZeroEqual){
			fileWriter.format("\tbeq Temp_%d,$zero,%s\n",i1,label);
			// ---- control flow graph -----
			 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
						false, true, label,-1, i1, -1));
			 ControlFlowGraph.serialNumber++;
		}
		else{
			int i2 =reg2.getSerialNumber();			
			fileWriter.format("\tbeq Temp_%d,Temp_%d,%s\n",i1,i2,label);
			// ---- control flow graph -----
			 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
						false, true, label,-1, i1, i2));
			 ControlFlowGraph.serialNumber++;
		}
				
	}
	
	public void bge(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbge Temp_%d,Temp_%d,%s\n",i1,i2,label);
		// ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, true, label,-1, i1, i2));
		 ControlFlowGraph.serialNumber++;
	}
	
	public void bgt(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbgt Temp_%d,Temp_%d,%s\n",i1,i2,label);
		// ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, true, label,-1, i1, i2));
		 ControlFlowGraph.serialNumber++;
	}
	
	public void ble(TEMP oprnd1,TEMP oprnd2,String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tble Temp_%d,Temp_%d,%s\n",i1,i2,label);
		// ---- control flow graph -----
		 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
					false, true, label,-1, i1, i2));
		 ControlFlowGraph.serialNumber++;
	}
	
	//$t = MEM[$s + offset]; advance_pc (4);  lb $t, offset($s)
	   public void lb(TEMP t, TEMP address,int offset) {
		   int i1 = t.getSerialNumber();
		   int i2 = address.getSerialNumber();
		   fileWriter.format("\tlb Temp_%d,%d(Temp_%d)\n", i1, offset, i2);
			// ---- control flow graph -----
			 controlGraph.addCommand(new CommandNode(ControlFlowGraph.serialNumber, false, null,
						false, false, null,i1, i2, -1));
			 ControlFlowGraph.serialNumber++;
	    }
	
	/**************************************/
	/* USUAL SINGLETONe IMPLEMENTATION ... */
	/**************************************/
	private static sir_MIPS_a_lot instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected sir_MIPS_a_lot() {
		this.controlGraph = new ControlFlowGraph();
	}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static sir_MIPS_a_lot getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new sir_MIPS_a_lot();

			try
			{
				/*********************************************************************************/
				/* [1] Open the MIPS text file and write data section with error message strings */
				/*********************************************************************************/
//				String dirname="./FOLDER_5_OUTPUT/";
//				String filename=String.format("MIPS.txt");

				/***************************************/
				/* [2] Open MIPS text file for writing */
				/***************************************/
				instance.fileWriter = new PrintWriter(outputFileName);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			/*****************************************************/
			/* [3] Print data section with error message strings */
			/*****************************************************/
			instance.fileWriter.print(".data\n");
			instance.fileWriter.format("globals_array: .space %d\n", globalsNum*4);
			instance.fileWriter.format(vtables);
			instance.fileWriter.print("string_access_violation: .asciiz \"Access Violation\"\n");
			instance.fileWriter.print("string_illegal_div_by_0: .asciiz \"Division By Zero\"\n");
			instance.fileWriter.print("string_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
			instance.fileWriter.format(funcNames);
			
			/************************************************/
			/* [4] Print text section with entry point main */
			/************************************************/
			instance.fileWriter.print(".text\n");
			instance.fileWriter.print("main:\n");

			/******************************************/
			/* [5] Will work with <= 10 variables ... */
			/******************************************/
			instance.fileWriter.print("\taddi $fp,$sp,0\n");
			instance.fileWriter.print("\tsw $zero,0($fp)\n");

		}
		return instance;
	}
}
