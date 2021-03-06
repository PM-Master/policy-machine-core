package gov.nist.csd.pm.pip.mysql;

import gov.nist.csd.pm.exceptions.PIPException;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.operations.OperationSet;
import gov.nist.csd.pm.pip.prohibitions.model.Prohibition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLProhibitionsTest {

    private MySQLProhibitions prohibitions;

    @BeforeEach
    void setup() throws Exception {
        // load the policydb_core sql script
        InputStream resourceAsStream = getClass().getResourceAsStream("/mysql/policydb_core.sql");
        if (resourceAsStream == null) {
            throw new Exception("could not read contents of policydb_core.sql");
        }

        // execute the sql script against the in memory database
        String sql = new String(resourceAsStream.readAllBytes());
        String[] split = sql.split(";");
        try (Connection conn = DriverManager.getConnection("jdbc:h2:~/policydb_core;MODE=MySQL", "sa", "");
             Statement stmt = conn.createStatement()) {
            for (String s : split) {
                stmt.executeUpdate(s);
            }
        }

        // create a new MySQLGraph with the connection
        MySQLConnection connection = new MySQLConnection("jdbc:h2:~/policydb_core;MODE=MySQL", "sa", "");
        this.prohibitions = new MySQLProhibitions(connection);
    }

    @Test
    void add() throws PIPException {
        Prohibition p1 = new Prohibition.Builder("prohibition 1", "super_u", new OperationSet("read"))
                .setIntersection(false)
                .addContainer("super_pc", true)
                .build();

        prohibitions.add(p1);

        Prohibition prohibition = prohibitions.get(p1.getName());
        assertEquals(p1.getName(), prohibition.getName());
        assertEquals(p1.getSubject(), prohibition.getSubject());
        assertEquals(p1.isIntersection(), prohibition.isIntersection());
        assertEquals(p1.getOperations(), prohibition.getOperations());
        assertEquals(p1.getContainers(), prohibition.getContainers());
    }

    @Test
    void addException() throws PIPException {
        Prohibition p3 = new Prohibition.Builder("prohibition 1", "super", new OperationSet("read"))
                .build();
        prohibitions.add(p3);

        //a null prohibition - a null or empty name - a null or empty subject
        Prohibition p = null;
        Prohibition p1 = new Prohibition.Builder("", "subject", new OperationSet("read"))
                .build();
        Prohibition p2 = new Prohibition.Builder("nameRandom", "", new OperationSet("read"))
                .build();
        //Another prohibition with the same name already exists
        Prohibition p3_sameName = new Prohibition.Builder("prohibition 1", "super_u", new OperationSet("read"))
                .addContainer("10", true)
                .build();

        assertThrows(IllegalArgumentException.class, () -> prohibitions.add(p));
        assertThrows(IllegalArgumentException.class, () -> prohibitions.add(p1));
        assertThrows(IllegalArgumentException.class, () -> prohibitions.add(p2));
        assertThrows(PIPException.class, () -> prohibitions.add(p3_sameName));

    }

    @Test
    void getAll() throws PIPException{
        //sizeTot is 0 since we cleared all the prohibitions
        int sizeTot = prohibitions.getAll().size();

        Prohibition p = new Prohibition.Builder("prohibition1", "subject", new OperationSet("read"))
                .build();
            prohibitions.add(p);

            assertEquals(sizeTot+1, prohibitions.getAll().size());
    }

    @Test
    void get() throws PIPException{
        Prohibition p = new Prohibition.Builder("new prohibition", "subject", new OperationSet("read", "write"))
                .addContainer("super_pc", true)
                .build();

        prohibitions.add(p);
        Prohibition prohibition = prohibitions.get(p.getName());
        assertEquals(p.getName(), prohibition.getName());
        assertEquals(p.getSubject(), prohibition.getSubject());
        assertEquals(p.getOperations(), prohibition.getOperations());
        assertEquals(p.getContainers(), prohibition.getContainers());
        assertEquals(p.isIntersection(), prohibition.isIntersection());
    }

    @Test
    void getException() throws PIPException{
        assertThrows(PIPException.class, () -> prohibitions.get("unknown prohibition"));
    }

    @Test
    void getProhibitionsFor() throws PIPException{

        Prohibition prohibition = new Prohibition.Builder("prohibition", "subject", new OperationSet("read"))
                .setIntersection(false)
                .addContainer("super_ua1", true)
                .build();

        Prohibition prohibition2 = new Prohibition.Builder("prohibition2", "subject", new OperationSet("read"))
                .setIntersection(false)
                .build();

        prohibitions.add(prohibition);
        prohibitions.add(prohibition2);
        assertEquals(2, prohibitions.getProhibitionsFor("subject").size());

    }

    @Test
    void update() throws PIPException{
        Prohibition prohibition = new Prohibition.Builder("prohibition", "subject", new OperationSet("read"))
                .setIntersection(false)
                .build();

        prohibitions.add(prohibition);
        Prohibition prohibition_get = prohibitions.get(prohibition.getName());

        //prohibition2 is used in order to update prohibition
        Prohibition prohibition2 = new Prohibition.Builder("prohibition update", "super_oa", new OperationSet("read", "write"))
                .setIntersection(true)
                .addContainer("super_pc", true)
                .build();

        //prohibition has now the same values as prohibition2
        prohibitions.update(prohibition.getName(), prohibition2);

        prohibition = prohibitions.get(prohibition2.getName());
        assertEquals(prohibition.isIntersection(), prohibition2.isIntersection());
        assertEquals(prohibition.getName(), prohibition2.getName());
        assertEquals(prohibition.getSubject(), prohibition2.getSubject());
        assertEquals(prohibition.getOperations(), prohibition2.getOperations());
        assertEquals(prohibition.getContainers(), prohibition2.getContainers());

    }

    @Test
    void updateException() throws PIPException {
        Prohibition prohibition = new Prohibition.Builder("prohibition", "subject", new OperationSet("read"))
                .setIntersection(false)
                .build();

        prohibitions.add(prohibition);
        //a null prohibition - a null or empty name - an already existing name
        Prohibition p = null;
        Prohibition p1 = new Prohibition.Builder("", "subject", new OperationSet("read"))
                .build();
        //Another prohibition with the same name already exists
        Prohibition p2 = new Prohibition.Builder("prohibition", "super_u", new OperationSet("read"))
                .build();

        assertThrows(IllegalArgumentException.class,() -> prohibitions.update(prohibition.getName(), p));
        assertThrows(IllegalArgumentException.class,() -> prohibitions.update(prohibition.getName(), p1));
        assertThrows(PIPException.class,() -> prohibitions.update(prohibition.getName(), p2));
    }

    @Test
    void delete() throws PIPException {
        Prohibition p = new Prohibition.Builder("new prohibition to be deleted", "new subject", new OperationSet("read"))
                .build();

        prohibitions.add(p);
        int sizeTot = prohibitions.getAll().size();
        prohibitions.delete(p.getName());

        assertEquals(sizeTot-1, prohibitions.getAll().size());
    }

    @Test
    void deleteException() throws PIPException {
        assertThrows(PIPException.class, () -> prohibitions.delete("a prohibition that do not exist"));
    }

}
