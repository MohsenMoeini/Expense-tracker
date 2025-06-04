package ir.snp.threashold.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ThresholdNotFoundException extends RuntimeException {
    public ThresholdNotFoundException(Long id) {
        super("Threshold with ID " + id + " not found");
    }
}
