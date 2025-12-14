package qa.dboyko.rococo.ex;

public class MuseumAlreadyExistsException extends RuntimeException {
    public MuseumAlreadyExistsException() {
    }

    public MuseumAlreadyExistsException(String name) {
        super("Museum with name %s already exists".formatted(name));
    }
}
