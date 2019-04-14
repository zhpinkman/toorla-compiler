package toorla.symbolTable;

import toorla.symbolTable.exceptions.ItemAlreadyExistsException;
import toorla.symbolTable.exceptions.ItemNotFoundException;
import toorla.symbolTable.symbolTableItem.SymbolTableItem;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private SymbolTable pre;
    private Map<String, SymbolTableItem> items;

    // Static members region

    public static SymbolTable top;
    public static SymbolTable root;

    private static Stack<SymbolTable> stack = new Stack<SymbolTable>();

    public static void push(SymbolTable symbolTable) {
        if (top != null)
            stack.push(top);
        top = symbolTable;
    }


    public static void pop() {
        top = stack.pop();
    }

    // End of static members region

    public SymbolTable() {
        this(null);
    }

    public SymbolTable(SymbolTable pre) {
        this.pre = pre;
        this.items = new HashMap<>();
    }

    public void put(SymbolTableItem item) throws ItemAlreadyExistsException {
        if (items.containsKey(item.getKey()))
            throw new ItemAlreadyExistsException();
        items.put(item.getKey(), item);
    }


    public SymbolTableItem get(String key) throws ItemNotFoundException {
        SymbolTableItem value = items.get(key);
        if (value == null && pre != null)
            return pre.get(key);
        else if (value == null)
            throw new ItemNotFoundException();
        else
            return value;
    }

    public void updateItemInCurrentScope(String prevKey, SymbolTableItem newItem) throws ItemNotFoundException {
        SymbolTableItem value = items.get(prevKey);
        if (value == null)
            throw new ItemNotFoundException();
        else {
            items.remove(prevKey);
            items.put(newItem.getKey(), newItem);
        }
    }

    public SymbolTable getPreSymbolTable() {
        return pre;
    }
}
