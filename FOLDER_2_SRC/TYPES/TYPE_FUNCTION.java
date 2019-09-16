package TYPES;

public class TYPE_FUNCTION extends TYPE
{
	/***********************************/
	/* The return type of the function */
	/***********************************/
	public TYPE returnType;

	/*************************/
	/* types of input params */
	/*************************/
	public TYPE_LIST params;
	public boolean isMethod;
	public int numOfParams = 0;
	public int localVarNum;
	public String labelName = "";
	public boolean isOverride = false;

	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FUNCTION(TYPE returnType,String name,TYPE_LIST params,boolean isMethod,int numOfParams,int localVarNum)
	{
		this.name = name;
		this.returnType = returnType;
		this.params = params;
		this.isMethod = isMethod;
		this.numOfParams = numOfParams;
        this.localVarNum = localVarNum;
	}

	public boolean isFunction() {return true;}
	public TYPE getTypeToBeCompared() {return this.returnType;}
}
