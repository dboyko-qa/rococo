package qa.dboyko.rococo.ex;

public class ArtistAlreadyExistsException extends RuntimeException {
    public ArtistAlreadyExistsException() {
    }

    public ArtistAlreadyExistsException(String code) {
        super("Country with code %s already exists".formatted(code));
    }
}
