package toorla.types.singleType;

import toorla.types.Type;

public class UndefinedType extends Type {
    @Override
    public String toString() {
        return "(UndefinedType)";
    }
    @Override
    public String toStringForError() {
        return "undefinedType";
    }
}
