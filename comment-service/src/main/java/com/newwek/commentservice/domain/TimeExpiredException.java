package com.newwek.commentservice.domain;

/**
 * Thrown to indicate that an operation was attempted outside of its allowed time period.
 * This exception is used within entities or services where certain actions are
 * restricted to specific time frames.
 *
 * <p>For example, updating a comment might be allowed only within one hour of its creation.
 * If an update is attempted after this period, a {@code TimeExpiredException} will be thrown.
 *
 * <h2>Usage Example</h2>
 * <pre>{@code
 * public void updateComment(Comment comment) {
 *     if (LocalDateTime.now().isAfter(comment.getEnableToUpdateTill())) {
 *         throw new TimeExpiredException("Cannot update comment: Update period has expired.");
 *     }
 *     // Proceed with update
 * }
 * }</pre>
 *
 * <p>This exception is a runtime exception and does not need to be declared in a method's
 * {@code throws} clause. It is typically used to indicate a programming error or an
 * unexpected use case scenario.
 *
 * @see RuntimeException
 */
public class TimeExpiredException extends RuntimeException {
    /**
     * Constructs a {@code TimeExpiredException} with the specified detail message.
     *
     * @param timeExpiredMessage the detail message. The detail message is saved for
     *                           later retrieval by the {@link #getMessage()} method.
     */
    public TimeExpiredException(String timeExpiredMessage) {
        super(timeExpiredMessage);
    }
}

