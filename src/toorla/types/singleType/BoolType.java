package toorla.types.singleType;

public class BoolType extends SingleType {
    @Override
    public String toString() {
        return "(BoolType)";
    }
    @Override
    public String toStringForError() {
        return "bool";
    }
}
