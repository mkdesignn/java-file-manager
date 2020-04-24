package filehandler.project.exceptions;

public class EmailValidationException extends Exception {

    private String email;

    public EmailValidationException(String email) {
        this.email = email;
    }

    @Override
    public String getMessage() {
        return "email (" + email + ") is not a valid one";
    }
}
