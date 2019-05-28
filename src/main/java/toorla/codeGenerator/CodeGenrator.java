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
import toorla.typeChecker.ExpressionTypeExtractor;
import toorla.types.Type;
import toorla.types.arrayType.ArrayType;
import toorla.types.singleType.BoolType;
import toorla.types.singleType.IntType;
import toorla.types.singleType.StringType;
import toorla.types.singleType.UserDefinedType;
import toorla.utilities.graph.Graph;
import toorla.visitor.Visitor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CodeGenrator extends Visitor<Void> {

    static String INTEGER_TYPE = "I";
    static String STRING_TYPE = "Ljava/lang/String;";
    static String BOOL_TYPE = "Z";
    static String ARRAY_TYPE = "[";
    ExpressionTypeExtractor expressionTypeExtractor;
    public BufferedWriter writer;
    int tabs_before;

    public String get_type_code(Type param){
        if (param instanceof IntType)
            return  INTEGER_TYPE;
        else if (param instanceof StringType)
            return  STRING_TYPE;
        else if (param instanceof BoolType)
            return BOOL_TYPE;
//            else if (parameter.getType() instanceof UserDefinedType)
//                result += parameter.getType(). TODO handle package for the format of user define types
        else if (param instanceof ArrayType){
            ArrayType type = (toorla.types.arrayType.ArrayType) param;
            return  ARRAY_TYPE + get_type_code(type.getSingleType());
        }

    }

    public String get_args_code(ArrayList<ParameterDeclaration> args){
        String result = "";
        for (ParameterDeclaration parameter : args){
            result += get_type_code(parameter.getType());
        }
        return result;
    }

    public void append_limits(){
        append_command(".limits locals 10");
        append_command(".limits stack 100");
    }

    public void append_default_constructor(){
        append_command(".method public <init>()V\n" +
                "   aload_0 ; push this\n" +
                "   invokespecial java/lang/Object/<init>()V ; call super\n" +
                "   return\n" +
                ".end method");
    }

    public void create_class_file(String class_name){
        File file = new File("artifact/" + class_name + ".j");
        try{
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter("artifact/" + class_name + ".j", true));
            writer.write("zhivar");
        }
        catch (IOException se){
        }
    }


    public void create_directory(){
        File theDir = new File("artifact");
        try{
            theDir.mkdir();
        }
        catch(SecurityException se){
        }
    }

    public CodeGenrator(Graph<String> classHierarchy){
        expressionTypeExtractor = new ExpressionTypeExtractor(classHierarchy);
        tabs_before = 0;
        create_directory();
    }

    public void append_command(String command)
    {
        try {
            for (int i = 0;i < tabs_before; i++)
                writer.write("      ");
            writer.write(command);
            writer.newLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


    }

    public Void visit(Plus plusExpr) {
        plusExpr.getRhs().accept(this);
        plusExpr.getLhs().accept(this);

        append_command("iadd");

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
        Type type = printStat.getArg().accept(expressionTypeExtractor);
        append_command("getstatic java/lang/System/out Ljava/io/PrintStream");
        if ( type instanceof IntType) {
            printStat.getArg().accept(this);
            append_command("invokevirtual java/io/PrintStream/println(I)V");
        }
        else if (type instanceof StringType){
            printStat.getArg().accept(this);
            append_command("invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V");
        }
//        else{
//            printStat.getArg().
//        }

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
        create_class_file(classDeclaration.getName().getName());
        append_default_constructor();
        return null;
    }

    public Void visit(EntryClassDeclaration entryClassDeclaration) {
        create_class_file(entryClassDeclaration.getName().getName());
        append_default_constructor();

        return null;
    }

    public Void visit(FieldDeclaration fieldDeclaration) {
        return null;
    }

    public Void visit(ParameterDeclaration parameterDeclaration) {
        return null;
    }

    public Void visit(MethodDeclaration methodDeclaration) {
        String static_keyword = " ";
        if (methodDeclaration.getName().getName().equals("main"))
            static_keyword = " static";
        String arg_defs = get_args_code(methodDeclaration.getArgs());
        append_command(".method " + methodDeclaration.getAccessModifier().toString() + static_keyword + methodDeclaration.getName().getName() + "(" +
                arg_defs + ")" + get_type_code(methodDeclaration.getReturnType()));
        append_limits();
        return null;
    }

    public Void visit(LocalVarsDefinitions localVarsDefinitions) {
        return null;
    }

    public Void visit(Program program) {
        for (ClassDeclaration classDeclaration : program.getClasses()){
            classDeclaration.accept(this);
        }
        return null;
    }

}
