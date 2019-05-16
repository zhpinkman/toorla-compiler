package toorla.typeChecking;


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
import toorla.nameAnalyzer.ClassParentshipExtractorPass;
import toorla.symbolTable.SymbolTable;
import toorla.symbolTable.symbolTableItem.ClassSymbolTableItem;
import toorla.symbolTable.symbolTableItem.MethodSymbolTableItem;
import toorla.symbolTable.symbolTableItem.SymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.FieldSymbolTableItem;
import toorla.symbolTable.symbolTableItem.varItems.LocalVariableSymbolTableItem;
import toorla.typeChecking.typeCheckExceptions.*;
import toorla.types.Type;
import toorla.types.singleType.*;
import toorla.visitor.Visitor;
import toorla.utilities.graph.Graph;

import javax.sound.midi.Soundbank;
import javax.sound.midi.SysexMessage;
import java.sql.SQLOutput;

public class TypeChecking implements Visitor<Type> {
    private Program program;
    private Graph<String> classHierarchy;
    private static int loop_depth;
    private String INT_TYPE = "(IntType)";
    private String INT_ARRAY = "(ArrayType,IntType)";
    private String STR_TYPE = "(StringType)";
    private String method_return_type = "";
    private String VAR_PREFIX = "var_";
    private String CLASS_PREFIX = "class_";
    private String METHOD_PREFIX = "method_";
    private String FIELD_PREFIX = "field_";
    private int var_index = 0;

    private static final String VARIABLE_CALLING = "VAR_CALLING_ID";
    private static final String FIELD_CALLING = "FIELD_CALLING_ID";
    private static final String CLASS_CALLING = "CLASS_CALLING_ID";
    private static final String METHOD_CALLING = "METHOD_CALLING_ID";
    private static final String ARRAY_CALLING = "ARRAY_CALLING_ID";
    private String who_is_calling_identifier = VARIABLE_CALLING;

    public TypeChecking(Program p){
        program = p;
        loop_depth = 0;
    }

    public void check(){
        ClassParentshipExtractorPass classParentshipExtractorPass = new ClassParentshipExtractorPass();
        classParentshipExtractorPass.analyze( program );
        classHierarchy = classParentshipExtractorPass.getResult();
        this.visit(program);

    }

