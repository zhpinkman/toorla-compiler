package toorla.ast.declaration.classDecs.classMembersDecs;

import toorla.visitor.Visitor;

public interface ClassMemberDeclaration {
    public <R> R accept(Visitor<R> visitor);
    public String toString();
    
}