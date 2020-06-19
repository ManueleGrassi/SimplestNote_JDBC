package it.univaq.lbd.exceptions;

public class NoConnectionAvailableException extends ApplicationException {

    public NoConnectionAvailableException() {
        super("Nessuna connessione disponibile!");
    }

}
