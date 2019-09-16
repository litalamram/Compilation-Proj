package AST;

public class MyRunTimeException extends RuntimeException
{
    public int row;
    public MyRunTimeException(int row) {
        super();
        this.row = row;
    }
    public MyRunTimeException(String s, int row) {
        super(s);
        this.row = row;
    }
}
