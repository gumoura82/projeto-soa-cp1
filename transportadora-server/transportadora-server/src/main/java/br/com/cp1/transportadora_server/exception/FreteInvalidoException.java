package br.com.cp1.transportadora_server.exception;

public class FreteInvalidoException extends RuntimeException {

    private final String codigo;

    public FreteInvalidoException(String codigo, String message) {
        super(message);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
