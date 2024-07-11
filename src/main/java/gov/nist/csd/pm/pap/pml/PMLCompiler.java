package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.op.Operation;
import gov.nist.csd.pm.pap.pml.antlr.PMLLexer;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.compiler.error.ErrorLog;
import gov.nist.csd.pm.pap.pml.compiler.visitor.PMLVisitor;
import gov.nist.csd.pm.pap.pml.scope.CompileGlobalScope;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PMLCompiler {

    protected Map<String, Operation<?>> functions;

    public PMLCompiler() {
        functions = new HashMap<>();
    }

    public PMLCompiler withFunctions(Collection<Operation<?>> funcs) {
        functions.clear();

        for (Operation<?> func : funcs) {
            functions.put(func.getName(), func);
        }

        return this;
    }

    public Map<String, Operation<?>> getFunctions() {
        return functions;
    }

    public CompiledPML compilePML(String input) throws PMException {
        ErrorLog errorLog = new ErrorLog();

        GlobalScope<Variable> globalScope = new CompileGlobalScope()
                .withFunctions(functions);

        PMLErrorHandler pmlErrorHandler = new PMLErrorHandler();

        PMLLexer lexer = new PMLLexer(CharStreams.fromString(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(pmlErrorHandler);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PMLParser parser = new PMLParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(pmlErrorHandler);

        // check for syntax errors
        PMLVisitor pmlVisitor = new PMLVisitor(new VisitorContext(tokens, new Scope<>(globalScope), errorLog, pmlErrorHandler));
        PMLParser.PmlContext pmlCtx = parser.pml();
        if (!pmlErrorHandler.getErrors().isEmpty()) {
            throw new PMLCompilationException(pmlErrorHandler.getErrors());
        }

        // check for semantic errors
        CompiledPML compiled = pmlVisitor.visitPml(pmlCtx);
        if (!errorLog.getErrors().isEmpty()) {
            throw new PMLCompilationException(errorLog.getErrors());
        }

        return compiled;
    }
}
