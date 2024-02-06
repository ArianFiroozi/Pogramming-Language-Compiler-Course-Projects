package main.visitor.codeGenerator;

import main.ast.node.Program;
import main.ast.node.declaration.*;
import main.ast.node.expression.*;
import main.ast.node.statement.*;
import main.ast.type.Type;
import main.ast.type.complexType.TradeType;
import main.compileError.CompileError;
import main.compileError.type.ConditionTypeNotBool;
import main.symbolTable.SymbolTable;
import main.symbolTable.itemException.ItemNotFoundException;
import main.symbolTable.symbolTableItems.*;
import main.visitor.Visitor;
import main.ast.node.expression.operators.BinaryOperator;
import main.ast.node.expression.values.*;
import main.ast.type.*;
import main.ast.type.primitiveType.*;
import main.compileError.*;
import main.compileError.type.UnsupportedOperandType;
import main.symbolTable.SymbolTable;
import main.symbolTable.itemException.ItemNotFoundException;
import main.symbolTable.symbolTableItems.FunctionItem;
import main.symbolTable.symbolTableItems.SymbolTableItem;
import main.symbolTable.symbolTableItems.VariableItem;
import main.visitor.*;
import main.ast.node.declaration.*;
import main.ast.type.complexType.TradeType;
import main.compileError.CompileError;
import main.compileError.name.*;
import main.symbolTable.SymbolTable;
import main.symbolTable.itemException.ItemAlreadyExistsException;
import main.symbolTable.itemException.ItemNotFoundException;
import main.symbolTable.symbolTableItems.*;
import main.visitor.Visitor;
import main.visitor.typeAnalyzer.*;

import java.util.ArrayList;


import java.io.*;

public class codeGenerator extends Visitor<String> {
//    You may use following items or add your own for handling typechecker
    TypeChecker expressionTypeChecker;
//    Graph<String> classHierarchy;
    private String outputPath;
    private FileWriter currentFile;
    private FunctionDeclaration currentMethod;

    public codeGenerator() {
//        this.classHierarchy = classHierarchy;

//        Uncomment below line to initialize your typechecker
        this.expressionTypeChecker = new TypeChecker(new ArrayList<CompileError>());

//        Call your type checker here!
//        ----------------------------
        this.prepareOutputFolder();

    }

    private void prepareOutputFolder() {
        this.outputPath = "output/";
        String jasminPath = "utilities/jarFiles/jasmin.jar";
        String listClassPath = "utilities/codeGenerationUtilityClasses/List.j";
        String fptrClassPath = "utilities/codeGenerationUtilityClasses/Fptr.j";
        try{
            File directory = new File(this.outputPath);
            File[] files = directory.listFiles();
            if(files != null)
                for (File file : files)
                    file.delete();
            directory.mkdir();
        }
        catch(SecurityException e) { }
        copyFile(jasminPath, this.outputPath + "jasmin.jar");
        copyFile(listClassPath, this.outputPath + "List.j");
        copyFile(fptrClassPath, this.outputPath + "Fptr.j");
    }

    private void copyFile(String toBeCopied, String toBePasted) {
        try {
            File readingFile = new File(toBeCopied);
            File writingFile = new File(toBePasted);
            InputStream readingFileStream = new FileInputStream(readingFile);
            OutputStream writingFileStream = new FileOutputStream(writingFile);
            byte[] buffer = new byte[1024];
            int readLength;
            while ((readLength = readingFileStream.read(buffer)) > 0)
                writingFileStream.write(buffer, 0, readLength);
            readingFileStream.close();
            writingFileStream.close();
        } catch (IOException e) { }
    }

    private void createFile(String name) {
        try {
            String path = this.outputPath + name + ".j";
            File file = new File(path);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(path);
            this.currentFile = fileWriter;
        } catch (IOException e) {}
    }

    private void addCommand(String command) {
        try {
            command = String.join("\n\t\t", command.split("\n"));
            if(command.startsWith("Label_"))
                this.currentFile.write("\t" + command + "\n");
            else if(command.startsWith("."))
                this.currentFile.write(command + "\n");
            else
                this.currentFile.write("\t\t" + command + "\n");
            System.out.println(command);
            this.currentFile.flush();
        } catch (IOException e) {}
    }

    private void addField(String name, String type)
    {
        addCommand(".field " + name + " " + type);
    }

    private String makeTypeSignature(Type t) {
        //todo
        if (t instanceof IntType) return "I";
        else if (t instanceof BoolType) return "Z";
        else if (t instanceof FloatType) return "F";
        else if (t instanceof StringType) return "[C";
        else return "V";
        // complex types unhandled
    }

    private String load_exp(Expression exp)
    {
//        if (exp.)
        return "";
    }

    @Override
    public String visit(Program program) {
        //todo
        createFile(program.toString());

        addCommand(".class public Program");
        addCommand(".super main/ast/node/Node");

        addField("vars", "Ljava/util/ArrayList");
        addField("inits", "Ljava/util/ArrayList");
        addField("starts", "Ljava/util/ArrayList");
        addField("functions", "Ljava/util/ArrayList");
        addField("programMainDeclaration", "Ljava/util/MainDeclaration"); // not sure about type

        SymbolTable.root = new SymbolTable();
        SymbolTable.push(SymbolTable.root);
        for (VarDeclaration varDeclaration : program.getVars())
            varDeclaration.accept(this);
        for (FunctionDeclaration functionDeclaration : program.getFunctions())
            functionDeclaration.accept(this);
        for (OnInitDeclaration onInitDeclaration : program.getInits())
            onInitDeclaration.accept(this);
        for (OnStartDeclaration onStartDeclaration : program.getStarts())
            onStartDeclaration.accept(this);
        program.getMain().accept(this);
        return null;
    }

    @Override
    public String visit(FunctionDeclaration methodDeclaration) {
        // todo
        return null;
    }

    @Override
    public String visit(Expression expression) {
        //todo
        if (varDeclaration.getRValue()!= null)
        {
            addCommand("istore/astore");
        }
        return null;
    }

    @Override
    public String visit(VarDeclaration varDeclaration) {
        //todo
        if (varDeclaration.getRValue()!= null)
        {
            addCommand("istore/astore");
        }
        return null;
    }

    @Override
    public String visit(AssignStmt assignmentStmt) {
        //todo
        return null;
    }

    // @Override
    // public String visit(BlockStmt blockStmt) {
    //     //todo
    //     return null;
    // }

    @Override
    public String visit(IfElseStmt conditionalStmt) {
        //todo
        return null;
    }

    @Override
    public String visit(MethodCall methodCallStmt) {
        //todo
        return null;
    }

    // @Override
    // public String visit(PrintStmt print) {
    //     //todo
    //     return null;
    // }

    @Override
    public String visit(ReturnStmt returnStmt) {
        Type type = returnStmt.getReturnedExpr().accept(expressionTypeChecker);
        if(type instanceof NullType) {
            addCommand("return");
        }
        else {
            // load the return exp ??

            returnStmt.getReturnedExpr().accept(this);

            addCommand("areturn");
            //todo add commands to return
        }
        return null;
    }

    @Override
    public String visit(NullValue nullValue) {
        String commands = "";
        //todo
        return commands;
    }

    @Override
    public String visit(IntValue intValue) {
        String commands = "";
        //todo
        return commands;
    }

    @Override
    public String visit(BoolValue boolValue) {
        String commands = "";
        //todo
        return commands;
    }

    @Override
    public String visit(StringValue stringValue) {
        String commands = "";
        //todo
        return commands;
    }

}