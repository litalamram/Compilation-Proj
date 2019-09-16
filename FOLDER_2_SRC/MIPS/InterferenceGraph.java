/***********/
/* PACKAGE */
/***********/
package MIPS;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/
import java.util.*;
import java.io.*;
import TYPES.*;

public class InterferenceGraph
{
	/*****************/
	/* Class Fields  */
	/*****************/
	InterferenceNode[] interferGraph;
	int[] registerAllocation;
	public static String outputFileName;
	
	public InterferenceGraph(int tempsNum){
		this.interferGraph = new InterferenceNode[tempsNum];
		for(int i=0; i<tempsNum; i++){
			this.interferGraph[i] = new InterferenceNode(i);
		}
		registerAllocation = new int[tempsNum];
	}
	
	public void createInterferGraph(ControlFlowGraph controlFlowGraph){
		for(ArrayList<CommandNode> graph:controlFlowGraph.controlGraph)
			updateInterfereGraph(graph);
	}
	
	public void updateInterfereGraph(ArrayList<CommandNode> graph){
		for(CommandNode command:graph){
			for(Integer tempNum:command.liveIn){
				if(tempNum.compareTo(159) ==0){
					//System.out.format("Line in command %d: %s\n", command.commandSerialNum, command.liveIn.toString());
				}
				this.interferGraph[tempNum].addInterfere(command.liveIn);
			}
		}
	}

	public void coloringTheGraphush(){
		int regNum;
		for(int i=0; i<this.interferGraph.length; i++){
			this.interferGraph[i].refreshDeg();
			this.registerAllocation[i] = -1;
		}
		Arrays.sort(this.interferGraph);
		// Allocate registers
		for(int i=0; i<this.interferGraph.length; i++){
			regNum = findRegisterAllocation(interferGraph[i]);
			this.registerAllocation[interferGraph[i].tempNum]= regNum;
			this.interferGraph[i].regNum = regNum;
		}
		// create mapping TempNum-->RegisterNum
//		for(int i=0; i<this.interferGraph.length; i++){
//			this.registerAllocation[interferGraph[i].tempNum]= interferGraph[i].regNum;
//		}
	}
	public int findRegisterAllocation(InterferenceNode node){
		boolean foundRegNum;
		for(int i=0; i<8; i++){
			foundRegNum = true;
			// checking all the interfere temps of node 
			// try to find regNum not in use
			for(Integer inter: node.interfereTemps){
				if(this.registerAllocation[(int)inter] == i){
					// regNum i is already allocated
					foundRegNum = false;
					break;
				}
			}
			if(foundRegNum)
				return i;
			
		}
		System.out.format(" could not find register allocation for: %d!!!! :-( \n",node.tempNum);
		return -1;
	}
	
	
	public void printInterfereGraph(){
		int counter = 1;
		for(int i=0; i<this.interferGraph.length; i++){
			interferGraph[i].printNode();			
		}
	}
	
	public void printRegisterAllocation(){
		System.out.println(Arrays.toString(this.registerAllocation));
	}
	
	public void AllocateRegisters(ControlFlowGraph controlFlowGraph){
		createInterferGraph(controlFlowGraph);
		coloringTheGraphush();
	}
	
	public void replaceAllTemps(){
		String tempToReplace, regAllocated;		
        String line = "", fileContent = "";
        BufferedReader reader;
        File file;
        PrintWriter fileWriter;
		//String dirname="./FOLDER_5_OUTPUT/";
		//String filename=String.format(outputFilename);
		try{
            file = new File(outputFileName);
            reader = new BufferedReader(new FileReader(file));
            while((line = reader.readLine()) != null)
            {
            	fileContent += line + "\r\n";
            }
            reader.close();		
	        //Path filePath = Paths.get(dirname+filename);
	        //String fileContent = new String(Files.readAllBytes(filePath));
		}
		catch(Exception e){
			System.out.format("Exception in replaceAllTemps\n");
			e.printStackTrace();
		}
        // replace alll
        for(int i=0; i<this.registerAllocation.length; i++){
        	tempToReplace = String.format("\\bTemp_%d\\b",i);
        	regAllocated = String.format("\\$t%d", registerAllocation[i]);
        	fileContent = fileContent.replaceAll(tempToReplace, regAllocated);
        }
        try{
        	fileWriter = new PrintWriter(outputFileName);
            fileWriter.print(fileContent);
            fileWriter.close();
        }
		catch(Exception e){
			System.out.format("Exception in replaceAllTemps fileWriter\n");
			e.printStackTrace();
		}

        
	}
	
	
}
