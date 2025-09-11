package org.csbf.security.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProblemError(@JsonProperty("name") String name,
                           @JsonProperty("reason") String reason) {
}
