package team5.class004.android.model;

/**
 * Created by sunriv on 2018. 3. 10..
 */

public class AbstractResponse<T> {

    public boolean success;
    public String error;
    public Integer errorCode;
    public Integer version;
    public String msg;
    public T result;

    public boolean isSuccess() {
        return success;
    }

    public T getResult() {
        return result;
    }

    public String getErrorMessage() {
        return error;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public AbstractResponse(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "AbstractResponse{" +
                "success=" + success +
                ", error='" + error + '\'' +
                ", errorCode=" + errorCode +
                ", result=" + result +
                ", msg=" + msg +
                '}';
    }
}