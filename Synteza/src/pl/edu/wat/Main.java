package pl.edu.wat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


import javax.tools.*;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {

        ParserConfiguration pconfig = new ParserConfiguration();
        pconfig.setAttributeComments(false);
        JavaParser.setStaticConfiguration(pconfig);
        final String fileName = "src\\Class.java";
        final String alteredFileName = "src\\ClassAltered.java";
        CompilationUnit cu;
        try(FileInputStream in = new FileInputStream(fileName)){
           cu = JavaParser.parse(in);
        }

        new Rewriter().visit(cu, null);
        cu.getClassByName("Class").get().setName("ClassAltered");

        try(FileWriter output = new FileWriter(new File(alteredFileName), false)) {
            output.write(cu.toString());
        }

        File[] files = {new File(alteredFileName)};
        String[] options = { "-d", "out//production//Synthesis" };

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
            compiler.getTask(
                null,
                fileManager,
                diagnostics,
                Arrays.asList(options),
                null,
                compilationUnits).call();

            diagnostics.getDiagnostics().forEach(d -> System.out.println(d.getMessage(null)));
        }
    }

    private static int sprawdzam(MethodDeclaration method){
        int a=0;
        Optional<BlockStmt> body = method.getBody();
        String stat = body.toString();
        if(!stat.contains("return")|| !body.isPresent())
        {
            a=1;
        }

        return a;
    }
    private static BlockStmt GetMethodStmt(MethodDeclaration method){
        BlockStmt block;
        method.removeBody();
        block = new BlockStmt();
        method.setBody(block);
        return block;
    }

    private static class Rewriter extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            String methodName = n.getNameAsString();
            Type methodType = n.getType();
            String s = methodType.toString();
            int spr = sprawdzam(n);
            if (spr==0 || s.contains("void") ) {
                return;
            }
            BlockStmt block = GetMethodStmt(n);
            NameExpr f = new NameExpr("throw new java");
            FieldAccessExpr f2= new FieldAccessExpr(f,"lang");
            FieldAccessExpr f3= new FieldAccessExpr(f2, "UnsupportedOperationException(\""+methodName+"\")");
            block.addStatement(f3);
        }
    }
}
