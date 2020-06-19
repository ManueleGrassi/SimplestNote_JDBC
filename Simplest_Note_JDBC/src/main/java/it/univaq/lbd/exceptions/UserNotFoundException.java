package it.univaq.lbd.exceptions;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException(){
        super("Utente non trovato");
    }

}
