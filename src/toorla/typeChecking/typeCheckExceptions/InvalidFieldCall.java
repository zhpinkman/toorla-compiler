package toorla.typeChecking.typeCheckExceptions;

public class InvalidFieldCall extends TypeCheckException {

    String field_name;
    String class_name;

    public InvalidFieldCall(int line, int column, String class_name, String field_name) {
        super(line, column);
        this.class_name = class_name;
        this.field_name = field_name;
    }


    @Override
    public void emit_error_message() {

        error_message = "There is no Field with name " + field_name + " with in\n" +
                "class " + class_name;
        System.out.println("Error:Line:" + line + ":" + error_message);

    }
}
