package toorla.typeChecking.typeCheckExceptions;

public class LvalueAssignability extends TypeCheckException {

    public LvalueAssignability(int line, int column) {
        super(line, column);
    }

    @Override
    public void emit_error_message() {

        error_message = "Left hand side expression is not assignable";
        System.out.println("Error:Line:" + line + ":" + error_message);

    }
}
