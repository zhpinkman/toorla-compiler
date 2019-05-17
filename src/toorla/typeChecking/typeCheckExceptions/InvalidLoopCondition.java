package toorla.typeChecking.typeCheckExceptions;

public class InvalidLoopCondition extends TypeCheckException {

    String condition_type;

    public InvalidLoopCondition(int line, int column, String condition_type) {
        super(line, column);
        this.condition_type = condition_type;
    }

    @Override
    public void emit_error_message() {

        error_message = "Condition type must be bool in " + condition_type + " statement";
        System.out.println("Error:Line:" + line + ":" + error_message);

    }
}
