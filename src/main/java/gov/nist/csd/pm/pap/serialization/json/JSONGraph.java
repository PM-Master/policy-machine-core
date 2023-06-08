package gov.nist.csd.pm.pap.serialization.json;

import gov.nist.csd.pm.common.graph.relationship.AccessRightSet;

import java.util.ArrayList;
import java.util.List;

public class JSONGraph {

    List<JSONPolicyClass> pcs;
    List<JSONUserAttribute> uas;
    List<JSONObjectAttribute> oas;
    List<JSONUserOrObject> users;
    List<JSONUserOrObject> objects;

    public JSONGraph(List<JSONPolicyClass> pcs,
                     List<JSONUserAttribute> uas,
                     List<JSONObjectAttribute> oas,
                     List<JSONUserOrObject> users,
                     List<JSONUserOrObject> objects) {
        this.pcs = pcs;
        this.uas = uas;
        this.oas = oas;
        this.users = users;
        this.objects = objects;
    }

    public List<JSONPolicyClass> getPcs() {
        return pcs;
    }

    public void setPcs(List<JSONPolicyClass> pcs) {
        this.pcs = pcs;
    }

    public List<JSONUserAttribute> getUas() {
        return uas;
    }

    public void setUas(List<JSONUserAttribute> uas) {
        this.uas = uas;
    }

    public List<JSONObjectAttribute> getOas() {
        return oas;
    }

    public void setOas(List<JSONObjectAttribute> oas) {
        this.oas = oas;
    }

    public List<JSONUserOrObject> getUsers() {
        return users;
    }

    public void setUsers(List<JSONUserOrObject> users) {
        this.users = users;
    }

    public List<JSONUserOrObject> getObjects() {
        return objects;
    }

    public void setObjects(List<JSONUserOrObject> objects) {
        this.objects = objects;
    }
}
