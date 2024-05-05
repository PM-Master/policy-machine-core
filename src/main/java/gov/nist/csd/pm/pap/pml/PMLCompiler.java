package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.pap.Policy;
import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.antlr.PMLLexer;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.compiler.error.ErrorLog;
import gov.nist.csd.pm.pap.pml.compiler.visitor.PMLVisitor;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class PMLCompiler {

    public static CompiledPML compilePML(Policy policy, String input, FunctionDefinitionStatement... customBuiltinFunctions) throws PMException {
        ErrorLog errorLog = new ErrorLog();

        GlobalScope<Variable, FunctionSignature> globalScope = GlobalScope.forCompile(policy, customBuiltinFunctions);

        PMLErrorHandler pmlErrorHandler = new PMLErrorHandler();

        PMLLexer lexer = new PMLLexer(CharStreams.fromString(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(pmlErrorHandler);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PMLParser parser = new PMLParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(pmlErrorHandler);

        PMLVisitor pmlVisitor = new PMLVisitor(new VisitorContext(tokens, new Scope<>(globalScope), errorLog));
        CompiledPML compiled = null;
        try {
            compiled = pmlVisitor.visitPml(parser.pml());
        } catch (Exception e) {
            errorLog.addError(parser.pml(), e.getMessage());
        }

        errorLog.addErrors(pmlErrorHandler.getErrors());

        // throw an exception if there are any errors from parsing
        if (!errorLog.getErrors().isEmpty()) {
            throw new PMLCompilationException(errorLog);
        }

        return compiled;
    }
}
