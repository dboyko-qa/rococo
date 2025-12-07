package qa.dboyko.rococo.ex;

public class ArtistAlreadyExistsException extends RuntimeException {
    public ArtistAlreadyExistsException() {
    }

    public ArtistAlreadyExistsException(String name) {
        super("Artist with name %s already exists".formatted(name));
    }
}
