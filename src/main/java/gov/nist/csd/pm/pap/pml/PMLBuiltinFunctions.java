package gov.nist.csd.pm.pap.pml;

import gov.nist.csd.pm.pap.pml.function.builtin.*;
import gov.nist.csd.pm.pap.pml.pattern.*;

import java.util.HashMap;
import java.util.Map;

public class PMLBuiltinFunctions {

    // util functions
    private static final Concat concat = new Concat();
    private static final Equals equals = new Equals();
    private static final Contains contains = new Contains();
    private static final ContainsKey containsKey = new ContainsKey();

    // policy functions
    private static final GetAssociationsWithSource getAssociationsWithSource = new GetAssociationsWithSource();
    private static final GetAssociationsWithTarget getAssociationsWithTarget = new GetAssociationsWithTarget();
    private static final GetAdjacentAscendants getAdjacentAscendants = new GetAdjacentAscendants();
    private static final GetAdjacentDescendants getAdjacentDescendants = new GetAdjacentDescendants();
    private static final GetNodeProperties getNodeProperties = new GetNodeProperties();
    private static final GetNodeType getNodeType = new GetNodeType();
    private static final GetProhibitionsFor getProhibitionsFor = new GetProhibitionsFor();
    private static final HasPropertyKey hasPropertyKey = new HasPropertyKey();
    private static final HasPropertyValue hasPropertyValue = new HasPropertyValue();
    private static final NodeExists nodeExists = new NodeExists();
    private static final GetNode getNode = new GetNode();
    private static final Search search = new Search();
    private static final Append append = new Append();
    private static final AppendAll appendAll = new AppendAll();
    private static final PcTargetName pcTargetName = new PcTargetName();

    private static final Map<String, PMLFunction<?>> BUILTIN_FUNCTIONS = new HashMap<>();

    static {
        BUILTIN_FUNCTIONS.put(concat.getOpName(), concat);
        BUILTIN_FUNCTIONS.put(equals.getOpName(), equals);
        BUILTIN_FUNCTIONS.put(contains.getOpName(), contains);
        BUILTIN_FUNCTIONS.put(containsKey.getOpName(), containsKey);
        BUILTIN_FUNCTIONS.put(appendAll.getOpName(), appendAll);
        BUILTIN_FUNCTIONS.put(append.getOpName(), append);

        BUILTIN_FUNCTIONS.put(getAssociationsWithSource.getOpName(), getAssociationsWithSource);
        BUILTIN_FUNCTIONS.put(getAssociationsWithTarget.getOpName(), getAssociationsWithTarget);
        BUILTIN_FUNCTIONS.put(getAdjacentAscendants.getOpName(), getAdjacentAscendants);
        BUILTIN_FUNCTIONS.put(getAdjacentDescendants.getOpName(), getAdjacentDescendants);
        BUILTIN_FUNCTIONS.put(getNodeProperties.getOpName(), getNodeProperties);
        BUILTIN_FUNCTIONS.put(getNodeType.getOpName(), getNodeType);
        BUILTIN_FUNCTIONS.put(getProhibitionsFor.getOpName(), getProhibitionsFor);
        BUILTIN_FUNCTIONS.put(hasPropertyKey.getOpName(), hasPropertyKey);
        BUILTIN_FUNCTIONS.put(hasPropertyValue.getOpName(), hasPropertyValue);
        BUILTIN_FUNCTIONS.put(nodeExists.getOpName(), nodeExists);
        BUILTIN_FUNCTIONS.put(getNode.getOpName(), getNode);
        BUILTIN_FUNCTIONS.put(search.getOpName(), search);
        BUILTIN_FUNCTIONS.put(pcTargetName.getOpName(), pcTargetName);
    }

    public static Map<String, PMLFunction<?>> builtinFunctions() {
        return new HashMap<>(BUILTIN_FUNCTIONS);
    }

    public static boolean isBuiltinFunction(String functionName) {
        return BUILTIN_FUNCTIONS.containsKey(functionName);
    }

    private PMLBuiltinFunctions() {}
}
