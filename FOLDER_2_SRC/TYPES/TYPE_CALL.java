package TYPES;

public class TYPE_CALL extends TYPE
{
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static TYPE_CALL instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected TYPE_CALL() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static TYPE_CALL getInstance()
	{
		if (instance == null)
		{
			instance = new TYPE_CALL();
			instance.name = "call";
		}
		return instance;
	}
}