    @Override
    public Type visit(PrintLine printStat) {
        Type expr_type = printStat.getArg().accept(this);
        try{
            String str = expr_type.toString();
            if (!str.equals(INT_TYPE)&& !str.equals(STR_TYPE) && !str.equals(INT_ARRAY))
                throw new PrintArgException(printStat.line, printStat.col);
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Assign assignStat) {
        try	{
            if (!assignStat.getLvalue().lvalue_check(SymbolTable.top()))
                throw new LvalueAssignability(assignStat.line, assignStat.col);
        }
        catch(TypeCheckException exception){
            exception.emit_error_message();
        }
        Type lhs = assignStat.getLvalue().accept(this);
        Type rhs = assignStat.getRvalue().accept(this);
//        System.out.println(lhs);
//        System.out.println(rhs);
//        try {
//            SymbolTable.top().get(assignStat.getLvalue().)
//        }
//        catch (Exception exception){
//
//        }
        return new VoidType();
    }

    @Override
    public Type visit(Block block) {
        SymbolTable.pushFromQueue();
        for (Statement statement: block.body){
            statement.accept(this);
        }
        SymbolTable.pop();

        return new VoidType();
    }

    @Override
    public Type visit(Conditional conditional) {
        Type cond = conditional.getCondition().accept(this);
        try {
            if (!cond.toString().equals("(BoolType)")) {
                throw new InvalidLoopCondition(conditional.line, conditional.col, conditional.toString());
            }
        }catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        SymbolTable.pushFromQueue();
        conditional.getThenStatement().accept(this);
        SymbolTable.pop();
        SymbolTable.pushFromQueue();
        conditional.getElseStatement().accept(this);
        SymbolTable.pop();
        return null;
    }

    @Override
    public Type visit(While whileStat) {
        SymbolTable.pushFromQueue();
        loop_depth ++;
        Type expr_type = whileStat.expr.accept(this);
        Type body_type = whileStat.body.accept(this);

        try	{
            if (!expr_type.toString().equals("(BoolType)"))
                throw new InvalidLoopCondition(whileStat.line, whileStat.col, whileStat.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        loop_depth --;
        SymbolTable.pop();
        return body_type;
    }

    @Override
    public Type visit(Return returnStat) {
        try {
//            System.out.println(returnStat.getReturnedExpr().accept(this).toString());
//            System.out.println(method_return_type);
            if (!returnStat.getReturnedExpr().accept(this).toString().equals(method_return_type))
                throw new InvalidReturnType(returnStat.line, returnStat.col);
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Break breakStat) {
        try{
            if (loop_depth == 0)
                throw new IllegalLoopStatementActions(breakStat.line, breakStat.col, breakStat.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Continue continueStat) {
        try{
            if (loop_depth == 0)
                throw new IllegalLoopStatementActions(continueStat.line, continueStat.col, continueStat.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Skip skip) {
        return new VoidType();
    }

    @Override
    public Type visit(LocalVarDef localVarDef) {
        var_index++;
        try {
            LocalVariableSymbolTableItem lvst = (LocalVariableSymbolTableItem) SymbolTable.top().get(VAR_PREFIX + localVarDef.getLocalVarName().getName());
            lvst.setInital_value(localVarDef.getInitialValue());
//            System.out.println(lvst.getInital_value());

        }
        catch (Exception exception){
            System.out.println("testing");
        }


        return new VoidType();
    }

    @Override
    public Type visit(IncStatement incStatement) {
        try {
            if (!incStatement.getOperand().lvalue_check(new SymbolTable()))
                throw new InvalidIncDecOperand(incStatement.line, incStatement.col, incStatement.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(DecStatement decStatement) {
        try {
            if (!decStatement.getOperand().lvalue_check(new SymbolTable()))
                throw new InvalidIncDecOperand(decStatement.line, decStatement.col, decStatement.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Plus plusExpr) {

        Type first_operand_type = plusExpr.getLhs().accept(this);
        Type second_operand_type = plusExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE)  || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(plusExpr.line, plusExpr.col, plusExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new IntType();
    }

    @Override
    public Type visit(Minus minusExpr) {

        Type first_operand_type = minusExpr.getLhs().accept(this);
        Type second_operand_type = minusExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(minusExpr.line, minusExpr.col,minusExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new IntType();
    }

    @Override
    public Type visit(Times timesExpr) {
        Type first_operand_type = timesExpr.getLhs().accept(this);
        Type second_operand_type = timesExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(timesExpr.line, timesExpr.col, timesExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new IntType();
    }

    @Override
    public Type visit(Division divExpr) {
        Type first_operand_type = divExpr.getLhs().accept(this);
        Type second_operand_type = divExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(divExpr.line, divExpr.col, divExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new IntType();
    }

    @Override
    public Type visit(Modulo moduloExpr) {

        Type first_operand_type = moduloExpr.getLhs().accept(this);
        Type second_operand_type = moduloExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(moduloExpr.line, moduloExpr.col, moduloExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new IntType();
    }

    @Override
    public Type visit(Equals equalsExpr) {
        Type first_operand_type =  equalsExpr.getLhs().accept(this);
        Type second_operand_type =  equalsExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(second_operand_type.toString()) )
                throw new InvalidOperationOperands( equalsExpr.line,  equalsExpr.col, equalsExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(GreaterThan gtExpr) {
        Type first_operand_type = gtExpr.getLhs().accept(this);
        Type second_operand_type = gtExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(gtExpr.line, gtExpr.col, gtExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(LessThan lessThanExpr) {
        Type first_operand_type = lessThanExpr.getLhs().accept(this);
        Type second_operand_type = lessThanExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(INT_TYPE) || !second_operand_type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(lessThanExpr.line, lessThanExpr.col, lessThanExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();

    }

    @Override
    public Type visit(And andExpr) {
        Type first_operand_type = andExpr.getLhs().accept(this);
        Type second_operand_type = andExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals("(BoolType)") || !second_operand_type.toString().equals("(BoolType)"))
                throw new InvalidOperationOperands(andExpr.line, andExpr.col, andExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(Or orExpr) {

        Type first_operand_type = orExpr.getLhs().accept(this);
        Type second_operand_type = orExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals("(BoolType)") || !second_operand_type.toString().equals("(BoolType)"))
                throw new InvalidOperationOperands(orExpr.line, orExpr.col, orExpr.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(Neg negExpr) {
        Type type = negExpr.getExpr().accept(this);
        try{
            if (!type.toString().equals(INT_TYPE))
                throw new InvalidOperationOperands(negExpr.line, negExpr.col, negExpr.toString());
        }
        catch(TypeCheckException exception){
            exception.emit_error_message();
        }
        return type;
    }

    @Override
    public Type visit(Not notExpr) {
        Type type = notExpr.getExpr().accept(this);
        try{
            if (!type.toString().equals("(BoolType)"))
                throw new InvalidOperationOperands(notExpr.line, notExpr.col, notExpr.toString());
        }
        catch(TypeCheckException exception){
            exception.emit_error_message();
        }
        return type;
    }

    @Override
    public Type visit(MethodCall methodCall) {
        Type instance_type = methodCall.getInstance().accept(this);

        return null;
    }

    @Override
    public Type visit(Identifier identifier) {
        SymbolTable s = new SymbolTable().top();

        switch (who_is_calling_identifier){
            case VARIABLE_CALLING:
                try {
                    LocalVariableSymbolTableItem var_item = (LocalVariableSymbolTableItem) SymbolTable.top().get("var_"+identifier.getName());
                    if (var_item.getIndex() <= var_index){
                        Type type = var_item.getInital_value().accept(this);
//                System.out.println(type);
                        return type;
                    }
//            System.out.println(var_index);
                }
                catch (Exception exception){
                    try{
                        throw new InvalidVariableCall(identifier.line, identifier.col, identifier.getName());
                    }catch (TypeCheckException e){
                        e.emit_error_message();
                    }
                }
                break;

            case FIELD_CALLING:
                who_is_calling_identifier = VARIABLE_CALLING;
                try {
                    FieldSymbolTableItem field_item = (FieldSymbolTableItem) SymbolTable.top().get("var_" + identifier.getName());
                    return field_item.getVarType();
                }
                catch (Exception exception){
                    return new UndefinedType();
                }

            case METHOD_CALLING:
                who_is_calling_identifier = VARIABLE_CALLING;
                try{
                    MethodSymbolTableItem method_item = (MethodSymbolTableItem) SymbolTable.top().get("method_" + identifier.getName());
                }
                catch (Exception e){

                }
                break;

            case CLASS_CALLING:
                who_is_calling_identifier = VARIABLE_CALLING;
                try{
                    ClassSymbolTableItem method_item = (ClassSymbolTableItem) SymbolTable.top().get("class_" + identifier.getName());
                }
                catch (Exception e){
                    try{
                        throw new InvalidClassName(identifier.line, identifier.col, identifier.getName());
                    }catch (InvalidClassName e2){
                        e2.emit_error_message();
                    }
                }
                break;
        }




        try {
            throw new InvalidVariableCall(identifier.line, identifier.col, identifier.getName());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }

        return null;
    }

    @Override
    public Type visit(Self self) {
        return null;
    }

    @Override
    public Type visit(IntValue intValue) {
        return new IntType();
    }

    @Override
    public Type visit(NewArray newArray) {
        return null;
    }

    @Override
    public Type visit(BoolValue booleanValue) {
        return new BoolType();
    }

    @Override
    public Type visit(StringValue stringValue) {
        return new StringType();
    }

    @Override
    public Type visit(NewClassInstance newClassInstance) {
        try {
            ClassSymbolTableItem class_item  = (ClassSymbolTableItem) SymbolTable.top().get(CLASS_PREFIX + newClassInstance.getClassName().getName());
            return new UserDefinedType(new ClassDeclaration(newClassInstance.getClassName()));
        }
        catch (Exception exception){
            try {
                throw new InvalidClassName(newClassInstance.line, newClassInstance.col, newClassInstance.getClassName().getName());
            }
            catch (TypeCheckException exc){
                exc.emit_error_message();
            }
        }
        return new UndefinedType();
    }

    @Override
    public Type visit(FieldCall fieldCall) {
        try {
            who_is_calling_identifier = CLASS_CALLING;
            UserDefinedType class_type = (UserDefinedType) fieldCall.getInstance().accept(this);
            Identifier class_name = class_type.getClassDeclaration().getName();

            ClassSymbolTableItem class_symbol_table = (ClassSymbolTableItem) SymbolTable.top().get(class_name.getName());
            try {
                FieldSymbolTableItem field = (FieldSymbolTableItem) class_symbol_table.getSymbolTable().get(fieldCall.getField().getName());
                return field.getVarType(); // PRIVATE TODO
            }catch (Exception e){

            }
            who_is_calling_identifier = FIELD_CALLING;
            Type field_type = fieldCall.getField().accept(this);
            return field_type;
        }
        catch (Exception e){

        }

        return new UndefinedType();
    }

    @Override
    public Type visit(ArrayCall arrayCall) {
        return null;
    }

    @Override
    public Type visit(NotEquals notEquals) {

        Type first_operand_type = notEquals.getLhs().accept(this);
        Type second_operand_type = notEquals.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(second_operand_type.toString()))
                throw new InvalidOperationOperands(notEquals.line, notEquals.col, notEquals.toString());
        }
        catch (TypeCheckException exception){
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(ClassDeclaration classDeclaration) {
        SymbolTable.pushFromQueue();
        for (ClassMemberDeclaration classMemberDeclaration: classDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        SymbolTable.pop();
        return new VoidType();
    }

    @Override
    public Type visit(EntryClassDeclaration entryClassDeclaration) {
        SymbolTable.pushFromQueue();
        for (ClassMemberDeclaration classMemberDeclaration: entryClassDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        SymbolTable.pop();
        return new VoidType();
    }

    @Override
    public Type visit(FieldDeclaration fieldDeclaration) {
        String type_name = "";
        String hard_type = fieldDeclaration.getType().toString();
        try {
            if (hard_type.equals(INT_TYPE) || hard_type.equals("(BoolType)") || hard_type.equals(STR_TYPE))
                return new VoidType();
            int index_of_name = hard_type.indexOf(',');
            type_name = hard_type.substring(index_of_name + 1, hard_type.length() - 1);
            SymbolTable.top().get("class_" + type_name);
        }
        catch (Exception exception){
            System.out.println("Error:Line:" + fieldDeclaration.line + ":" + "There is no type with name " + type_name);
        }
        return null;
    }

    @Override
    public Type visit(ParameterDeclaration parameterDeclaration) {
        return null;
    }

    @Override
    public Type visit(MethodDeclaration methodDeclaration) {
        method_return_type = methodDeclaration.getReturnType().toString(); // setting return type of the method declaration
        var_index = 0;
        String type_name = methodDeclaration.getReturnType().toString();
        try {
            if (!type_name.equals(INT_TYPE) && !type_name.equals("(BoolType)") && !type_name.equals(STR_TYPE)) {
                int index_of_name = type_name.indexOf(',');
                type_name = type_name.substring(index_of_name + 1, type_name.length() - 1);
            }
        }
        catch (Exception exception){
            System.out.println("Error:Line:" + methodDeclaration.line + ":" + "There is no type with name " + type_name);
        }
        SymbolTable.pushFromQueue();
        for (Statement statement: methodDeclaration.getBody()){
            statement.accept(this);
        }
        SymbolTable.pop();

        return new VoidType();
    }

    @Override
    public Type visit(LocalVarsDefinitions localVarsDefinitions) {
        for (LocalVarDef lvd: localVarsDefinitions.getVarDefinitions()) {
            lvd.accept(this);
        }
        return null;
    }

    @Override
    public Type visit(Program program) {
        SymbolTable.pushFromQueue();
        for (ClassDeclaration classDeclaration : program.getClasses()){
            classDeclaration.accept(this);
        }
        SymbolTable.pop();
        return new VoidType();
    }

}
