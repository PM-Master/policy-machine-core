package gov.nist.csd.pm.pap.pml.compiler.visitor;

import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationRuntimeException;
import gov.nist.csd.pm.pap.pml.function.PMLOperation;
import gov.nist.csd.pm.pap.pml.function.PMLRequiredCapability;
import gov.nist.csd.pm.pap.pml.function.PMLRoutine;
import gov.nist.csd.pm.pap.pml.function.*;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.statement.PMLStatement;
import gov.nist.csd.pm.pap.pml.statement.PMLStatementSerializer;
import gov.nist.csd.pm.pap.pml.statement.operation.CreateOperationStatement;
import gov.nist.csd.pm.pap.pml.statement.operation.CreateRoutineStatement;
import gov.nist.csd.pm.pap.pml.type.Type;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;

public class FunctionDefinitionVisitor extends PMLBaseVisitor<CreateOperationStatement> {

    public FunctionDefinitionVisitor(VisitorContext visitorCtx) {
        super(visitorCtx);
    }

    @Override
    public CreateOperationStatement visitFunctionDefinitionStatement(PMLParser.FunctionDefinitionStatementContext ctx) {
        PMLParser.FunctionSignatureContext functionSignatureContext = ctx.functionSignature();
        boolean isOp = functionSignatureContext.OPERATION() != null;

        FunctionSignature signature = new FunctionSignatureVisitor(visitorCtx, isOp).visitFunctionSignature(functionSignatureContext);

        List<PMLStatement> body = parseBody(ctx, signature.getCapMap(), signature.getReturnType());

        // check if the function is an operation
        if (isOp) {
            return new CreateOperationStatement(new PMLOperation(signature.getFunctionName(), signature.getCapMap(), body));
        } else {
            return new CreateRoutineStatement(new PMLRoutine(signature.getFunctionName(), signature.getCapMap(), body));
        }
    }

    private List<PMLStatement> parseBody(PMLParser.FunctionDefinitionStatementContext ctx,
                                                      Map<String, PMLRequiredCapability> args,
                                                      Type returnType) {
        // create a new scope for the function body
        VisitorContext localVisitorCtx = visitorCtx.copy();

        // add the args to the local scope, overwriting any variables with the same ID as the formal args
        for (Map.Entry<String, PMLRequiredCapability> entry : args.entrySet()) {
            String key = entry.getKey();
            PMLRequiredCapability cap = entry.getValue();

            localVisitorCtx.scope().addOrOverwriteVariable(
                    key,
                    new Variable(key, cap.type(), false)
            );
        }

        StatementBlockVisitor statementBlockVisitor = new StatementBlockVisitor(localVisitorCtx, returnType);
        StatementBlockVisitor.Result result = statementBlockVisitor.visitStatementBlock(ctx.statementBlock());

        if (!result.allPathsReturned() && !returnType.isVoid()) {
            throw new PMLCompilationRuntimeException(ctx, "not all conditional paths return");
        }

        return result.stmts();
    }

    public static class FunctionSignatureVisitor extends PMLBaseVisitor<PMLStatementSerializer> {

        private boolean isOp;

        public FunctionSignatureVisitor(VisitorContext visitorCtx, boolean isOp) {
            super(visitorCtx);

            this.isOp = isOp;
        }

        @Override
        public FunctionSignature visitFunctionSignature(PMLParser.FunctionSignatureContext ctx) {
            String funcName = ctx.ID().getText();
            Map<String, PMLRequiredCapability> args = parseFormalArgs(ctx.formalArgList());

            Type returnType = parseReturnType(ctx.returnType);

            return new FunctionSignature(isOp, funcName, returnType, args);
        }

        private Map<String, PMLRequiredCapability> parseFormalArgs(PMLParser.FormalArgListContext formalArgListCtx) {
            Map<String, PMLRequiredCapability> formalArgs = new HashMap<>();
            Set<String> argNames = new HashSet<>();
            for (int i = 0; i < formalArgListCtx.formalArg().size(); i++) {
                PMLParser.FormalArgContext formalArgCtx = formalArgListCtx.formalArg().get(i);
                String name = formalArgCtx.ID().getText();

                // check that two formal args dont have the same name and that there are no constants with the same name
                if (argNames.contains(name)) {
                    throw new PMLCompilationRuntimeException(
                            formalArgCtx,
                            String.format("formal arg '%s' already defined in signature", name)
                    );
                } else if (visitorCtx.scope().variableExists(name)) {
                    throw new PMLCompilationRuntimeException(
                            formalArgCtx,
                            String.format("formal arg '%s' already defined as a constant in scope", name)
                    );
                }

                // get arg type
                PMLParser.VariableTypeContext varTypeContext = formalArgCtx.variableType();
                Type type = Type.toType(varTypeContext);

                // req cap if operation
                PMLParser.OpReqCapContext opReqCapContext = formalArgCtx.opReqCap();
                if(opReqCapContext != null) {
                    List<String> reqCaps = new ArrayList<>();

                    if (opReqCapContext.ID() != null) {
                        reqCaps.add(opReqCapContext.ID().getText());
                    } else if (opReqCapContext.idArr() != null && !opReqCapContext.idArr().isEmpty()){
                        List<TerminalNode> id = opReqCapContext.idArr().ID();
                        for (TerminalNode terminalNode : id) {
                            reqCaps.add(terminalNode.getText());
                        }
                    }

                    formalArgs.put(name, new PMLRequiredCapability(i, type, reqCaps));
                } else {
                    formalArgs.put(name, new PMLRequiredCapability(i, type));
                }

                argNames.add(name);
            }

            return formalArgs;
        }

        private Type parseReturnType(PMLParser.VariableTypeContext variableTypeContext) {
            if (variableTypeContext == null) {
                return Type.voidType();
            }

            return Type.toType(variableTypeContext);
        }
    }
}
