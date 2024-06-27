package gov.nist.csd.pm.pap.serialization.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;

import java.util.List;

public class JSONPolicy {

    private AccessRightSet resourceAccessRights;
    private JSONGraph graph;
    private List<String> prohibitions;
    private List<String> obligations;

    public JSONPolicy(AccessRightSet resourceAccessRights,
                      JSONGraph graph,
                      List<String> prohibitions,
                      List<String> obligations) {
        this.resourceAccessRights = resourceAccessRights;
        this.graph = graph;
        this.prohibitions = prohibitions;
        this.obligations = obligations;
    }

    public AccessRightSet getResourceAccessRights() {
        return resourceAccessRights;
    }

    public void setResourceAccessRights(AccessRightSet resourceAccessRights) {
        this.resourceAccessRights = resourceAccessRights;
    }

    public JSONGraph getGraph() {
        return graph;
    }

    public void setGraph(JSONGraph graph) {
        this.graph = graph;
    }

    public List<String> getProhibitions() {
        return prohibitions;
    }

    public void setProhibitions(List<String> prohibitions) {
        this.prohibitions = prohibitions;
    }

    public List<String> getObligations() {
        return obligations;
    }

    public void setObligations(List<String> obligations) {
        this.obligations = obligations;
    }

    @Override
    public String toString() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this);
    }

    public static JSONPolicy fromJSON(String json) {
        return new Gson().fromJson(json, JSONPolicy.class);
    }
}
