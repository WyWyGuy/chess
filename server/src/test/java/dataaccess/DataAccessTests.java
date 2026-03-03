package dataaccess;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import service.ClearService;

import javax.xml.crypto.Data;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessTests {

    DatabaseUserDAO userDAO = new DatabaseUserDAO();
    DatabaseAuthDAO authDAO = new DatabaseAuthDAO();
    DatabaseGameDAO gameDAO = new DatabaseGameDAO();

    @Test
    void successfulUserClear() throws DataAccessException {
        
    }

}
