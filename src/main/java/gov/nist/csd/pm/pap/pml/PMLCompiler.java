package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.common.exception.PMException;
import gov.nist.csd.pm.pap.pml.antlr.PMLLexer;
import gov.nist.csd.pm.pap.pml.antlr.PMLParser;
import gov.nist.csd.pm.pap.pml.compiler.Variable;
import gov.nist.csd.pm.pap.pml.compiler.error.ErrorLog;
import gov.nist.csd.pm.pap.pml.compiler.visitor.PMLVisitor;
import gov.nist.csd.pm.pap.pml.function.FunctionSignature;
import gov.nist.csd.pm.pap.pml.scope.CompileGlobalScope;
import gov.nist.csd.pm.pap.pml.scope.GlobalScope;
import gov.nist.csd.pm.pap.pml.context.VisitorContext;
import gov.nist.csd.pm.pap.pml.exception.PMLCompilationException;
import gov.nist.csd.pm.pap.pml.scope.Scope;
import gov.nist.csd.pm.pap.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.pap.pml.value.Value;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.HashMap;
import java.util.Map;

public class PMLCompiler {

    protected Map<String, Value> constants;
    protected Map<String, FunctionDefinitionStatement> functions;

    public PMLCompiler() {
        constants = new HashMap<>();
        functions = new HashMap<>();
    }

    public PMLCompiler addConstant(String name, Value value) {
        constants.put(name, value);
        return this;
    }

    public PMLCompiler addFunction(String name, FunctionDefinitionStatement functionDefinitionStatement) {
        functions.put(name, functionDefinitionStatement);
        return this;
    }

    public Map<String, Value> getConstants() {
        return constants;
    }

    public void setConstants(Map<String, Value> constants) {
        this.constants = constants;
    }

    public Map<String, FunctionDefinitionStatement> getFunctions() {
        return functions;
    }

    public void setFunctions(
            Map<String, FunctionDefinitionStatement> functions) {
        this.functions = functions;
    }

    public CompiledPML compilePML(String input) throws PMException {
        ErrorLog errorLog = new ErrorLog();

        Map<String, Variable> constantVars = constantsToVariables();
        Map<String, FunctionSignature> functionSigs = functionsToSignatures();

        GlobalScope<Variable, FunctionSignature> globalScope = new CompileGlobalScope()
                .withProvidedConstants(constantVars)
                .withProvidedFunctions(functionSigs);

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

    private Map<String, FunctionSignature> functionsToSignatures() {
        Map<String, FunctionSignature> sigs = new HashMap<>();

        for (Map.Entry<String, FunctionDefinitionStatement> e : functions.entrySet()) {
            sigs.put(e.getKey(), e.getValue().getSignature());
        }

        return sigs;
    }

    private Map<String, Variable> constantsToVariables() {
        Map<String, Variable> vars = new HashMap<>();

        for (Map.Entry<String, Value> e : constants.entrySet()) {
            String key = e.getKey();
            Value value = e.getValue();
            vars.put(e.getKey(), new Variable(key, value.getType(), true));
        }

        return vars;
    }
}
