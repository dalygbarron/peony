package peony;

/**
 * Basically the point of this class is to take the advantage of exceptions
 * which is that they can send back some error text, and give this advantage to
 * whatever you call error handling without exceptions because exceptions are
 * stupid as hell.
 * @param <T> is the actual type you are trying to send back from you function
 *            or whatever it is.
 */
public class Result<T> {
    private final boolean success;
    private final T value;
    private final String message;

    /**
     * Secret constructor that just sets everything. It's secret because when
     * you use this stuff some of these arguments are kinda implicit in the way
     * you actually want to use this class.
     * @param success is whether the result is a success.
     * @param value   is the value in the result (only relevant to success).
     * @param message is the error message (only relevant to failure).
     */
    private Result(boolean success, T value, String message) {
        this.success = success;
        this.value = value;
        this.message = message;
    }

    /**
     * Gives you the success of the result.
     * @return the success.
     */
    public boolean success() {
        return this.success;
    }

    /**
     * Gives you the value of the result.
     * @return the result.
     */
    public T value() {
        return this.value;
    }

    /**
     * Gives you the error message from the result.
     * @return the error message.
     */
    public String message() {
        return this.message;
    }

    /**
     * Creates a failure result.
     * @param message is the error message.
     * @param <T>     is the type that was meant to be in the result.
     * @return the created failure result.
     */
    public static <T> Result<T> fail(String message) {
        return new Result<T>(false, null, message);
    }

    /**
     * Creates a success result.
     * @param value is the value to give as the result.
     * @param <T>   is the type of the value to give.
     * @return the success result object we just created together.
     */
    public static <T> Result<T> ok(T value) {
        return new Result<T>(true, value, null);
    }

    /**
     * Argumentless ok function for cases where there is nothing to give.
     * @param <T> is the type of return which is admittedly meaningless here.
     * @return the result that is successful.
     */
    public static <T> Result<T> ok() {
        return new Result<T>(true, null, null);
    }
}
