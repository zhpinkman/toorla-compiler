package toorla.typeChecking.typeCheckExceptions;

public class InvalidArraySize extends TypeCheckException {

    public InvalidArraySize(int line, int column) {
        super(line, column);
    }

    @Override
    public void emit_error_message() {
        error_message = "Size of an array must be of type integer";
        System.out.println("Error:Line:" + line + ":" + error_message);

    }
}
