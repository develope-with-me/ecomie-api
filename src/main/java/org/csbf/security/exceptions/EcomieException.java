package org.csbf.security.exceptions;

import java.io.Serial;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public class EcomieException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6539904016201632067L;

    private final Problem problem;

    public EcomieException(Problem problem) {
        this(problem, problem.title());
    }

    public EcomieException(Problem problem, String message) {
        super(message);
        this.problem = problem;
    }

    public EcomieException(Problem problem, String message, Throwable cause) {
        super(message, cause);
        this.problem = problem;
    }

    public EcomieException(Problem problem, Throwable cause) {
        super(cause);
        this.problem = problem;
    }

    protected EcomieException(Problem problem, String message, Throwable cause,
                               boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.problem = problem;
    }

    public Problem getProblem() {
        return problem;
    }
}
