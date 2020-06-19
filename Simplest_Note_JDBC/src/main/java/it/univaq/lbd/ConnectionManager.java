package it.univaq.lbd;

import it.univaq.lbd.exceptions.ApplicationException;
import it.univaq.lbd.exceptions.ConnectionCloseException;
import it.univaq.lbd.exceptions.ConnectionException;
import it.univaq.lbd.exceptions.NoConnectionAvailableException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private static final String DB_NAME = "simplest_note";
    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/" + DB_NAME + "?noAccessToProcedureBodies=true" + "&serverTimezone=Europe/Rome";
    private static final String DB_USER = "simplestNoteUser";
    private static final String DB_PASSWORD = "simplestNotePwd";
    private static Connection _connection;

    private static Connection connect() throws ApplicationException {
        try {
            return DriverManager.getConnection(CONNECTION_STRING, DB_USER, DB_PASSWORD);
        } catch (SQLException ex) {
            throw new ConnectionException(ex);
        }
    }

    public static Connection getConnection() throws ApplicationException{
        if(_connection != null){
            return _connection;
        }
        _connection = connect();
        try {
            _connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
        System.out.println("Creata una nuova connessione al database");
        return _connection;
    }

    public static void closeConnection() throws ApplicationException{
        if(_connection != null){
            try {
                _connection.close();
                System.out.println("Cessata la connessione al database");
                return;
             } catch (SQLException e) {
                throw new ConnectionCloseException(e);
            }
        }
        throw new NoConnectionAvailableException();
    }

}
