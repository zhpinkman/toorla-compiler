package toorla.types.singleType;

public class StringType extends SingleType {
    @Override
    public String toString() {
        return "(StringType)";
    }
    @Override
    public String toStringForError() {
        return "string";
    }
}
