package org.csbf.ecomie.exceptions;

//import com.sun.mail.util.MailConnectException;
import lombok.extern.slf4j.Slf4j;
import org.csbf.ecomie.utils.helperclasses.ResponseMessage.ExceptionResponseMessage;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
        import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

        import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ResourceEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(Exception ex) {
        ExceptionResponseMessage message = new ExceptionResponseMessage(ex.getMessage());
        return new ResponseEntity<>(message, NOT_FOUND);
    }

    @ExceptionHandler(ResourceExistsException.class)
    public ResponseEntity<Object> handleResourceExistsException(Exception ex) {
        ExceptionResponseMessage message = new ExceptionResponseMessage(ex.getMessage());
        return new ResponseEntity<>(message, CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(Exception ex) {
        ExceptionResponseMessage message = new ExceptionResponseMessage(ex.getMessage());
        return new ResponseEntity<>(message, BAD_REQUEST);
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<Object> handleNullPointerExceptions(Exception ex) {
        ExceptionResponseMessage message = new ExceptionResponseMessage(ex.getMessage());
        return new ResponseEntity<>(message, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<Object> handleInvalidTokenExceptions(Exception ex) {
        ExceptionResponseMessage message = new ExceptionResponseMessage(ex.getMessage());
        return new ResponseEntity<>(message, FORBIDDEN);
    }

    @ExceptionHandler(FailedSendEmailException.class)
    public ResponseEntity<Object> handleFailedSendEmailException(Exception ex) {
        ExceptionResponseMessage message = new ExceptionResponseMessage(ex.getMessage());
        return new ResponseEntity<>(message, BAD_GATEWAY);
    }

    @ExceptionHandler(BadRequestException.InvalidAuthenticationRequestException.class)
    public ResponseEntity<Object> handleInvalidAuthenticationRequestException(Exception ex) {
        ExceptionResponseMessage message = new ExceptionResponseMessage(ex.getMessage());
        return new ResponseEntity<>(message, FORBIDDEN);
    }


}
