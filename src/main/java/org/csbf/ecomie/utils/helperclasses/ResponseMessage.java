package org.csbf.ecomie.utils.helperclasses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.csbf.ecomie.utils.commons.Domain;


/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@Data
@Builder
@AllArgsConstructor
public class ResponseMessage<D extends Domain>  {
    private boolean success;
    private String description;
    private D data;

    public static class SuccessResponseMessage<D extends Domain> extends ResponseMessage<D> {
        public SuccessResponseMessage(String description) {
            super(true, description, null);
        }

        public SuccessResponseMessage(String description, D data) {
            super(true, description, data);
        }
    }

    public static class ExceptionResponseMessage<D extends Domain> extends ResponseMessage<D> {
        public ExceptionResponseMessage(String description) {
            super(false, description, null);
        }
        public ExceptionResponseMessage(String description, D data) {
            super(false, description, data);
        }
    }
}
