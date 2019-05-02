package toorla.symbolTable.symbolTableItem.varItems;

import toorla.types.AnonymousType;

public class LocalVariableSymbolTableItem extends VarSymbolTableItem {
    private int index;

    public LocalVariableSymbolTableItem(String name, int index) {
        this.name = name;
        this.varType = new AnonymousType();
        this.index = index;
    }


    public int getIndex() {
        return index;
    }


}
