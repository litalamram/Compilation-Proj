package TYPES;

public abstract class TYPE
{
	/******************************/
	/*  Every type has a name ... */
	/******************************/
	public String name;
	public int offset = 0;
	public boolean isLocal = false;
	public boolean isGlobal = false;

	/*************/
	/* isClass() */
	/*************/
	public boolean isClass(){ return false;}

	/*************/
	/* isArray() */
	/*************/
	public boolean isArray(){ return false;}

	/*************/
	/* isFunction() */
	/*************/
	public boolean isFunction(){ return false;}

	/*************/
	/* isEquals() */
	/*************/
	public boolean isEquals(TYPE other){
		return (this.name).equals(other.name);
	}
	/*************/
	/* isVarDec() */
	/*************/
	public boolean isVarDec(){
		return false;
	}
	/*************/
	/* getTypeToBeCompared() */
	/*************/
	public TYPE getTypeToBeCompared() {return this;}

	public boolean isNullable() { return isClass() || isArray(); }
	public boolean isString(){return false;}
}
