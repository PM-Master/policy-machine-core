package gov.nist.csd.pm.impl.neo4j;

import gov.nist.csd.pm.pap.UserDefinedPMLStore;
import gov.nist.csd.pm.policy.exceptions.*;
import gov.nist.csd.pm.policy.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.policy.pml.value.Value;
import org.apache.commons.lang3.SerializationUtils;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

import java.util.HashMap;
import java.util.Map;

import static gov.nist.csd.pm.impl.neo4j.Neo4jGraphStore.NAME_PROPERTY;
import static gov.nist.csd.pm.impl.neo4j.Neo4jObligationsStore.BYTES_PROPERTY;

public class Neo4jUserDefinedPMLStore implements UserDefinedPMLStore {

    public static final Label PML_FUNC_LABEL = Label.label("PML_FUNC");
    public static final Label PML_CONST_LABEL = Label.label("PML_CONST");

    private Neo4jConnection neo4j;

    public Neo4jUserDefinedPMLStore(Neo4jConnection neo4j) {
        this.neo4j = neo4j;
    }

    @Override
    public void createFunction(FunctionDefinitionStatement functionDefinitionStatement)
            throws PMLFunctionAlreadyDefinedException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                checkCreateFunctionInput(functionDefinitionStatement.getSignature().getFunctionName());

                Node node = tx.createNode(PML_FUNC_LABEL);
                node.setProperty(NAME_PROPERTY, functionDefinitionStatement.getSignature().getFunctionName());
                node.setProperty(BYTES_PROPERTY, SerializationUtils.serialize(functionDefinitionStatement));
            });
        } catch (PMLFunctionAlreadyDefinedException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void deleteFunction(String functionName) throws PMBackendException {
        try {
            neo4j.runTx(tx -> {
                if (!checkDeleteFunctionInput(functionName)) {
                    return;
                }

                Node node = tx.findNode(PML_FUNC_LABEL, NAME_PROPERTY, functionName);
                node.delete();
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public Map<String, FunctionDefinitionStatement> getFunctions() throws PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                try(ResourceIterator<Node> iter = tx.findNodes(PML_FUNC_LABEL)) {
                    Map<String, FunctionDefinitionStatement> map = new HashMap<>();

                    while (iter.hasNext()) {
                        Node node = iter.next();
                        String name = String.valueOf(node.getProperty(NAME_PROPERTY));
                        FunctionDefinitionStatement func = SerializationUtils.deserialize((byte[]) node.getProperty(BYTES_PROPERTY));

                        map.put(name, func);
                    }

                    return map;
                }
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public FunctionDefinitionStatement getFunction(String name)
            throws PMLFunctionNotDefinedException, PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                checkGetFunctionInput(name);

                Node node = tx.findNode(PML_FUNC_LABEL, NAME_PROPERTY, name);
                return SerializationUtils.deserialize((byte[]) node.getProperty(BYTES_PROPERTY));
            });
        } catch (PMLFunctionNotDefinedException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void createConstant(String constantName, Value constantValue)
            throws PMLConstantAlreadyDefinedException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                checkCreateConstantInput(constantName);

                Node node = tx.createNode(PML_CONST_LABEL);
                node.setProperty(NAME_PROPERTY, constantName);
                node.setProperty(BYTES_PROPERTY, SerializationUtils.serialize(constantValue));
            });
        } catch (PMLConstantAlreadyDefinedException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void deleteConstant(String constName) throws PMBackendException {
        try {
            neo4j.runTx(tx -> {
                if (!checkDeleteConstantInput(constName)) {
                    return;
                }

                Node node = tx.findNode(PML_CONST_LABEL, NAME_PROPERTY, constName);
                node.delete();
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public Map<String, Value> getConstants() throws PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                try(ResourceIterator<Node> iter = tx.findNodes(PML_CONST_LABEL)) {
                    Map<String, Value> map = new HashMap<>();

                    while (iter.hasNext()) {
                        Node node = iter.next();
                        String name = String.valueOf(node.getProperty(NAME_PROPERTY));
                        Value value = SerializationUtils.deserialize((byte[]) node.getProperty(BYTES_PROPERTY));

                        map.put(name, value);
                    }

                    return map;
                }
            });
        } catch (PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public Value getConstant(String name) throws PMLConstantNotDefinedException, PMBackendException {
        try {
            return neo4j.runTx(tx -> {
                checkGetConstantInput(name);

                Node node = tx.findNode(PML_CONST_LABEL, NAME_PROPERTY, name);
                return SerializationUtils.deserialize((byte[]) node.getProperty(BYTES_PROPERTY));
            });
        } catch (PMLConstantNotDefinedException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void checkCreateFunctionInput(String name) throws PMLFunctionAlreadyDefinedException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                Node node = tx.findNode(PML_FUNC_LABEL, NAME_PROPERTY, name);
                if (node != null) {
                    throw new PMLFunctionAlreadyDefinedException(name);
                }
            });
        } catch (PMLFunctionAlreadyDefinedException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void checkGetFunctionInput(String name) throws PMLFunctionNotDefinedException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                Node node = tx.findNode(PML_FUNC_LABEL, NAME_PROPERTY, name);
                if (node == null) {
                    throw new PMLFunctionNotDefinedException(name);
                }
            });
        } catch (PMLFunctionNotDefinedException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void checkCreateConstantInput(String name) throws PMLConstantAlreadyDefinedException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                Node node = tx.findNode(PML_CONST_LABEL, NAME_PROPERTY, name);
                if (node != null) {
                    throw new PMLConstantAlreadyDefinedException(name);
                }
            });
        } catch (PMLConstantAlreadyDefinedException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }

    @Override
    public void checkGetConstantInput(String name) throws PMLConstantNotDefinedException, PMBackendException {
        try {
            neo4j.runTx(tx -> {
                Node node = tx.findNode(PML_CONST_LABEL, NAME_PROPERTY, name);
                if (node == null) {
                    throw new PMLConstantNotDefinedException(name);
                }
            });
        } catch (PMLConstantNotDefinedException | PMBackendException e) {
            throw e;
        } catch (PMException e) {
            throw new PMBackendException(e);
        }
    }
}
