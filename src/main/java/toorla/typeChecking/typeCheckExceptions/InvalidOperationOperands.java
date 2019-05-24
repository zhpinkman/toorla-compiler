package toorla.typeChecking.typeCheckExceptions;

public class InvalidOperationOperands extends TypeCheckException {

    String operation_name;

    public InvalidOperationOperands(int line, int column, String operation_name) {
        super(line, column);
        this.operation_name = operation_name;
    }

    @Override
    public void emit_error_message() {

        error_message = "Unsupported operand types for " + operation_name;

        System.out.println("Error:Line:" + line + ":" + error_message);



    }
}
