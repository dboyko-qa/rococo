package qa.dboyko.rococo.ex;

public class MuseumNotFoundException extends RuntimeException {
    public MuseumNotFoundException() {
    }

    public MuseumNotFoundException(String code) {
        super("Museum code %s not found.".formatted(code));
    }
}
