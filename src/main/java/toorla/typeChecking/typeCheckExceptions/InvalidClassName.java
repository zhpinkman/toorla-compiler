package toorla.typeChecking.typeCheckExceptions;

public class InvalidClassName extends TypeCheckException {

    String class_name;

    public InvalidClassName(int line, int column, String class_name) {
        super(line, column);
        this.class_name = class_name;
    }

    @Override
    public void emit_error_message() {

        error_message = "There is no class with name " + class_name;
        System.out.println("Error:Line:" + line + ":" + error_message);

    }
}
