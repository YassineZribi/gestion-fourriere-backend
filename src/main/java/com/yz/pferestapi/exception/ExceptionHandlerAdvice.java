package com.yz.pferestapi.exception;

import com.yz.pferestapi.dto.ErrorResponse;
import com.yz.pferestapi.dto.FormValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleObjectNotFoundException(AppException ex) {
        return new ErrorResponse(ex.getStatus().value(), ex.getMessage(), ex.getMessage());
    }

    /**
     * This handles invalid inputs.
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    FormValidationErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<String, String>();

        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            details.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return new FormValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Provided arguments are invalid, see 'details' for more information.",
                ex.getMessage(),
                details
        );
    }

    @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse handleAuthenticationException(Exception ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Username or password is incorrect.", ex.getMessage());
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse handleInsufficientAuthenticationException(InsufficientAuthenticationException ex) {
        ex.printStackTrace();
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "Login credentials are missing.", ex.getMessage());
    }

    @ExceptionHandler(AccountStatusException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse handleAccountStatusException(AccountStatusException ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "User account is abnormal (disabled or locked).", ex.getMessage());
    }

    @ExceptionHandler({InvalidBearerTokenException.class, OAuth2AuthenticationException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    ErrorResponse handleInvalidBearerTokenException(Exception ex) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), "The access token provided is expired, revoked, malformed, or invalid for other reasons.", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), "No permission.", ex.getMessage());
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handleNoHandlerFoundException(Exception ex) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "This API endpoint is not found.", ex.getMessage());
    }

    /**
     * Fallback handles any unhandled exceptions.
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse handleOtherException(Exception ex) {
        ex.printStackTrace();
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "A server internal error occurs.", ex.getMessage());
    }
}
