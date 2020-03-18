package filehandler.project.exceptions;

public class UuidNotFoundException extends Exception {

    private String uuid;

    public UuidNotFoundException(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getMessage() {
        return "UUID (" + uuid + ") not found";
    }
}
