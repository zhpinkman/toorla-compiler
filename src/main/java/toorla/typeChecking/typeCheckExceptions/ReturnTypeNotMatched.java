package toorla.typeChecking.typeCheckExceptions;

public class ReturnTypeNotMatched extends TypeCheckException {

    String return_type;

    public ReturnTypeNotMatched(int line, int column, String return_type) {
        super(line, column);
        this.return_type = return_type;
    }

    @Override
    public void emit_error_message() {

        error_message = "Expression returned by this method must be " + return_type;
        System.out.println("Error:Line:" + line + ":" + error_message);

    }
}
