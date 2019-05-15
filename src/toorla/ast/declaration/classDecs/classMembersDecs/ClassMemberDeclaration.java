package toorla.ast.declaration.classDecs.classMembersDecs;

import toorla.symbolTable.SymbolTable;
import toorla.types.Type;
import toorla.visitor.Visitor;

public interface ClassMemberDeclaration {
    <R> R accept(Visitor<R> visitor);

    String toString();

    public abstract Type type_check(SymbolTable symbolTable);
}