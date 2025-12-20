package qa.dboyko.rococo.ex;

public class PaintingNotFoundException extends RuntimeException {
    public PaintingNotFoundException() {
    }

    public PaintingNotFoundException(String code) {
        super("Artist code %s not found.".formatted(code));
    }
}
