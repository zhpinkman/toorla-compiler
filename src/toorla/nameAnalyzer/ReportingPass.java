package toorla.nameAnalyzer;

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
import toorla.nameAnalyzer.compileErrorException.CompileErrorException;
import toorla.visitor.Visitor;

public class ReportingPass implements Visitor<Integer>, INameAnalyzingPass<Void> {

    @Override
    public Integer visit(PrintLine printStat) {
        return 0;
    }

    @Override
    public Integer visit(Assign assignStat) {
        return 0;
    }

    @Override
    public Integer visit(Block block) {
        int numOfErrors = 0;
        for (Statement stmt : block.body)
            numOfErrors += stmt.accept(this);
        return numOfErrors;
    }

    @Override
    public Integer visit(Conditional conditional) {
        return conditional.getThenStatement().accept(this)
        + conditional.getElseStatement().accept(this);
    }

    @Override
    public Integer visit(While whileStat) {
        return whileStat.body.accept(this);
    }

    @Override
    public Integer visit(Return returnStat) {
        return 0;
    }

    @Override
    public Integer visit(Break breakStat) {

        return 0;
    }

    @Override
    public Integer visit(Continue continueStat) {
        return 0;
    }

    @Override
    public Integer visit(Skip skip) {
        return 0;
    }

    @Override
    public Integer visit(LocalVarDef localVarDef) {
        for (CompileErrorException e : localVarDef.relatedErrors) {
            System.out.println(e);
        }
        return localVarDef.relatedErrors.size();
    }

    @Override
    public Integer visit(IncStatement incStatement) {
        return 0;
    }

    @Override
    public Integer visit(DecStatement decStatement) {
        return 0;
    }

    @Override
    public Integer visit(Plus plusExpr) {
        return 0;
    }

    @Override
    public Integer visit(Minus minusExpr) {
        return 0;
    }

    @Override
    public Integer visit(Times timesExpr) {
        return 0;
    }

    @Override
    public Integer visit(Division divExpr) {
        return 0;
    }

    @Override
    public Integer visit(Modulo moduloExpr) {
        return 0;
    }

    @Override
    public Integer visit(Equals equalsExpr) {
        return 0;
    }

    @Override
    public Integer visit(GreaterThan gtExpr) {
        return 0;
    }

    @Override
    public Integer visit(LessThan lessThanExpr) {
        return 0;
    }

    @Override
    public Integer visit(And andExpr) {
        return 0;
    }

    @Override
    public Integer visit(Or orExpr) {
        return 0;
    }

    @Override
    public Integer visit(Neg negExpr) {
        return 0;
    }

    @Override
    public Integer visit(Not notExpr) {
        return 0;
    }

    @Override
    public Integer visit(MethodCall methodCall) {
        return 0;
    }

    @Override
    public Integer visit(Identifier identifier) {
        return 0;
    }

    @Override
    public Integer visit(Self self) {
        return 0;
    }

    @Override
    public Integer visit(IntValue intValue) {
        return 0;
    }

    @Override
    public Integer visit(NewArray newArray) {
        return 0;
    }

    @Override
    public Integer visit(BoolValue booleanValue) {
        return 0;
    }

    @Override
    public Integer visit(StringValue stringValue) {
        return 0;
    }

    @Override
    public Integer visit(NewClassInstance newClassInstance) {
        return 0;
    }

    @Override
    public Integer visit(FieldCall fieldCall) {
        return 0;
    }

    @Override
    public Integer visit(ArrayCall arrayCall) {
        return 0;
    }

    @Override
    public Integer visit(NotEquals notEquals) {
        return 0;
    }

    @Override
    public Integer visit(ClassDeclaration classDeclaration) {
        int numOfErrors = classDeclaration.relatedErrors.size();
        for (CompileErrorException e : classDeclaration.relatedErrors) {
            System.out.println(e);
        }
        for (ClassMemberDeclaration cmd : classDeclaration.getClassMembers())
            numOfErrors += cmd.accept(this);
        return numOfErrors;
    }

    @Override
    public Integer visit(EntryClassDeclaration entryClassDeclaration) {
        return this.visit((ClassDeclaration) entryClassDeclaration);
    }

    @Override
    public Integer visit(FieldDeclaration fieldDeclaration) {
        for (CompileErrorException e : fieldDeclaration.relatedErrors) {
            System.out.println(e);
        }
        return fieldDeclaration.relatedErrors.size();
    }

    @Override
    public Integer visit(ParameterDeclaration parameterDeclaration) {
        for (CompileErrorException e : parameterDeclaration.relatedErrors) {
            System.out.println(e);
        }
        return parameterDeclaration.relatedErrors.size();
    }

    @Override
    public Integer visit(MethodDeclaration methodDeclaration) {
        int numOfErrors = methodDeclaration.relatedErrors.size();
        for (CompileErrorException e : methodDeclaration.relatedErrors) {
            System.out.println(e);
        }
        for (ParameterDeclaration pd : methodDeclaration.getArgs())
            numOfErrors += pd.accept(this);
        for (Statement stmt : methodDeclaration.getBody())
            numOfErrors += stmt.accept(this);
        return numOfErrors;
    }

    @Override
    public Integer visit(LocalVarsDefinitions localVarsDefinitions) {
        int numOfErrors = 0;
        for (LocalVarDef lvd : localVarsDefinitions.getVarDefinitions()) {
            numOfErrors += lvd.accept(this);
        }
        return numOfErrors;
    }

    @Override
    public Integer visit(Program program) {
        int numOfErrors = program.relatedErrors.size();
        for (CompileErrorException e : program.relatedErrors) {
            System.out.println(e);
        }
        for (ClassDeclaration cd : program.getClasses()) {
            numOfErrors += cd.accept(this);
        }
        return numOfErrors;
    }

    @Override
    public void analyze(Program program) {
        int numOfErrors = this.visit(program);
        if( numOfErrors != 0 )
            System.exit( 1 );
    }

    @Override
    public Void getResult() {
        return null;
    }
}
