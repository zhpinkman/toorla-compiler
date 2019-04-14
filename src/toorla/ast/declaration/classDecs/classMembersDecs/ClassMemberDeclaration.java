package toorla.ast.declaration.classDecs.classMembersDecs;

import toorla.visitor.Visitor;

public interface ClassMemberDeclaration {
    <R> R accept(Visitor<R> visitor);
    String toString();
    
}