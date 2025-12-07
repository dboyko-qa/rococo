package qa.dboyko.rococo.ex;

public class ArtistNotFoundException extends RuntimeException {
    public ArtistNotFoundException() {
    }

    public ArtistNotFoundException(String code) {
        super("Artist code %s not found.".formatted(code));
    }
}
