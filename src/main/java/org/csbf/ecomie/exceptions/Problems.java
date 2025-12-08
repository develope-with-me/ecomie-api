package org.csbf.ecomie.exceptions;

import java.util.List;
import java.util.Objects;

/**
 * Ecomie Project.
 *
 * @author DB.Tech
 */
public interface Problems {

    String BAD_PAYLOAD = "Bad Payload";
    String VALIDATION_ERROR_MESSAGE = "Validation Error";
    String INTERNAL_SERVER_ERROR_MESSAGE = "Internal Server Error";

    Problem INVALID_PARAMETER_ERROR =
            new Problem("Bad Parameters", "One or more parameters is invalid.", 400,
                    "Invalid Parameter Error", "", "40000000", List.of());

    Problem PAYLOAD_VALIDATION_ERROR = new Problem(BAD_PAYLOAD, "The payload provided is invalid.",
            400, VALIDATION_ERROR_MESSAGE, "", "40000001", List.of());

    Problem BAD_REQUEST = new Problem("Bad Request", "The request was not well constructed.",
            400, VALIDATION_ERROR_MESSAGE, "", "40000001", List.of());

    Problem NO_PAYLOAD_PROVIDED_ERROR =
            new Problem(BAD_PAYLOAD, "The payload provided should not be null.", 400,
                    VALIDATION_ERROR_MESSAGE, "", "40000002", List.of());

    Problem NULL_OBJECT_PROVIDED_ERROR =
            new Problem("Null Object", "The object provided should not be null.", 400,
                    VALIDATION_ERROR_MESSAGE, "", "40000003", List.of());

    Problem OBJECT_VALIDATION_ERROR = new Problem("Bad Object", "The object provided is invalid.",
            400, VALIDATION_ERROR_MESSAGE, "", "40000004", List.of());

    Problem INCONSISTENT_DATA_ERROR = new Problem("Inconsistent Data",
            "The object does not match the domain.", 400, "Data Error", "", "40000005", List.of());
    Problem JSON_DESERIALIZATION_ERROR =
            new Problem(BAD_PAYLOAD, "Unable to deserialize the payload provided.", 400,
                    "Serialization/Deserialization Error", "", "40000006", List.of());

    Problem JSON_SERIALIZATION_ERROR =
            new Problem("JSON Serialization Error", "Unable to serialize the pojo provided.", 400,
                    "Serialization/Deserialization Error", "", "40000007", List.of());

    Problem INCONSISTENT_STATE_ERROR =
            new Problem("Inconsistent State", "The system state does not match expected value.",
                    400, "Data Error", "", "40000008", List.of());

    Problem QUERY_ERROR = new Problem("Bad Query", "An issue occurred while querying the platform.",
            400, "Query Error", "", "40000009", List.of());

    Problem UNIQUE_CONSTRAINT_VIOLATION_ERROR =
            new Problem("Unique Constraint", "A unique constraints violation error occurred.", 400,
                    VALIDATION_ERROR_MESSAGE, "", "40000011", List.of());

    Problem EMAIL_ALREADY_VERIFIED_ERROR = new Problem("Email Verification Error",
            "Email already verified", 400, "Verification Error", "", "40000010", List.of());

    Problem UNAUTHORIZED = new Problem("Unauthorized", "The request requires user authentication.",
            401, "Resource Error", "", "40100000", List.of());

    Problem PAYMENT_REQUIRED = new Problem("Payment Required", "The request requires payment.", 402,
            "Payment Error", "", "40200000", List.of());

    Problem FORBIDDEN_OPERATION_ERROR = new Problem("Forbidden Operation",
            "This request is forbidden.", 403, "Authorisation Error", "", "40300000", List.of());

    Problem NOT_FOUND = new Problem("Not Found", "The requested resource was not found.", 404,
            "Resource Error", "", "40400000", List.of());

    Problem METHOD_NOT_ALLOWED = new Problem("Method Not Allowed", "The requested method is not allowed", 405,
            "Resource Error", "", "40500000", List.of());

    Problem REQUEST_TIMEOUT = new Problem("Request Timeout", "Request timed out.", 408,
            "Resource Error", "", "40800000", List.of());

    Problem CONFLICT = new Problem("Conflict", "There is a conflict.", 409,
            "Resource Error", "", "40900000", List.of());

    Problem REQUEST_URI_TOO_LONG = new Problem("Request URI Too Long", "The requested URI is too long.", 414,
            "Reasource Error", "", "41400000", List.of());

    Problem UNSUPPORTED_MEDIA_TYPE = new Problem("Unsupported Media Type", "The media provided has unsupported media type.", 415,
            "Reasource Error", "", "41500000", List.of());

    Problem INTERNAL_SERVER_ERROR = new Problem(INTERNAL_SERVER_ERROR_MESSAGE,
            "An error occurred while processing the request.", 500, INTERNAL_SERVER_ERROR_MESSAGE,
            "", "50000000", List.of());

    Problem NOT_A_CLOUD_EVENT_ERROR = new Problem("Not A CloudEvent Error",
            """
            An error occurred while processing the messageType-bus request.
            Either messageType body is null or it is not in a CloudEvent format""", 500,
            INTERNAL_SERVER_ERROR_MESSAGE, "", "50000001", List.of());

    Problem NO_OPERATION_ID = new Problem(BAD_PAYLOAD, "No operationId provided.", 500,
            VALIDATION_ERROR_MESSAGE, "", "50000002", List.of());

    Problem DATABASE_ERROR = new Problem("Database Error", "A database error occurred.", 500,
            "Persistence Error", "", "50000003", List.of());

    Problem NOT_IMPLEMENTED_ERROR =
            new Problem("Not Implemented Error", "There requested operation is not implemented.",
                    501, "Not Implemented Error", "", "50100000", List.of());


    static <T extends Throwable> Problem fromThrowable(T cause) {
        if (cause instanceof EcomieException ecomieException) {
            return ecomieException.getProblem();
        } else {

//            if (cause instanceof ReplyException replyException) {
//            try {
//                return new JSONObject(replyException.getMessage()).mapTo(Problem.class);
//            } catch (DecodeException | IllegalArgumentException ex) {
//                new Problem("Reply Exception", replyException.getMessage(), 500,
//                        replyException.failureType().toString(), "",
//                        StringUtils.leftPad("%s".formatted(replyException.failureCode()), 8, '0'),
//                        List.of());
//            }
            return new Problem("Generic Exception", cause.getMessage(), 500, "Error", "", "00000001",
                    List.of());
        }

    }

    static EcomieException toThrowable(Problem problem) {
        return new EcomieException(Objects.requireNonNullElse(problem, INTERNAL_SERVER_ERROR));
    }
}
