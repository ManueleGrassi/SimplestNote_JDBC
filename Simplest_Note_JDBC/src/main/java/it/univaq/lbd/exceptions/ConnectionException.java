package it.univaq.lbd.exceptions;

public class ConnectionException extends ApplicationException {

    public ConnectionException(Throwable cause) {
        super("Errore di connessione", cause);
    }

}
