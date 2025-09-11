package org.csbf.security.utils.helperclasses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.csbf.security.utils.commons.Domain;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Data
@Builder
@AllArgsConstructor
public class ResponseMessage<T extends Domain> {
    private boolean success;
    private String description;
    private T domain;

    public static class SuccessResponseMessage extends ResponseMessage {
        public SuccessResponseMessage(String description) {
            super(true, description, null);
        }
    }

    public static class ExceptionResponseMessage extends ResponseMessage {
        public ExceptionResponseMessage(String description) {
            super(false, description, null);
        }
    }
}
