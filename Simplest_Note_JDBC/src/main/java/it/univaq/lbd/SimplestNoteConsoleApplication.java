package it.univaq.lbd;

import it.univaq.lbd.exceptions.ApplicationException;
import it.univaq.lbd.exceptions.UserNotFoundException;
import it.univaq.lbd.views.NoteView;

import java.util.List;
import java.util.Scanner;

import static it.univaq.lbd.ConnectionManager.*;

public class SimplestNoteConsoleApplication {

    private void startOperations(){
        System.out.println("Sei un utente già registrato? <y/n>");
        Scanner reader = new Scanner(System.in);
        String response = reader.next();

        if(response.toUpperCase().equals("Y")){
            insertUsername(true);
        }else if(response.toUpperCase().equals("N")){
            System.out.println("Effettua la registrazione");
            insertUsername(false);
        }else{
            System.out.println("Input non valido");
            startOperations();
        }

    }

    private void insertUsername(Boolean isLogin){
        System.out.println("Inserisci il tuo username:");
        Scanner reader = new Scanner(System.in);
        String response = reader.next();
        if(response != null && !response.isEmpty()){
            insertPsw(response,isLogin);
        }else{
            System.out.println("Input non valido");
            insertUsername(isLogin);
        }
    }

    private void insertPsw(String username,Boolean isLogin){
        System.out.println("Inserisci la tua password:");
        Scanner reader = new Scanner(System.in);
        String psw = reader.next();
        if(psw != null){
            if(isLogin) {
                signIn(username, psw);
                return;
            }
            insertEmail(username,psw);
        }else{
            System.out.println("Input non valido");
            insertPsw(username,isLogin);
        }
    }

    private void insertEmail(String username,String psw){
        System.out.println("Inserisci la tua email:");
        Scanner reader = new Scanner(System.in);
        String email = reader.next();
        if(email != null && !email.isEmpty()){
             signUp(username,psw,email);
        }else{
            System.out.println("Input non valido");
            insertEmail(username,psw);
        }
    }

    private void doYouWantToDoAnotherAction(){
        System.out.println("Vuoi eseguire un'altra azione? <y/n>");
        Scanner reader = new Scanner(System.in);
        String response = reader.next();
        if(response.toUpperCase().equals("Y")){
            otherActions();
        }
    }

    private void otherActions(){
        System.out.println("Scegli un'azione tra le seguenti: \n 1 - Visualizza il tuo blocco note; \n 2 - Condividi una nota con un altro utente; \n 3 - Crea una nota; \n 4 - Chiudi il programma; \n Inserisci il numero: ");
        Scanner reader = new Scanner(System.in);
        Integer action = reader.nextInt();
        switch (action){
            case 1:
                getMyNotePad();
                break;
            case 2:
                selectNote();
                break;
            case 3:
                insertNewNote();
                break;
            case 4:
                break;
            default:
                System.out.println("Input non riconosciuto");
                doYouWantToDoAnotherAction();
        }
    }

    private void selectNote(){
        System.out.println("Inserisci l'id della nota che vuoi condividere:");
        Scanner scanner = new Scanner(System.in);
        Integer id = scanner.nextInt();
        if(id != null){
            selectUser(id);
        }else{
            System.out.println("Input non riconosciuto");
            doYouWantToDoAnotherAction();
        }
    }

    private void selectUser(Integer noteId){
        System.out.println("Inserisci l'email o lo username dell'utente a cui vuoi condividere questa nota:");
        Scanner scanner = new Scanner(System.in);
        String user = scanner.next();
        if(user != null && !user.isEmpty()){
            selectLevelAuth(noteId,user);
        }else{
            System.out.println("Input non riconosciuto");
            doYouWantToDoAnotherAction();
        }
    }

    private void selectLevelAuth(Integer noteId,String user){
        System.out.println("L'utente può modificare la nota? <y/n>");
        Scanner scanner = new Scanner(System.in);
        String response = scanner.next();
        if(response.toUpperCase().equals("Y")){
            try {
                AccountService.shareNote(user,noteId,false);
            }catch (ApplicationException ex){
                ex.logException();
                doYouWantToDoAnotherAction();
            }
        }else if(response.toUpperCase().equals("N")){
            try {
                AccountService.shareNote(user,noteId,true);
            }catch (ApplicationException ex){
                ex.logException();
                doYouWantToDoAnotherAction();
            }
        }else{
            System.out.println("Input non valido");
        }
        doYouWantToDoAnotherAction();
    }

    private void getMyNotePad(){
        try {
            List<NoteView> myNotePad = NotepadService.getMyNotepad();
            if (!myNotePad.isEmpty()){
                myNotePad.forEach(element -> {
                    System.out.println("id: " + element.getId() + " title: " + element.getTitle() + " text: " + element.getText() + " tags: " + element.getTags());
                });
            }else{
                System.out.println("Non hai nessuna nota nel tuo blocco note!");
            }
            doYouWantToDoAnotherAction();
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    private void insertNewNote(){
        System.out.println("Inserisci il titolo della nuova nota:");
        Scanner scanner = new Scanner(System.in);
        String title = scanner.nextLine();
        if(title != null && !title.isEmpty()) {
            try {
                NotepadService.insertNewNote(title);
                doYouWantToDoAnotherAction();
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Input non riconosciuto");
            doYouWantToDoAnotherAction();
        }
    }

    private void signUp(String username,String psw,String email){
        try {
            AccountService.signUp(username, psw, email);
            signIn(username, psw);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    private void signIn(String username, String psw) {
        try {
            AccountService.doLogin(username, psw);
            doYouWantToDoAnotherAction();
        }catch (ApplicationException e){
            if(e instanceof  UserNotFoundException){
                System.out.println("Credenziali errate, riprova!");
                insertUsername(true);
                return;
            }
            e.logException();
            try {
               closeConnection();
            } catch (ApplicationException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SimplestNoteConsoleApplication instance = new SimplestNoteConsoleApplication();
        System.out.println("Benvenuto in Simplest Note!");
        instance.startOperations();
        try {
            closeConnection();
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
        System.out.println("Arrivederci!");
    }

}
