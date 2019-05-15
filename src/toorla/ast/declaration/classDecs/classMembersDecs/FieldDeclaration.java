package toorla.ast.declaration.classDecs.classMembersDecs;

import toorla.ast.declaration.TypedVariableDeclaration;
import toorla.ast.expression.Identifier;
import toorla.symbolTable.SymbolTable;
import toorla.typeChecking.typeCheckExceptions.InvalidClassName;
import toorla.typeChecking.typeCheckExceptions.TypeCheckException;
import toorla.types.Type;
import toorla.visitor.Visitor;

import javax.sound.midi.SysexMessage;

public class FieldDeclaration extends TypedVariableDeclaration implements ClassMemberDeclaration {

    private AccessModifier accessModifier;

    public FieldDeclaration(Identifier name) {
        this.identifier = name;
        this.accessModifier = AccessModifier.ACCESS_MODIFIER_PRIVATE;
    }

    public FieldDeclaration(Identifier identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
        this.accessModifier = AccessModifier.ACCESS_MODIFIER_PRIVATE;
    }

    public FieldDeclaration(Identifier identifier, Type type, AccessModifier modifier) {
        this.identifier = identifier;
        this.type = type;
        this.accessModifier = modifier;
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    /**
     * @param accessModifier the accessModifier to set
     */
    public void setAccessModifier(AccessModifier accessModifier) {
        this.accessModifier = accessModifier;
    }

    @Override
    public String toString() {
        return "FieldDeclaration";
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Type type_check(SymbolTable symbolTable) {
        try {
//            System.out.println(this.type.);
            SymbolTable.top().get("class_sss");
            System.out.println("hoooray");
        }
        catch (Exception exception){
            System.out.println("ahhh");
        }
        return null;
    }
}
