package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.pap.pml.CompiledPML;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationRuntimeException;
import gov.nist.csd.pm.pap.pml.expression.Expression;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationException;
import gov.nist.csd.pm.pap.pml.scope.PMLScopeException;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.statement.VariableDeclarationStatement;

import java.util.*;

public class PMLVisitor extends PMLBaseVisitor<CompiledPML> {

    public PMLVisitor(VisitorContext visitorCtx) {
        super(visitorCtx);
    }

    @Override
    public CompiledPML visitPml(PMLParser.PmlContext ctx) {
        SortedStatements sortedStatements = sortStatements(ctx);

        // create a separate visitor context for the constants and functions and compile them before anything else

        VisitorContext constAndFuncCtx = visitorCtx.copy();
        Map<String, Expression> consts = compilePersistedConstants(constAndFuncCtx, sortedStatements.constantCtxs);
        Map<String, FunctionDefinitionStatement> funcs = compilePersistedFunctions(constAndFuncCtx, sortedStatements.functionCtxs);


        Map<String, Variable> persistedConsts = constExpressionsToVariables(consts);
        persistedConsts.putAll(visitorCtx.scope().global().getPersistedConstants());

        Map<String, FunctionSignature> persistedFuncs = funcStmtsToSignatures(funcs);
        persistedFuncs.putAll(visitorCtx.scope().global().getPersistedFunctions());

        // add the persisted level constants and functions to the global scope
        visitorCtx.scope().global().setPersistedConstants(persistedConsts);
        visitorCtx.scope().global().setPersistedFunctions(persistedFuncs);

        return new CompiledPML(
                consts,
                funcs,
                compileStatements(sortedStatements.statementCtxs)
        );
    }

    private SortedStatements sortStatements(PMLParser.PmlContext ctx) {
        List<PMLParser.ConstDeclarationContext> constantCtxs = new ArrayList<>();
        List<PMLParser.FunctionDefinitionStatementContext> functionCtxs = new ArrayList<>();
        List<PMLParser.StatementContext> statementCtxs = new ArrayList<>();

        for (PMLParser.StatementContext stmtCtx : ctx.statement()) {
            if (stmtCtx.variableDeclarationStatement() != null) {
                if (stmtCtx.variableDeclarationStatement() instanceof PMLParser.ConstDeclarationContext) {
                    constantCtxs.add((PMLParser.ConstDeclarationContext) stmtCtx.variableDeclarationStatement());
                } else {
                    statementCtxs.add(stmtCtx);
                }
            } else if (stmtCtx.functionDefinitionStatement() != null) {
                functionCtxs.add(stmtCtx.functionDefinitionStatement());
            } else {
                statementCtxs.add(stmtCtx);
            }
        }

        return new SortedStatements(constantCtxs, functionCtxs, statementCtxs);
    }

    private record SortedStatements(List<PMLParser.ConstDeclarationContext> constantCtxs,
                                    List<PMLParser.FunctionDefinitionStatementContext> functionCtxs,
                                    List<PMLParser.StatementContext> statementCtxs) {}

    private Map<String, Variable> constExpressionsToVariables(Map<String, Expression> consts) {
        Map<String, Variable> persistedConsts = new HashMap<>();
        for (Map.Entry<String, Expression> e : consts.entrySet()) {
            try {
                persistedConsts.put(e.getKey(), new Variable(
                        e.getKey(),
                        e.getValue().getType(visitorCtx.scope()),
                        true
                ));
            } catch (PMLScopeException ex) {
                throw new PMLCompilationRuntimeException(ex);
            }
        }

        return persistedConsts;
    }

    private Map<String, FunctionSignature> funcStmtsToSignatures(Map<String, FunctionDefinitionStatement> funcs) {
        Map<String, FunctionSignature> signatures = new HashMap<>();
        for (FunctionDefinitionStatement f : funcs.values()) {
            signatures.put(f.getSignature().getFunctionName(), f.getSignature());
        }

        return signatures;
    }

