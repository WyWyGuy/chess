package service;

import dataaccess.*;

public class ClearService {

    private DatabaseAuthDAO authDAO = new DatabaseAuthDAO();
    private DatabaseGameDAO gameDAO = new DatabaseGameDAO();
    private DatabaseUserDAO userDAO = new DatabaseUserDAO();

    public void clear() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }

}
