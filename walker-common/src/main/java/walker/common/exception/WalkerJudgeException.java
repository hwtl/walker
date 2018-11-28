package walker.common.exception;

public class WalkerJudgeException extends RuntimeException{

    private Object source;

    public WalkerJudgeException(String message, Object source) {
        super(message);
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }
}
