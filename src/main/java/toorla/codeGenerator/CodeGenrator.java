package toorla.codeGenerator;

import toorla.ast.Program;
import toorla.ast.declaration.classDecs.ClassDeclaration;
import toorla.ast.declaration.classDecs.EntryClassDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.ClassMemberDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.FieldDeclaration;
import toorla.ast.declaration.classDecs.classMembersDecs.MethodDeclaration;
import toorla.ast.declaration.localVarDecs.ParameterDeclaration;
import toorla.ast.expression.*;
import toorla.ast.expression.binaryExpression.*;
import toorla.ast.expression.unaryExpression.Neg;
import toorla.ast.expression.unaryExpression.Not;
import toorla.ast.expression.value.BoolValue;
import toorla.ast.expression.value.IntValue;
import toorla.ast.expression.value.StringValue;
import toorla.ast.statement.*;
import toorla.ast.statement.localVarStats.LocalVarDef;
import toorla.ast.statement.localVarStats.LocalVarsDefinitions;
import toorla.ast.statement.returnStatement.Return;
import toorla.compileErrorException.CompileErrorException;
import toorla.compileErrorException.typeErrors.*;
import toorla.compileErrorException.typeErrors.itemNotDeclared.ClassNotDeclaredException;
import toorla.symbolTable.SymbolTable;
import toorla.symbolTable.exceptions.ItemNotFoundException;
import toorla.symbolTable.symbolTableItem.ClassSymbolTableItem;
import toorla.symbolTable.symbolTableItem.MethodSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.LocalVariableSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.VarSymbolTableItem;
import toorla.typeChecker.ExpressionTypeExtractor;
import toorla.types.Type;
import toorla.types.Undefined;
import toorla.types.arrayType.ArrayType;
import toorla.types.singleType.BoolType;
import toorla.types.singleType.IntType;
import toorla.types.singleType.StringType;
import toorla.types.singleType.UserDefinedType;
import toorla.utilities.graph.Graph;
import toorla.visitor.Visitor;

public class CodeGenrator extends Visitor<Void> {
    public Void visit(Plus plusExpr) {
        return null;
    }

    public Void visit(Minus minusExpr) {
        return null;
    }

    public Void visit(Times timesExpr) {
        return null;
    }

    public Void visit(Division divExpr) {
        return null;
    }

    public Void visit(Modulo moduloExpr) {
        return null;
    }

    public Void visit(Equals equalsExpr) {
        return null;
    }

    public Void visit(GreaterThan gtExpr) {
        return null;
    }

    public Void visit(LessThan lessThanExpr) {
        return null;
    }

    public Void visit(And andExpr) {
        return null;
    }

    public Void visit(Or orExpr) {
        return null;
    }

    public Void visit(Neg negExpr) {
        return null;
    }

    public Void visit(Not notExpr) {
        return null;
    }

    public Void visit(MethodCall methodCall) {
        return null;
    }

    public Void visit(Identifier identifier) {
        return null;
    }

    public Void visit(Self self) {
        return null;
    }

    public Void visit(IntValue intValue) {
        return null;
    }

    public Void visit(NewArray newArray) {
        return null;
    }

    public Void visit(BoolValue booleanValue) {
        return null;
    }

    public Void visit(StringValue stringValue) {
        return null;
    }

    public Void visit(NewClassInstance newClassInstance) {
        return null;
    }

    public Void visit(FieldCall fieldCall) {
        return null;
    }

    public Void visit(ArrayCall arrayCall) {
        return null;
    }

    public Void visit(NotEquals notEquals) {
        return null;
    }

    // Statement
    public Void visit(PrintLine printStat) {
        return null;
    }

    public Void visit(Assign assignStat) {
        return null;
    }

    public Void visit(Block block) {
        return null;
    }

    public Void visit(Conditional conditional) {
        return null;
    }

    public Void visit(While whileStat) {
        return null;
    }

    public Void visit(Return returnStat) {
        return null;
    }

    public Void visit(Break breakStat) {
        return null;
    }

    public Void visit(Continue continueStat) {
        return null;
    }

    public Void visit(Skip skip) {
        return null;
    }

    public Void visit(LocalVarDef localVarDef) {
        return null;
    }

    public Void visit(IncStatement incStatement) {
        return null;
    }

    public Void visit(DecStatement decStatement) {
        return null;
    }

    // declarations
    public Void visit(ClassDeclaration classDeclaration) {
        return null;
    }

    public Void visit(EntryClassDeclaration entryClassDeclaration) {
        return null;
    }

    public Void visit(FieldDeclaration fieldDeclaration) {
        return null;
    }

    public Void visit(ParameterDeclaration parameterDeclaration) {
        return null;
    }

    public Void visit(MethodDeclaration methodDeclaration) {
        return null;
    }

    public Void visit(LocalVarsDefinitions localVarsDefinitions) {
        return null;
    }

    public Void visit(Program program) {
        return null;
    }

}