    private Map<String, FunctionDefinitionStatement> compilePersistedFunctions(VisitorContext visitorCtx, List<PMLParser.FunctionDefinitionStatementContext> functionSignatureCtxs) {
        FunctionDefinitionVisitor.FunctionSignatureVisitor functionSignatureVisitor =
                new FunctionDefinitionVisitor.FunctionSignatureVisitor(visitorCtx);
        // initialize the function signatures map with any signature defined in the policy already

        Map<String, FunctionSignature> functionSignatures = new HashMap<>(visitorCtx.scope().global().getPersistedFunctions());
        // track the function definitions statements to be processed,
        // any function with an error won't be processed but execution will continue inorder to find anymore errors
        Map<String, PMLParser.FunctionDefinitionStatementContext> validFunctionDefs = new HashMap<>();

        for (PMLParser.FunctionDefinitionStatementContext functionDefinitionStatementContext : functionSignatureCtxs) {
            // visit the signature which will add to the scope, if an error occurs, log it and continue
            try {
                FunctionSignature signature = functionSignatureVisitor.visitFunctionSignature(
                        functionDefinitionStatementContext.functionSignature());

                // check that the function isn't already defined in the pml or global scope
                if (functionSignatures.containsKey(signature.getFunctionName())) {
                    visitorCtx.errorLog().addError(functionDefinitionStatementContext,
                            "function '" + signature.getFunctionName() + "' already defined in scope");
                    continue;
                }

                functionSignatures.put(signature.getFunctionName(), signature);
                validFunctionDefs.put(signature.getFunctionName(), functionDefinitionStatementContext);
            } catch (PMLCompilationRuntimeException e) {
                visitorCtx.errorLog().addErrors(e.getErrors());
            }
        }

        // store all function signatures for use in compiling function bodies
        visitorCtx.scope().global().setPersistedFunctions(functionSignatures);

        // compile function bodies
        FunctionDefinitionVisitor functionDefinitionVisitor = new FunctionDefinitionVisitor(visitorCtx);
        Map<String, FunctionDefinitionStatement> funcs = new HashMap<>();

        for (PMLParser.FunctionDefinitionStatementContext functionDefinitionStatementContext : validFunctionDefs.values()) {
            // visit the definition which will return the statement with body
            try {
                FunctionDefinitionStatement funcStmt =
                        functionDefinitionVisitor.visitFunctionDefinitionStatement(functionDefinitionStatementContext);

                funcs.put(funcStmt.getSignature().getFunctionName(), funcStmt);
            } catch (PMLCompilationRuntimeException e) {
                visitorCtx.errorLog().addErrors(e.getErrors());
            }
        }

        return funcs;
    }

    private Map<String, Expression> compilePersistedConstants(VisitorContext visitorCtx, List<PMLParser.ConstDeclarationContext> constantCtxs) {
        Map<String, Expression> vars = new HashMap<>();

        VarStmtVisitor varStmtVisitor = new VarStmtVisitor(visitorCtx);

        for (PMLParser.ConstDeclarationContext constantCtx : constantCtxs) {
            try {
                VariableDeclarationStatement constStmt = varStmtVisitor.visitConstDeclaration(constantCtx);

                for (VariableDeclarationStatement.Declaration declaration : constStmt.getDeclarations()) {
                    vars.put(declaration.id(), declaration.expression());
                }
            } catch (PMLCompilationRuntimeException e) {
                visitorCtx.errorLog().addErrors(e.getErrors());
            }
        }

        return vars;
    }

    private List<PMLStatement> compileStatements(List<PMLParser.StatementContext> statementCtxs) {
        List<PMLStatement> statements = new ArrayList<>();
        for (PMLParser.StatementContext stmtCtx : statementCtxs) {
            StatementVisitor statementVisitor = new StatementVisitor(visitorCtx);

            try {
                PMLStatement statement = statementVisitor.visitStatement(stmtCtx);
                statements.add(statement);
            } catch (PMLCompilationRuntimeException e) {
                visitorCtx.errorLog().addErrors(e.getErrors());
            }
        }

        return statements;
    }

}
