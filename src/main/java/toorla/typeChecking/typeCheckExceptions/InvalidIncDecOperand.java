package toorla.typeChecking.typeCheckExceptions;

public class InvalidIncDecOperand extends TypeCheckException {

    String operation_type;

    public InvalidIncDecOperand(int line, int column, String operation_type) {
        super(line, column);
        this.operation_type = operation_type;
    }


    @Override
    public void emit_error_message() {
        error_message = "Operand of " + operation_type + " must be a valid lvalue";
        System.out.println("Error:Line:" + line + ":" + error_message);

    }
}
