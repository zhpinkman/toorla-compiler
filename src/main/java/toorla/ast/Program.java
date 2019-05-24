package toorla.ast;

import toorla.ast.declaration.classDecs.ClassDeclaration;
import toorla.symbolTable.SymbolTable;
import toorla.types.Type;
import toorla.types.singleType.VoidType;
import toorla.visitor.Visitor;

import java.util.ArrayList;
import java.util.List;

public class Program extends Tree {
    private ArrayList<ClassDeclaration> classes = new ArrayList<>();


    public void addClass(ClassDeclaration classDeclaration) {
        classes.add(classDeclaration);
    }

    public List<ClassDeclaration> getClasses() {
        return classes;
    }

    @Override
    public String toString() {
        return "Program";
    }

    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Type type_check(SymbolTable symbolTable) {
        SymbolTable.pushFromQueue();
        for (ClassDeclaration classDeclaration : this.getClasses()){
            classDeclaration.type_check(symbolTable);
        }
        SymbolTable.pop();
        return new VoidType();
    }
}
