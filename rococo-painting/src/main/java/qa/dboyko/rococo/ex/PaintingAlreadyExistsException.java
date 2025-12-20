package qa.dboyko.rococo.ex;

public class PaintingAlreadyExistsException extends RuntimeException {
    public PaintingAlreadyExistsException() {
    }

    public PaintingAlreadyExistsException(String name) {
        super("Artist with name %s already exists".formatted(name));
    }
}
