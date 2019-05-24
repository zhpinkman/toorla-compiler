package toorla.typeChecking.typeCheckExceptions;

public class InvalidVariableCall extends TypeCheckException {


    private String var_name;

    public InvalidVariableCall(int line, int column, String var_name) {
        super(line, column);
        this.var_name = var_name;

    }


    @Override
    public void emit_error_message() {
        error_message = "Variable " + var_name +  " is not declared yet in this " +
                "Scope";
        System.out.println("Error:Line:" + line + ":" + error_message);
    }

}
