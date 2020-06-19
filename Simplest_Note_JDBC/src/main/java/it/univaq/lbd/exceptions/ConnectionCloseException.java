package it.univaq.lbd.exceptions;

public class ConnectionCloseException extends ApplicationException {

    public ConnectionCloseException(Throwable cause) {
        super("Errore durante la chiusura della connessione", cause);
    }

}
