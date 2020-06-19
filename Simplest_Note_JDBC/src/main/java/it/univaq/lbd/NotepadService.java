package it.univaq.lbd;

import it.univaq.lbd.exceptions.ApplicationException;
import it.univaq.lbd.exceptions.UserNotFoundException;
import it.univaq.lbd.views.NoteView;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotepadService {

    public static List<NoteView> getMyNotepad() throws ApplicationException {
        List<NoteView> noteViews = new ArrayList<>();
        if(AccountService.getLoggedIdAccount() == null){
            throw new UserNotFoundException();
        }
        Connection connection = ConnectionManager.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CALL get_user_notepad(?)");
            preparedStatement.setNString(1,AccountService.getLoggedIdAccount().toString());
            System.out.println("------------> Eseguo la query: " + preparedStatement.toString());
            preparedStatement.executeUpdate();
            PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM user_notepad ");
            System.out.println("------------> Eseguo la query: " + preparedStatement1.toString());
            ResultSet rs = preparedStatement1.executeQuery();
            while (rs.next()){
                NoteView noteView = new NoteView(rs.getInt("id"), rs.getNString("note_title"), rs.getNString("note_text"), rs.getNString("note_tags"));
                noteViews.add(noteView);
            }
        }catch(SQLException ex){

        }
        return noteViews;
    }

    public static void insertNewNote(String title) throws ApplicationException{
        if(AccountService.getLoggedIdAccount() == null){
            throw new UserNotFoundException();
        }
        Connection connection = ConnectionManager.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("CALL insert_note_with_title(?,?)");
            preparedStatement.setNString(1,title);
            preparedStatement.setInt(2,AccountService.getLoggedIdAccount());
            System.out.println("------------> Eseguo la query: " + preparedStatement.toString());
            preparedStatement.executeUpdate();
            System.out.println("------------> Eseguo il commit");
            connection.commit();
        } catch (SQLException e) {
           throw new ApplicationException("Errore durante l'inserimento della nota", e);
        }

    }

}
