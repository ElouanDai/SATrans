/*
    Copyright 2024 CodeTranscenders
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package translator;

import org.eclipse.jdt.core.dom.*;
import Block.ActionBlock;
import Block.MyBlock;
import Block.StmtBlock;
import Block.ThreadBlock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

public class Main {
    private static final List<MyBlock> myBlockList = new ArrayList<>();
    private static final List<String> threadCLassNames = new ArrayList<>();
    private static final HashSet<String> lockSet = new LinkedHashSet<>();
    private static final HashSet<String> threadSet = new LinkedHashSet<>();

    public static boolean isThreadBlock(Statement stmt) {
        //只考虑完整的变量声明，如果只是fragment还要考虑ddg
        if (stmt instanceof VariableDeclarationStatement vdStmt) {
            Type type = vdStmt.getType();
            if (type instanceof SimpleType simpleType) {
                Name typeName = simpleType.getName();
                return typeName.getFullyQualifiedName().equals("Thread") || threadCLassNames.contains(type.toString());
            }
        }
        return false;
    }

    public static String isMoveBlock(Statement stmt) {
        ArrayList<String> moveList = new ArrayList<>();
        moveList.add("wait");
        moveList.add("notify");
        moveList.add("start");
        if (stmt instanceof TryStatement tryStatement) {
            List<Statement> tryStmts = tryStatement.getBody().statements();
            if (tryStmts.get(0) instanceof ExpressionStatement expressionStatement) {
                Expression exp = expressionStatement.getExpression();
                if (exp instanceof MethodInvocation methodInvocation) {
                    String string = methodInvocation.getExpression().toString();
                    String identifier = methodInvocation.getName().getIdentifier();
                    if (moveList.contains(identifier)) {
                        return identifier.toUpperCase() + " " + string;
                    }
                }
            }
        } else if (stmt instanceof ExpressionStatement) {
            Expression exp = ((ExpressionStatement) stmt).getExpression();
            if (exp instanceof MethodInvocation methodInvocation) {
                String string = methodInvocation.getExpression().toString();
                String identifier = methodInvocation.getName().getIdentifier();
                if (moveList.contains(identifier)) {
                    return identifier.toUpperCase() + " " + string;
                }
            }
        }
        return null;
    }

    public static ThreadBlock threadName2Block(String name) {
        for (MyBlock myBlock : myBlockList) {
            if (myBlock instanceof ThreadBlock threadBlock && threadBlock.getThreadName().equals(name))
                return threadBlock;
        }
        throw new RuntimeException("ThreadBlock not found");
    }

    private static String readSourceFromFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        //input file 绝对路径
        String source = readSourceFromFile("C:\\Users\\legion\\Desktop\\ArkTS源码转换器\\CodeTranscenders\\src\\main\\java\\testfile\\MultipleThread2.java");
        assert source != null;
        parser.setSource(source.toCharArray());
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        List<TypeDeclaration> types = cu.types();
        for (TypeDeclaration typeDeclaration : types) {
            if (typeDeclaration.getSuperclassType() != null && typeDeclaration.getSuperclassType().toString().equals("Thread")) {
                threadCLassNames.add(typeDeclaration.getName().toString());
//                classes.add(typeDeclaration);
            } else if (typeDeclaration.superInterfaceTypes().size() != 0 && typeDeclaration.superInterfaceTypes().get(0).toString().equals("Runnable")) {
                threadCLassNames.add(typeDeclaration.getName().toString());
//                classes.add(typeDeclaration);
            }
        }
        System.out.println("==语法树解析中==");
        cu.accept(new ASTVisitor() {
            //            public boolean visit(Block node) {
//                System.out.println("====Block start=====");
//                System.out.println(node);
//                System.out.println("====Block end=====");
//                return true;
//            }
//
            //假设main方法里一定有thread声明/thread操作
            public boolean visit(MethodDeclaration node) {
//                System.out.println("====Method start=====");
//                System.out.println("Method: " + node.getName());
                if (node.getName().getIdentifier().equals("main")) {
                    Block mainBlock = node.getBody();
                    List<Statement> statements = mainBlock.statements();
                    int last = 0;
                    for (int i = 0; i < statements.size(); i++) {
                        Statement statement = statements.get(i);
                        if (isThreadBlock(statement)) {
                            List<Statement> subList = statements.subList(last, i);
                            last = i + 1;
                            if (!subList.isEmpty()) {
                                myBlockList.add(new StmtBlock(subList));
                            }
                            List<VariableDeclarationFragment> fragments = ((VariableDeclarationStatement) statement).fragments();
                            myBlockList.add(new ThreadBlock(fragments.get(0).getName().getIdentifier()));
                            threadSet.add(fragments.get(0).getName().getIdentifier());
                        } else if (isMoveBlock(statement) != null) {
                            String moveName = isMoveBlock(statement);
                            List<Statement> subList = statements.subList(last, i);
                            last = i + 1;
                            if (!subList.isEmpty()) {
                                myBlockList.add(new StmtBlock(subList));
                            }
                            myBlockList.add(new ActionBlock(moveName));
                        }
                    }
                    if (last < statements.size()) {
                        List<Statement> subList = statements.subList(last, statements.size());
                        if (!subList.isEmpty()) {
                            myBlockList.add(new StmtBlock(subList));
                        }
                    }
                }
//                System.out.println("====Method end=====");
                return true;
            }
//
//            public boolean visit(TypeDeclaration node) {
//                System.out.println("====type start=====");//类或接口的声明
//                List list = node.bodyDeclarations();        //类的成员变量/方法声明
//                for(Object o:list)
//                    System.out.println(o.getClass());
//                System.out.println("====type end=====");
//                return true;
//            }
            //
//            public boolean visit(ClassInstanceCreation node) {
//                System.out.println("====ClassInstanceCreation start=====");///类的创建实例
//                System.out.println("class: " + node);
//                Type type = node.getType();
//                System.out.println("====ClassInstanceCreation end=====");
//                return true;
//            }

            public boolean visit(VariableDeclarationStatement node) {
//                System.out.println("====Variable start=====\n");
//                Type type = node.getType();
//                if (type instanceof SimpleType simpleType) {
//                    String className = simpleType.getName().getFullyQualifiedName();
//                          if (className.equals("Thread"))
                // 以上可以用isThread替代
                if (isThreadBlock(node)) {
                    List<VariableDeclarationFragment> fragments = node.fragments();
                    for (VariableDeclarationFragment fragment : fragments) {       //一条语句声明多个变量
                        ThreadBlock threadBlock = threadName2Block(fragment.getName().getIdentifier());
                        List<MyBlock> threadBlocks = new ArrayList<>();
                        Expression initializer = fragment.getInitializer();
                        if (initializer instanceof ClassInstanceCreation classInstanceCreation) {
                            String name = classInstanceCreation.getType().toString();
                            if (name.equals("Thread")) {
                                List<Expression> arguments = classInstanceCreation.arguments();
                                for (Expression argument : arguments) {
                                    if (argument instanceof LambdaExpression exp)       // ()->
                                    {
                                        Block block = (Block) exp.getBody();
                                        splitBlock(block, threadBlocks);
                                    } else if (argument instanceof ClassInstanceCreation instance) // Runnable接口
                                    {
                                        System.out.println("implement Runnable");
                                        String RunnableName = instance.getType().toString();
                                        for (TypeDeclaration typeDeclaration : types) {
                                            if (typeDeclaration.getName().toString().equals(RunnableName)) {
                                                MethodDeclaration[] methods = typeDeclaration.getMethods();
                                                for (MethodDeclaration methodDeclaration : methods) {
                                                    if (methodDeclaration.getName().toString().equals("run")) {
                                                        Block block = methodDeclaration.getBody();
                                                        splitBlock(block, threadBlocks);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else        //继承Thread
                            {
                                System.out.println("extend Thread");
                                for (TypeDeclaration typeDeclaration : types) {
                                    if (typeDeclaration.getName().toString().equals(name)) {
                                        MethodDeclaration[] methods = typeDeclaration.getMethods();
                                        for (MethodDeclaration methodDeclaration : methods) {
                                            if (methodDeclaration.getName().toString().equals("run")) {
                                                Block block = methodDeclaration.getBody();
                                                splitBlock(block, threadBlocks);
                                            }
                                        }
                                    }

                                }
                            }
                        }
                        threadBlock.setBlocks(threadBlocks);
                    }
                }
                return true;
            }
//
//            public boolean visit(VariableDeclarationFragment node) {            //变量声明片段
//                System.out.println("Variable: " + node.getName()+" "+node.getInitializer());
//                SimpleName name = node.getName();
//                Expression initializer = node.getInitializer();
//                if(initializer instanceof MethodInvocation methodInvocation)
//                {
//                    String methodName = methodInvocation.getName().getIdentifier();
//                    System.out.println(methodName);
//                }
//                else if (initializer instanceof ClassInstanceCreation classInstanceCreation)
//                {
//                    System.out.println(classInstanceCreation.arguments());
//                }
//                return true;
//            }
//

//
//            public boolean visit(VariableDeclarationExpression node) {
//                System.out.println("exp: " + node);
//                return true;
//            }
        });
        System.out.println("==中间状态生成中==");
        setBlockId();

//        printMyBlock();

//        System.out.println("lockNum="+lockSet.size());
        System.out.println("==中间状态翻译中==");
        Translator translator = new Translator(myBlockList);
        translator.interpret();
        System.out.println("ArkTS代码已生成在output文件夹下");
    }

    public static void printMyBlock() {
        for (MyBlock myBlock : myBlockList) {
            if (myBlock instanceof StmtBlock stmtBlock) {
                System.out.println(stmtBlock+" "+stmtBlock.getId());
            } else if (myBlock instanceof ThreadBlock threadBlock) {
                System.out.println("ThreadBlock: "+threadBlock.getId()+" "+threadBlock.getThreadId());
                List<MyBlock> blocks = threadBlock.getBlocks();
                for (MyBlock block : blocks) {
                    System.out.print("    ");
                    System.out.println(block+" "+block.getId());
                }
            } else if (myBlock instanceof ActionBlock actionBlock) {
                System.out.println(actionBlock +" "+ actionBlock.getId());
            }
        }
    }

    public static void splitBlock(Block block, List<MyBlock> threadBlocks) {
        List<Statement> statements = block.statements();
        int last = 0;
        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i) instanceof SynchronizedStatement synStatement) {
                Expression lock = synStatement.getExpression();
                lockSet.add(lock.toString());
                List<Statement> subList = statements.subList(last, i);
                last = i + 1;
                if (!subList.isEmpty()) {
                    threadBlocks.add(new StmtBlock(subList));
                }
                threadBlocks.add(new ActionBlock("ACQ " + lock));
                List<Statement> statements1 = synStatement.getBody().statements();
                int last1 = 0;
                for (int j = 0; j < statements1.size(); j++) {
                    Statement statement = statements1.get(j);
                    if (isMoveBlock(statement) != null) {
                        String moveName = isMoveBlock(statement);
                        List<Statement> subList1 = statements1.subList(last1, j);
                        last1 = j + 1;
                        if (!subList1.isEmpty()) {
                            threadBlocks.add(new StmtBlock(subList1));
                        }
                        threadBlocks.add(new ActionBlock(moveName));
                    }
                }
                if (last1 < statements1.size()) {
                    List<Statement> subList1 = statements1.subList(last1, statements1.size());
                    if (!subList1.isEmpty()) {
                        threadBlocks.add(new StmtBlock(subList1));
                    }
                }
                threadBlocks.add(new ActionBlock("REL " + lock));
            }
        }
        if (last < statements.size()) {
            List<Statement> subList = statements.subList(last, statements.size());
            threadBlocks.add(new StmtBlock(subList));
        }
    }

    public static void setBlockId() {
        int blockId = 1;
        int threadId = 1;
        for (MyBlock myBlock : myBlockList) {
            if (myBlock instanceof ThreadBlock threadBlock) {
                int threadBlockId = 1;
                threadBlock.setId(String.valueOf(blockId));         //父类myBlock的id
                threadBlock.setThreadId(String.valueOf(threadId));  //子类threadBlock的id
                List<MyBlock> blocks = threadBlock.getBlocks();
                for (MyBlock block : blocks) {
                    block.setId(blockId + "_" + threadBlockId);
                    threadBlockId++;
                }
                threadId++;
            }
            else
                myBlock.setId(String.valueOf(blockId));
            blockId++;
        }
    }

    public static int getThreadNum() {
        return threadSet.size();
    }

    public static String getThreadIndex(String threadName) {
        int index = 1;
        for (String thread : threadSet) {
            if (thread.equals(threadName))
                return String.valueOf(index);
            index++;
        }
        return "-1";
    }

    public static int getLockNum() {
        return lockSet.size();
    }

    public static String getLockIndex(String lockName) {
        int index = 1;
        for (String lock : lockSet) {
            if (lock.equals(lockName))
                return String.valueOf(index);
            index++;
        }
        return "-1";
    }
}
