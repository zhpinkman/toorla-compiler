package toorla.typeChecking.typeCheckExceptions;

public class IllegalLoopStatementActions extends TypeCheckException {


    String action;

    public IllegalLoopStatementActions(int line, int column, String action) {
        super(line, column);
        this.action = action;
    }

    @Override
    public void emit_error_message() {

        error_message = "Invalid use of " + action + "," + action + " must be used as loop statement";

        System.out.println("Error:Line:" + line + ":" + error_message);

    }
}
