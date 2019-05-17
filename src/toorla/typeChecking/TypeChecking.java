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
import toorla.types.arrayType.ArrayType;
import toorla.types.singleType.*;
import toorla.visitor.Visitor;
import toorla.utilities.graph.Graph;

import javax.sound.midi.Soundbank;
import javax.sound.midi.SysexMessage;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TypeChecking implements Visitor<Type> {
    private boolean type_check = true;
    private Program program;
    private Graph<String> classHierarchy;
    private static int loop_depth;
    private String INT_TYPE = "(IntType)";
    private String INT_ARRAY = "(ArrayType,IntType)";
    private String STR_TYPE = "(StringType)";
    private String BOOL_TYPE = "(BoolType)";
    private String ARRAY_TYPE = "(ArrayType,";
    private String UNDEFINED_TYPE = "(UndefinedType)";
    private Type method_return_type;
    private String VAR_PREFIX = "var_";
    private String CLASS_PREFIX = "class_";
    private String METHOD_PREFIX = "method_";
    private String FIELD_PREFIX = "field_";
    private int var_index = 0;
    private static ClassDeclaration current_class;
    private String public_access = "(ACCESS_MODIFIER_PUBLIC)";
    private String private_access = "(ACCESS_MODIFIER_PRIVATE)";

    private static final String VARIABLE_CALLING = "VAR_CALLING_ID";
    private static final String FIELD_CALLING = "FIELD_CALLING_ID";
    private static final String CLASS_CALLING = "CLASS_CALLING_ID";
    private static final String METHOD_CALLING = "METHOD_CALLING_ID";
    private static final String ARRAY_CALLING = "ARRAY_CALLING_ID";
    private String who_is_calling_identifier = VARIABLE_CALLING;

    private Boolean is_subtype(String lhs, String rhs){ //rhs is subtype of lhs // lhs is father
        try {
//            System.out.println(lhs);
//            System.out.println(rhs);

            int index;
            index = lhs.indexOf(',');
            if(index != -1) {
                lhs = lhs.substring(index + 1, lhs.length() - 1);
            }
            index = rhs.indexOf(',');
            if(index != -1) {
                rhs = rhs.substring(index + 1, rhs.length() - 1);
            }
            Collection<String> a = classHierarchy.getParentsOfNode(rhs);
            if (lhs.equals(rhs) || classHierarchy.getParentsOfNode(rhs).contains(lhs) ){
//                System.out.println(true);
                return true;
            }
            else{
//                System.out.println(false);
                return false;
            }
        }
        catch (Exception exception){
            //System.out.println("wow");
        }

        if(lhs.equals(UNDEFINED_TYPE) || rhs.equals(UNDEFINED_TYPE)){
            return true;// it may have been undefined
        }else {
            return false;
        }
    }

    private Boolean is_not_primitive(Type type){
        if (!type.toString().equals(INT_TYPE) && !type.toString().equals(STR_TYPE) && !type.toString().equals(BOOL_TYPE)
        && !type.toString().startsWith("(ArrayType,"))
            return true;
        else
            return false;
    }

    public TypeChecking(Program p){
        program = p;
        loop_depth = 0;
    }

    public boolean check(){
        ClassParentshipExtractorPass classParentshipExtractorPass = new ClassParentshipExtractorPass();
        classParentshipExtractorPass.analyze( program );
        classHierarchy = classParentshipExtractorPass.getResult();
        this.visit(program);
        return type_check;
    }

    @Override
    public Type visit(PrintLine printStat) {
        Type expr_type = printStat.getArg().accept(this);
        try{
            String str = expr_type.toString();
            if (!str.equals(INT_TYPE)&& !str.equals(STR_TYPE) && !str.equals(INT_ARRAY) && !str.equals(UNDEFINED_TYPE))
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
//        System.out.println(lhs);
        Type rhs = assignStat.getRvalue().accept(this);
//        System.out.println(rhs);
        if (is_not_primitive(lhs) && is_not_primitive(rhs)){
            String rhs_class_name ;
            String lhs_class_name ;
            int index;
            index = lhs.toString().indexOf(',');
            lhs_class_name = lhs.toString().substring(index + 1, lhs.toString().length() - 1);
            index = rhs.toString().indexOf(',');
            rhs_class_name = rhs.toString().substring(index + 1, rhs.toString().length() - 1);
//            System.out.println(lhs_class_name);
//            System.out.println(rhs_class_name);
            try {
                if (is_subtype(lhs_class_name, rhs_class_name))
                    return new VoidType();
                else
                    throw new LvalueAssignability(assignStat.line, assignStat.col);
            }
            catch (TypeCheckException exception){
                exception.emit_error_message();
            }

        }
        else{
            try{
                if (!lhs.toString().contains(rhs.toString()) && !rhs.toString().contains(lhs.toString())) // not sure about arrays and assigning each of them to one another
                    throw new LvalueAssignability(assignStat.line, assignStat.col);
            }
            catch (TypeCheckException exception){
                exception.emit_error_message();
            }
        }

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
            if (!cond.toString().equals("(BoolType)") && !cond.toString().equals(UNDEFINED_TYPE)) {

                throw new InvalidLoopCondition(conditional.getCondition().line, conditional.getCondition().col, conditional.toString());
            }
        }catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
        }
        SymbolTable.pushFromQueue();
        conditional.getThenStatement().accept(this);
        SymbolTable.pop();
        SymbolTable.pushFromQueue();
        conditional.getElseStatement().accept(this);
        SymbolTable.pop();
        return new VoidType();
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
            type_check = false;
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
            Type return_type = returnStat.getReturnedExpr().accept(this);

            if (!is_subtype(method_return_type.toString(), return_type.toString()) && !return_type.toString().equals(UNDEFINED_TYPE) )
                throw new InvalidReturnType(returnStat.line, returnStat.col, method_return_type.toStringForError());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Break breakStat) {
        try{
            if (loop_depth == 0)
                throw new IllegalLoopStatementActions(breakStat.line, breakStat.col, "Break");
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Continue continueStat) {
        try{
            if (loop_depth == 0)
                throw new IllegalLoopStatementActions(continueStat.line, continueStat.col, "Continue");
        }
        catch (TypeCheckException exception){
            type_check = false;
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
//            System.out.println("1");
            LocalVariableSymbolTableItem lvst = (LocalVariableSymbolTableItem) SymbolTable.top().get(VAR_PREFIX + localVarDef.getLocalVarName().getName());
            lvst.setInital_value(localVarDef.getInitialValue().accept(this));
//            System.out.println(lvst.getInital_value());

        }
        catch (Exception exception){
//            System.out.println("testing");
        }


        return new VoidType();
    }

    @Override
    public Type visit(IncStatement incStatement) {
        try {
            if (!incStatement.getOperand().lvalue_check(new SymbolTable()))
                throw new InvalidIncDecOperand(incStatement.line, incStatement.col, "Inc");

            Type op_type = incStatement.getOperand().accept(this);
            if( !op_type.toString().equals(INT_TYPE) && !op_type.toString().equals(UNDEFINED_TYPE) ) {
                try {
                    throw new InvalidOperationOperands(incStatement.line, incStatement.col, incStatement.toString());
                } catch (InvalidOperationOperands e) {
                    type_check = false;
                    e.emit_error_message();
                }
            }
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(DecStatement decStatement) {
        try {
            if (!decStatement.getOperand().lvalue_check(new SymbolTable()))
                throw new InvalidIncDecOperand(decStatement.line, decStatement.col, "Dec");

            Type op_type = decStatement.getOperand().accept(this);
            if( !op_type.toString().equals(INT_TYPE) && !op_type.toString().equals(UNDEFINED_TYPE) ) {
                try {
                    throw new InvalidOperationOperands(decStatement.line, decStatement.col, decStatement.toString());
                } catch (InvalidOperationOperands e) {
                    type_check = false;
                    e.emit_error_message();
                }
            }
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
        }
        return new VoidType();
    }

    @Override
    public Type visit(Plus plusExpr) {

        Type first_operand_type = plusExpr.getLhs().accept(this);
        Type second_operand_type = plusExpr.getRhs().accept(this);
        try {
            if  ( (!first_operand_type.toString().equals(INT_TYPE)&&!first_operand_type.toString().equals(UNDEFINED_TYPE) ) ||
                    (!second_operand_type.toString().equals(INT_TYPE)&&!second_operand_type.toString().equals(UNDEFINED_TYPE)) )
                throw new InvalidOperationOperands(plusExpr.line, plusExpr.col, plusExpr.toString());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return new IntType();
    }

    @Override
    public Type visit(Minus minusExpr) {

        Type first_operand_type = minusExpr.getLhs().accept(this);
        Type second_operand_type = minusExpr.getRhs().accept(this);
        try {
            if  ( (!first_operand_type.toString().equals(INT_TYPE)&&!first_operand_type.toString().equals(UNDEFINED_TYPE) ) ||
                    (!second_operand_type.toString().equals(INT_TYPE)&&!second_operand_type.toString().equals(UNDEFINED_TYPE)) )
                throw new InvalidOperationOperands(minusExpr.line, minusExpr.col,minusExpr.toString());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return new IntType();
    }

    @Override
    public Type visit(Times timesExpr) {
        Type first_operand_type = timesExpr.getLhs().accept(this);
        Type second_operand_type = timesExpr.getRhs().accept(this);
        try {
            if  ( (!first_operand_type.toString().equals(INT_TYPE)&&!first_operand_type.toString().equals(UNDEFINED_TYPE) ) ||
                    (!second_operand_type.toString().equals(INT_TYPE)&&!second_operand_type.toString().equals(UNDEFINED_TYPE)) )
                throw new InvalidOperationOperands(timesExpr.line, timesExpr.col, timesExpr.toString());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return new IntType();
    }

    @Override
    public Type visit(Division divExpr) {
        Type first_operand_type = divExpr.getLhs().accept(this);
        Type second_operand_type = divExpr.getRhs().accept(this);
        try {
            if  ( (!first_operand_type.toString().equals(INT_TYPE)&&!first_operand_type.toString().equals(UNDEFINED_TYPE) ) ||
                    (!second_operand_type.toString().equals(INT_TYPE)&&!second_operand_type.toString().equals(UNDEFINED_TYPE)) )
                throw new InvalidOperationOperands(divExpr.line, divExpr.col, divExpr.toString());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return new IntType();
    }

    @Override
    public Type visit(Modulo moduloExpr) {

        Type first_operand_type = moduloExpr.getLhs().accept(this);
        Type second_operand_type = moduloExpr.getRhs().accept(this);
        try {
            if  ( (!first_operand_type.toString().equals(INT_TYPE)&&!first_operand_type.toString().equals(UNDEFINED_TYPE) ) ||
                    (!second_operand_type.toString().equals(INT_TYPE)&&!second_operand_type.toString().equals(UNDEFINED_TYPE)) )
                throw new InvalidOperationOperands(moduloExpr.line, moduloExpr.col, moduloExpr.toString());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return new IntType();
    }

    @Override
    public Type visit(Equals equalsExpr) {
        Type first_operand_type =  equalsExpr.getLhs().accept(this);
        Type second_operand_type =  equalsExpr.getRhs().accept(this);
        try {
            if (!first_operand_type.toString().equals(second_operand_type.toString()) &&  !first_operand_type.toString().equals(UNDEFINED_TYPE) && !second_operand_type.toString().equals(UNDEFINED_TYPE)  )
                throw new InvalidOperationOperands( equalsExpr.line,  equalsExpr.col, equalsExpr.toString());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return new BoolType();
    }

    @Override
    public Type visit(GreaterThan gtExpr) {
        Type first_operand_type = gtExpr.getLhs().accept(this);
        Type second_operand_type = gtExpr.getRhs().accept(this);
        try {
            if ( (!first_operand_type.toString().equals(INT_TYPE)&&!first_operand_type.toString().equals(UNDEFINED_TYPE) ) ||
                    (!second_operand_type.toString().equals(INT_TYPE)&&!second_operand_type.toString().equals(UNDEFINED_TYPE)) )
                throw new InvalidOperationOperands(gtExpr.line, gtExpr.col, gtExpr.toString());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return new BoolType();
    }

    @Override
    public Type visit(LessThan lessThanExpr) {
        Type first_operand_type = lessThanExpr.getLhs().accept(this);
        Type second_operand_type = lessThanExpr.getRhs().accept(this);
        try {
            if ( (!first_operand_type.toString().equals(INT_TYPE)&&!first_operand_type.toString().equals(UNDEFINED_TYPE) ) ||
                    (!second_operand_type.toString().equals(INT_TYPE)&&!second_operand_type.toString().equals(UNDEFINED_TYPE)) )
                throw new InvalidOperationOperands(lessThanExpr.line, lessThanExpr.col, lessThanExpr.toString());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return new BoolType();

    }

    @Override
    public Type visit(And andExpr) {
        Type first_operand_type = andExpr.getLhs().accept(this);
        Type second_operand_type = andExpr.getRhs().accept(this);
        try {
            if ( (!first_operand_type.toString().equals("(BoolType)")&&!first_operand_type.toString().equals(UNDEFINED_TYPE) ) ||
                    (!second_operand_type.toString().equals("(BoolType)")&&!second_operand_type.toString().equals(UNDEFINED_TYPE)) )
                throw new InvalidOperationOperands(andExpr.line, andExpr.col, andExpr.toString());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return new BoolType();
    }

    @Override
    public Type visit(Or orExpr) {

        Type first_operand_type = orExpr.getLhs().accept(this);
        Type second_operand_type = orExpr.getRhs().accept(this);
        try {
            if ( (!first_operand_type.toString().equals("(BoolType)")&&!first_operand_type.toString().equals(UNDEFINED_TYPE) ) ||
                    (!second_operand_type.toString().equals("(BoolType)")&&!second_operand_type.toString().equals(UNDEFINED_TYPE)) )
                throw new InvalidOperationOperands(orExpr.line, orExpr.col, orExpr.toString());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return new BoolType();
    }

    @Override
    public Type visit(Neg negExpr) {
        Type type = negExpr.getExpr().accept(this);
        try{
            if (!type.toString().equals(INT_TYPE) && !type.toString().equals(UNDEFINED_TYPE))
                throw new InvalidOperationOperands(negExpr.line, negExpr.col, negExpr.toString());
        }
        catch(TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return type;
    }

    @Override
    public Type visit(Not notExpr) {
        Type type = notExpr.getExpr().accept(this);
        try{
            if (!type.toString().equals("(BoolType)") && !type.toString().equals(UNDEFINED_TYPE) )
                throw new InvalidOperationOperands(notExpr.line, notExpr.col, notExpr.toString());
        }
        catch(TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
            return new UndefinedType();
        }
        return type;
    }

    @Override
    public Type visit(MethodCall methodCall) {
        Type instance_type = methodCall.getInstance().accept(this);

        boolean flag = is_using_self_or_nothing(methodCall.getInstance());

        try {
            String class_name = ((UserDefinedType) instance_type).getClassDeclaration().getName().getName();
            ClassSymbolTableItem class_symbol_table = (ClassSymbolTableItem) SymbolTable.top().get(CLASS_PREFIX + class_name);
            try {
                MethodSymbolTableItem method = (MethodSymbolTableItem) class_symbol_table.getSymbolTable().get(METHOD_PREFIX + methodCall.getMethodName().getName());

                if(method.getArgumentsTypes().size() != methodCall.getArgs().size()) {
                    throw new Exception();  // GOES TO InvalidMethodCall if args doesnt match
                }
                int i=0;
                for (Expression arg_expr: methodCall.getArgs() ) {
                    Type arg_type = arg_expr.accept(this);

                    if(!arg_type.toString().equals(UNDEFINED_TYPE) &&
                            !method.getArgumentsTypes().get(i).toString().equals(UNDEFINED_TYPE) &&
                            !is_subtype(method.getArgumentsTypes().get(i).toString(), arg_type.toString() ) ){
                        throw new Exception(); // GOES TO InvalidMethodCall if args doesnt match
                    }
                    i++;
                }
                if ( method.getAccessModifier().toString().equals(public_access) || ( is_subtype(class_name, current_class.getName().getName()) && flag ) ){
                    return method.getReturnType();  // PUBLIC or (Private and subClass)
                }else{
                    try{
                        throw new IllegalAccessToMember(methodCall.line, methodCall.col, class_name, "Method", method.getName());
                    }catch (IllegalAccessToMember ie){
                        type_check = false;
                        ie.emit_error_message();
                    }
                }
//                System.out.println("tested");
            }catch (Exception  exception){
                try{
                    throw new InvalidMethodCall(methodCall.line, methodCall.col, class_name, methodCall.getMethodName().getName());
                }catch (InvalidMethodCall ie){
                    type_check = false;
                    ie.emit_error_message();
                }
            }
        }
        catch (Exception e){
            //Error for when method call instance was not a class.
            try {
                if(!instance_type.toString().equals(UNDEFINED_TYPE) ) { // if not undefined
                    throw new InvalidOperationOperands(methodCall.line, methodCall.col, methodCall.toString());
                }
            }catch (InvalidOperationOperands io){
                type_check = false;
                io.emit_error_message();
            }
        }

        return new UndefinedType();
    }


    @Override
    public Type visit(Identifier identifier) {
//        System.out.println("here");
        //SymbolTable s = new SymbolTable().top();


        try {
            LocalVariableSymbolTableItem var_item = (LocalVariableSymbolTableItem) SymbolTable.top().get("var_" + identifier.getName());
            if (var_item.getIndex() <= var_index) {
                if(var_item.getInital_value() == null){

                }
                return var_item.getInital_value();
//                System.out.println(type);
            }
//            System.out.println(var_index);
        } catch (Exception exception) {
            //
        }

        try {
            FieldSymbolTableItem field_item = (FieldSymbolTableItem) SymbolTable.top().get("var_" + identifier.getName());
            return field_item.getVarType();
        } catch (Exception exception) {
            //Field call is being handled int it's node, if there is no a.b we assume it was variable we were searching for
            //System.out.println("error about couldn't finding variable or field"); // not sure which error to emit
        }


        try {
            throw new InvalidVariableCall(identifier.line, identifier.col, identifier.getName());
        }
        catch (TypeCheckException exception){
            type_check = false;
            exception.emit_error_message();
        }

        return new UndefinedType();
    }




    @Override
    public Type visit(Self self) {
//        System.out.println("here");
        return new UserDefinedType(current_class);
    }

    @Override
    public Type visit(IntValue intValue) {
        return new IntType();
    }

    @Override
    public Type visit(NewArray newArray) {
        Type index_type = newArray.getLength().accept(this);

        if(!index_type.toString().equals(INT_TYPE) && !index_type.toString().equals(UNDEFINED_TYPE)){
            try{
                throw new InvalidArraySize(newArray.line, newArray.col);
            }catch (InvalidArraySize e){
                type_check = false;
                e.emit_error_message();
            }
        }

        return new ArrayType(newArray.getType());
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
                type_check = false;
                exc.emit_error_message();
            }
        }
        return new UndefinedType();
    }

    private boolean is_using_self_or_nothing(Expression e){
        try {
            Self testSelf = (Self) e;
            return true;
        }catch (Exception e1) {
            return false;
        }
    }

    @Override
    public Type visit(FieldCall fieldCall) {
        Type instance_type = fieldCall.getInstance().accept(this);

        boolean flag = is_using_self_or_nothing(fieldCall.getInstance());

        if(instance_type.toString().startsWith(ARRAY_TYPE)){
            if(fieldCall.getField().getName().equals("length")){
                return new IntType();
            }else{
                try {
                    if(!instance_type.toString().equals(UNDEFINED_TYPE) ) { // if not undefined
                        throw new InvalidOperationOperands(fieldCall.line, fieldCall.col, fieldCall.toString());
                    }
                }catch (InvalidOperationOperands io){
                    type_check = false;
                    io.emit_error_message();
                }
                return new UndefinedType();
            }
        }

        try { // instance type is class
            String class_name = ((UserDefinedType) instance_type).getClassDeclaration().getName().getName();
            ClassSymbolTableItem class_symbol_table = (ClassSymbolTableItem) SymbolTable.top().get(CLASS_PREFIX + class_name);
            try {
                FieldSymbolTableItem field = (FieldSymbolTableItem) class_symbol_table.getSymbolTable().get(VAR_PREFIX + fieldCall.getField().getName());
                if ( field.getAccessModifier().toString().equals(public_access) ||
                        ( is_subtype(class_name, current_class.getName().getName()) && flag ) ){
                    return field.getVarType();  // PUBLIC or (Private and subClass when not using new object)
                }else{
                    try{
                        throw new IllegalAccessToMember(fieldCall.line, fieldCall.col, class_name, "Field", field.getName());
                    }catch (IllegalAccessToMember ie){
                        type_check = false;
                        ie.emit_error_message();
                    }
                }
//                System.out.println("tested");
            }catch (Exception  exception){
                try{
                    throw new InvalidFieldCall(fieldCall.line, fieldCall.col, class_name, fieldCall.getField().getName());
                }catch (InvalidFieldCall ie){
                    type_check = false;
                    ie.emit_error_message();
                }
            }
        }
        catch (Exception e){
            //Error for when field call instance was not found or was not a class.
            try {
                if(!instance_type.toString().equals(UNDEFINED_TYPE) ) { // if not undefined
                    throw new InvalidOperationOperands(fieldCall.line, fieldCall.col, fieldCall.toString());
                }
            }catch (InvalidOperationOperands io){
                type_check = false;
                io.emit_error_message();
            }
        }

//        try {
//            if (fieldCall.getInstance().toString().equals("(Self)")) {
////                System.out.println("zzz");
////                System.out.println(current_class.getName().getName());
////                System.out.println(CLASS_PREFIX + current_class.getName().getName());
//                ClassSymbolTableItem class_symbol_table = (ClassSymbolTableItem) SymbolTable.top().get(CLASS_PREFIX + current_class.getName().getName());
////                System.out.println("test");
//                try {
//                    FieldSymbolTableItem field = (FieldSymbolTableItem) class_symbol_table.getSymbolTable().get(VAR_PREFIX + fieldCall.getField().getName());
////                    System.out.println(field.getVarType());
//                    return field.getVarType();
//                }
//                catch (Exception exception){}
//            }
//        }
//        catch (Exception exception){
//
//        }
        return new UndefinedType();
    }

    @Override
    public Type visit(ArrayCall arrayCall) {
        Type instance_type = arrayCall.getInstance().accept(this);
        Type index_type = arrayCall.getIndex().accept(this);
        if(instance_type.toString().equals(UNDEFINED_TYPE)){
            return new UndefinedType();
        }
        if(index_type.toString().equals(UNDEFINED_TYPE)){
            return instance_type;
        }
        if( instance_type.toString().startsWith("(ArrayType,") ){
            if(index_type.toString().equals(INT_TYPE) ){
                return instance_type;
            }else{
                //index is not int
                try{
                    throw new InvalidOperationOperands(arrayCall.line, arrayCall.col, arrayCall.toString());
                }catch (InvalidOperationOperands e){
                    type_check = false;
                    e.emit_error_message();
                }
            }
        }else{
            try{
                throw new InvalidOperationOperands(arrayCall.line, arrayCall.col, arrayCall.toString());
            }catch (InvalidOperationOperands e){
                type_check = false;
                e.emit_error_message();
            }
        }
        return new UndefinedType();
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
            type_check = false;
            exception.emit_error_message();
        }
        return new BoolType();
    }

    @Override
    public Type visit(ClassDeclaration classDeclaration) {
        current_class = classDeclaration;
        String class_name = classDeclaration.getName().getName();
        try{
            Collection<String> a = classHierarchy.getParentsOfNode(class_name);
            if(classHierarchy.getParentsOfNode(class_name).contains(class_name) == true){
                type_check = false;
                System.out.println("Error:Line:"+ classDeclaration.getName().line +":There is cycle in inheritance");
            }
        }catch (Exception e){

        }
        SymbolTable.pushFromQueue();
        for (ClassMemberDeclaration classMemberDeclaration: classDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        SymbolTable.pop();
        return new VoidType();
    }

    @Override
    public Type visit(EntryClassDeclaration entryClassDeclaration) {
        String class_name = entryClassDeclaration.getName().getName();
        try{

            Collection<String> a = classHierarchy.getParentsOfNode(class_name);
            if(classHierarchy.getParentsOfNode(class_name).contains(class_name) == true){
                type_check = false;
                System.out.println("Error:Line:"+ entryClassDeclaration.getName().line +":There is cycle in inheritance");
            }
        }catch (Exception e){

        }
        current_class = entryClassDeclaration;
        SymbolTable.pushFromQueue();

        try{
            MethodSymbolTableItem m = (MethodSymbolTableItem) SymbolTable.top().get("method_main");
        }catch (Exception e){
            System.out.println("Error: Entry class and it's ancestors has no main method");
            type_check = false;
        }

        for (ClassMemberDeclaration classMemberDeclaration: entryClassDeclaration.getClassMembers()){
            classMemberDeclaration.accept(this);
        }
        SymbolTable.pop();
        return new VoidType();
    }

    @Override
    public Type visit(FieldDeclaration fieldDeclaration) {

        if(!is_type_declared(fieldDeclaration.getType(), fieldDeclaration.line)){

        }

        return new VoidType();
    }

    @Override
    public Type visit(ParameterDeclaration parameterDeclaration) {
        var_index++;
        try {
//            System.out.println("1");
            Type t = parameterDeclaration.getType();
            is_type_declared(t, parameterDeclaration.line);
            SymbolTable s = SymbolTable.top();
            LocalVariableSymbolTableItem lvst = (LocalVariableSymbolTableItem) SymbolTable.top().get(VAR_PREFIX + parameterDeclaration.getIdentifier().getName());
            lvst.setInital_value(parameterDeclaration.getType());

//            System.out.println(lvst.getInital_value());

        }
        catch (Exception exception){
//            System.out.println("testing");
        }


        return new VoidType();
    }

    @Override
    public Type visit(MethodDeclaration methodDeclaration) {
//        System.out.println(methodDeclaration);
        method_return_type = methodDeclaration.getReturnType(); // setting return type of the method declaration

        var_index = 0;
        String type_name = methodDeclaration.getReturnType().toString();
        if(!is_type_declared(methodDeclaration.getReturnType() , methodDeclaration.getName().line)){

        }

        SymbolTable.pushFromQueue();

        for (ParameterDeclaration p: methodDeclaration.getArgs()) {
            p.accept(this);
        }

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
        return new VoidType();
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




    private  boolean is_type_declared(Type t, int line){
        String type_name = t.toString();
        String hard_type = t.toString();
        try {
            try{
                ArrayType fa = (ArrayType) t;
                hard_type = fa.getSingleType().toString();
            }catch (Exception e){
            }

            if (hard_type.equals(INT_TYPE) || hard_type.equals(BOOL_TYPE) || hard_type.equals(STR_TYPE))
                return true;
            int index_of_name = hard_type.indexOf(',');
            type_name = hard_type.substring(index_of_name + 1, hard_type.length() - 1);
            SymbolTable s = SymbolTable.top();
            SymbolTable.top().get("class_" + type_name);
            return true;
        }
        catch (Exception exception){
            System.out.println("Error:Line:" + line + ":" + "There is no type with name " + type_name);
            type_check = false;
            return false;
        }
    }

    private String standard_name(String n){
        int index;
        index = n.indexOf(',');
        if(index != -1) {
            n = n.substring(index + 1, n.length() - 1);
        }
        return n;
    }

}
