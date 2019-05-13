package toorla.typeChecking.typeCheckExceptions;

public class InvalidMethodCall extends TypeCheckException {

    String class_name;
    String method_name;

    public InvalidMethodCall(int line, int column, String class_name, String method_name) {
        super(line, column);

    }

    @Override
    public void emit_error_message() {
        error_message = "There is no Method with name " + method_name + " with\n" +
                "such parameters in class " + class_name;
        System.out.println("Error:Line:" + line + ":" + error_message);
    }
}
