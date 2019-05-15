package toorla.ast.expression;

import toorla.ast.Tree;
import toorla.symbolTable.SymbolTable;

public abstract class Expression extends Tree {
    public abstract Boolean lvalue_check(SymbolTable symbolTable);
};
