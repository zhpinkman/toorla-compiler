package toorla.typeChecking.typeCheckExceptions;

public class InvalidMethodCall extends TypeCheckException {

    String class_name;
    String method_name;

    public InvalidMethodCall(int line, int column, String class_name, String method_name) {
        super(line, column);
        this.class_name = class_name;
        this.method_name = method_name;
    }

    @Override
    public void emit_error_message() {
        error_message = "There is no Method with name " + method_name + " with " +
                "such parameters in class " + class_name;
        System.out.println("Error:Line:" + line + ":" + error_message);
    }
}
