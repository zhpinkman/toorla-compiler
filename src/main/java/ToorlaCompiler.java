import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import toorla.ast.Program;
import toorla.nameAnalyzer.NameAnalyzer;
import toorla.symbolTable.SymbolTable;
import toorla.typeChecking.TypeChecking;

public class ToorlaCompiler {
    public void compile(CharStream textStream) {
        ToorlaLexer toorlaLexer = new ToorlaLexer(textStream);
        CommonTokenStream tokenStream = new CommonTokenStream(toorlaLexer);
        ToorlaParser toorlaParser = new ToorlaParser(tokenStream);
        Program toorlaASTCode = toorlaParser.program().mProgram;
        NameAnalyzer nameAnalyzer = new NameAnalyzer(toorlaASTCode);
        nameAnalyzer.analyze();

        TypeChecking tc = new TypeChecking(toorlaASTCode);
        if(tc.check())
            System.out.println("No error detected");



        //toorlaASTCode.type_check(SymbolTable.root);


    }
}
