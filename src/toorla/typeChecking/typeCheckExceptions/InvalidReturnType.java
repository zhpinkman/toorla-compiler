package toorla.typeChecking.typeCheckExceptions;

public class InvalidReturnType extends TypeCheckException {

    private String method_return_type;
    public InvalidReturnType(int line, int column, String method_return) {
        super(line, column);
        this.method_return_type = method_return;
    }

    @Override
    public void emit_error_message() {
        error_message = "Expression returned by this method must be " + method_return_type;
        System.out.println("Error:Line:" + line + ":" + error_message);
    }
}
