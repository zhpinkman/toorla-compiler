package toorla.typeChecking.typeCheckExceptions;

public abstract class TypeCheckException extends Exception {
    int line;
    int column;
    String error_message;

    public TypeCheckException(int line, int column){
        this.line = line;
        this.column = column;
    }

    public abstract void emit_error_message();

}
