package it.univaq.lbd;

import it.univaq.lbd.exceptions.ApplicationException;
import it.univaq.lbd.exceptions.NoteOwnerException;
import it.univaq.lbd.exceptions.UserNotFoundException;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountService {


    private static Integer loggedIdAccount;

    public static void doLogin(String username,String psw) throws ApplicationException {
        Connection connection = ConnectionManager.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("CALL search_user_username_password(?,?,@id_user, @username_user,  @email_user); ");
            statement.setNString(1,username);
            statement.setNString(2,psw);
            System.out.println("------------> Eseguo la query: " + statement.toString());
            statement.executeUpdate();
            PreparedStatement statementSelect = connection.prepareStatement("SELECT @id_user; ");
            System.out.println("------------> Eseguo la query: " + statementSelect.toString());
            ResultSet rs = statementSelect.executeQuery();
            while (rs.next()){
                AccountService.loggedIdAccount = rs.getInt("@id_user");
            }
            if(AccountService.loggedIdAccount == null || AccountService.loggedIdAccount == 0){
                throw new UserNotFoundException();
            }
        } catch(UserNotFoundException ex) {
            throw ex;
        }catch (SQLException e) {
            throw  new ApplicationException("Errore durante la richiesta di login",e);
        }
    }

    public static Integer getUserId(String user) throws ApplicationException{
        Connection connection = ConnectionManager.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM USER WHERE username=? OR  email=?; ");
            statement.setNString(1,user);
            statement.setNString(2,user);
            System.out.println("------------> Eseguo la query: " + statement.toString());
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                return rs.getInt("id");
            }
            throw new UserNotFoundException();
        } catch(UserNotFoundException ex) {
            throw ex;
        }catch (SQLException e) {
           throw new ApplicationException("Errore durante la ricerca dell'utente",e);
        }
    }

    public static void shareNote(String user, Integer noteId, Boolean readOnly) throws ApplicationException{
        if(getLoggedIdAccount() == null)
            throw new UserNotFoundException();
        try {
            Integer userId = getUserId(user);
            if(userId == getLoggedIdAccount())
                throw new ApplicationException("Non puoi condividere la nota a te stesso!");

            Connection connection = ConnectionManager.getConnection();

            PreparedStatement preparedStatementCheck = connection.prepareStatement("SELECT * FROM SHARED WHERE id_user = ? AND id_note = ?");
            preparedStatementCheck.setInt(1,userId);
            preparedStatementCheck.setInt(2,noteId);
            ResultSet rs = preparedStatementCheck.executeQuery();
            System.out.println("------------> Eseguo la query: " + preparedStatementCheck.toString());
            while (rs.next()){
                throw new ApplicationException("Hai già condiviso la nota con questo utente!");
            }

            PreparedStatement preparedStatement = connection.prepareStatement("CALL add_user_permission(?,?,?,?)");
            preparedStatement.setInt(1,userId);
            preparedStatement.setInt(2,noteId);
            preparedStatement.setString(3,readOnly ? "R" : "W");
            preparedStatement.setInt(4, AccountService.getLoggedIdAccount());
            System.out.println("------------> Eseguo la query: " + preparedStatement.toString());
            preparedStatement.executeUpdate();
            System.out.println("------------> Eseguo il commit della query");
            connection.commit();
        } catch (ApplicationException e) {
            throw e;
        } catch (SQLException ex){
            if(ex.getErrorCode() == 45000) {
                throw new NoteOwnerException();
            }else{
                throw new ApplicationException("Errore durante la condivisione della nota", ex);
            }
        }
    }

    public static Integer getLoggedIdAccount(){
        return loggedIdAccount;
    }

    public static void signUp(String username,String psw,String email) throws ApplicationException{
        Connection connection = ConnectionManager.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("CALL insert_user(?,?,?)");
            statement.setNString(1,username);
            statement.setNString(2,email);
            statement.setNString(3,psw);
            System.out.println("------------> Eseguo la query: " + statement.toString());
            statement.executeUpdate();
            System.out.println("------------> Eseguo il commit della query");
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

}
