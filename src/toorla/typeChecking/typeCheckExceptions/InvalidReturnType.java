package toorla.typeChecking.typeCheckExceptions;

public class InvalidReturnType extends TypeCheckException {

    public InvalidReturnType(int line, int column) {
        super(line, column);
    }

    @Override
    public void emit_error_message() {
        error_message = "return statement expression doesn't match with the method return type";
        System.out.println("Error:Line:" + line + ":" + error_message);
    }
}
