package org.csbf.ecomie.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import one.util.streamex.StreamEx;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public record Problem(@JsonProperty("title") String title, @JsonProperty("detail") String detail,
                      @JsonProperty("statusCode") Integer statusCode,
                      @JsonProperty("type") String type, @JsonProperty("instance") String instance,
                      @JsonProperty("errorCode") String code,
                      @JsonProperty("invalidParams") List<ProblemError> problemErrors)
        implements Serializable {

    @Serial
    private static final long serialVersionUID = -2396131626314218778L;

    public Problem withTitle(String aNewTitle) {
        return new Problem(aNewTitle, detail, statusCode, type, instance, code, problemErrors);
    }

    public Problem withDetail(String aNewDetail) {
        return new Problem(title, aNewDetail, statusCode, type, instance, code, problemErrors);
    }

    public Problem appendDetail(String aNewDetail) {
        return new Problem(title, "%s%n%s".formatted(detail, aNewDetail), statusCode, type,
                instance, code, problemErrors);
    }

    public Problem withStatusCode(Integer aNewStatusCode) {
        return new Problem(title, detail, aNewStatusCode, type, instance, code, problemErrors);
    }

    public Problem withType(String aNewType) {
        return new Problem(title, detail, statusCode, aNewType, instance, code, problemErrors);
    }

    public Problem withInstance(String aNewInstance) {
        return new Problem(title, detail, statusCode, type, aNewInstance, code, problemErrors);
    }

    public Problem withCode(String aNewCode) {
        return new Problem(title, detail, statusCode, type, instance, aNewCode, problemErrors);
    }

    public Problem withProblemErrors(List<ProblemError> aNewProblemErrors) {
        var newProblemErrors = aNewProblemErrors == null || aNewProblemErrors.isEmpty()
                ? problemErrors
                : StreamEx.of(problemErrors.stream()).append(aNewProblemErrors.stream()).toList();
        return new Problem(title, detail, statusCode, type, instance, code, newProblemErrors);
    }

    public Problem withProblemError(String name, String reason) {
        return withProblemErrors(new ProblemError(name, reason));
    }

    public Problem withProblemErrors(ProblemError... newProblemErrors) {
        return withProblemErrors(newProblemErrors != null ? Arrays.asList(newProblemErrors) : null);
    }

    public EcomieException toException() {
        return new EcomieException(
                new Problem(title, detail, statusCode, type, instance, code, problemErrors));
    }

    public String problemErrorsAsString() {
        return problemErrorsAsString(List.of());
    }

    public String problemErrorsAsString(List<String> exclude) {
        var errors = problemErrors == null ? List.<ProblemError>of() : problemErrors;
        var asString = errors.stream()
                .filter(problemError -> !exclude.contains(problemError.name()))
                .map(problemError -> "%s: %n%s".formatted(problemError.name(),
                        problemError.reason()))
                .collect(Collectors.joining("\n"));
        asString = StringUtils.trimToEmpty(asString);
        return StringUtils.isBlank(asString) ? asString
                : " Extra Details: %s%n".formatted(asString);
    }
}
