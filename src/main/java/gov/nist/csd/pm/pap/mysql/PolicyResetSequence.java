package gov.nist.csd.pm.pap.mysql;

import java.util.Arrays;
import java.util.List;

public class PolicyResetSequence {

    public static List<String> getSequence() {
        return Arrays.asList(
                "SET SQL_SAFE_UPDATES = 0",
                "SET FOREIGN_KEY_CHECKS=0",
                "delete from node",
                "delete from assignment",
                "delete from association",
                "delete from prohibition",
                "delete from prohibition_container",
                "delete from obligation",
                "delete from resource_access_rights",
                "delete from pml_function",
                "delete from pml_constant",
                "SET SQL_SAFE_UPDATES = 1",
                "SET FOREIGN_KEY_CHECKS=1"
        );
    }
}
