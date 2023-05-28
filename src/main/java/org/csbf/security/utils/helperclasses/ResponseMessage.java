package org.csbf.security.utils.helperclasses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResponseMessage {
    private boolean success;
    private String description;

    public static class SuccessResponseMessage extends ResponseMessage {
        public SuccessResponseMessage(String description) {
            super(true, description);
        }
    }

    public static class ExceptionResponseMessage extends ResponseMessage {
        public ExceptionResponseMessage(String description) {
            super(false, description);
        }
    }
}
