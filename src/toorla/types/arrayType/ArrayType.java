package toorla.types.arrayType;

import toorla.types.Type;
import toorla.types.singleType.SingleType;

public class ArrayType extends Type {
    private SingleType singleType;

    public ArrayType(SingleType s) {
        this.singleType = s;
    }

    public SingleType getSingleType() {
        return singleType;
    }

    public void setSingleType(SingleType singleType) {
        this.singleType = singleType;
    }

    @Override
    public String toString() {
        return "(ArrayType," + singleType.toString() + ")";
    }
}
