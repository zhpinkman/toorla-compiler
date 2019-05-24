package toorla.typeChecking.typeCheckExceptions;

public class PrintArgException extends TypeCheckException {


    public PrintArgException(int line, int column) {
        super(line, column);
    }

    @Override
    public void emit_error_message() {
        error_message = "Type of parameter of print built-in function must " +
                "be integer , string or array of integer";
        System.out.println("Error:Line:" + line + ":" + error_message);
    }
}
