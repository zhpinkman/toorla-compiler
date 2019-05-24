package toorla.typeChecking.typeCheckExceptions;

public class IllegalAccessToMember extends TypeCheckException {

    String class_name;
    String member_type;
    String member_name;

    public IllegalAccessToMember(int line, int column, String class_name, String member_type, String member_name) {
        super(line, column);
        this.class_name = class_name;
        this.member_type = member_type;
        this.member_name = member_name;
    }

    @Override
    public void emit_error_message() {

        error_message = "Illegal access to " + member_type + " " + member_name + " of an object of class " + class_name;
        System.out.println("Error:Line:" + line + ":" + error_message);

    }
}
