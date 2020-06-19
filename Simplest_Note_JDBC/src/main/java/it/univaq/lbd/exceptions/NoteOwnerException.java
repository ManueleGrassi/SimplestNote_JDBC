package it.univaq.lbd.exceptions;

public class NoteOwnerException extends ApplicationException {

    public NoteOwnerException() {
        super("Non sei il proprietario di questa nota");
    }

}

