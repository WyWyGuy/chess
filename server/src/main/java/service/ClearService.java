package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;

public class ClearService {

    private MemoryAuthDAO authDAO = new MemoryAuthDAO();
    private MemoryGameDAO gameDAO = new MemoryGameDAO();
    private MemoryUserDAO userDAO = new MemoryUserDAO();

    public void clear() throws DataAccessException {
        authDAO.clear();
        gameDAO.clear();
        userDAO.clear();
    }

}
