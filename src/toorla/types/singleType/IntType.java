package toorla.types.singleType;

public class IntType extends SingleType {
    @Override
    public String toString() {
        return "(IntType)";
    }
    @Override
    public String toStringForError() {
        return "int";
    }
}
