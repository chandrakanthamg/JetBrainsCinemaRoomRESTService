package cinema;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Error {
    public String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
