package ru.tokarev.exception;

import org.modelmapper.MappingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.tokarev.dto.ApiErrorDto;
import ru.tokarev.exception.categoryexception.CategoryBadRequestException;
import ru.tokarev.exception.categoryexception.CategoryExistsException;
import ru.tokarev.exception.categoryexception.CategoryNotFoundException;
import ru.tokarev.exception.itemexception.ItemBadRequestException;
import ru.tokarev.exception.itemexception.ItemExistsException;
import ru.tokarev.exception.itemexception.ItemNotFoundException;
import ru.tokarev.exception.marketplaceexception.MarketPlaceBadRequestException;
import ru.tokarev.exception.marketplaceexception.MarketPlaceExistsException;
import ru.tokarev.exception.marketplaceexception.MarketPlaceNotFoundException;
import ru.tokarev.exception.productexception.ProductBadRequestException;
import ru.tokarev.exception.productexception.ProductExistsException;
import ru.tokarev.exception.productexception.ProductNotFoundException;
import ru.tokarev.exception.roleexception.RoleNotFoundException;
import ru.tokarev.exception.userexception.UserBadRequestException;
import ru.tokarev.exception.userexception.UserExistsException;
import ru.tokarev.exception.userexception.UserNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class ControllerAdviser extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        return getBindExceptionMessage(ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(
            BindException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        return getBindExceptionMessage(ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request
    ) {

        return buildApiErrorDto(HttpStatus.BAD_REQUEST.value(), "Bad request",
                List.of(ex.getLocalizedMessage()),
                (ServletWebRequest) request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
            MissingServletRequestPartException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request
    ) {

        return buildApiErrorDto(HttpStatus.BAD_REQUEST.value(), "Bad request",
                List.of(ex.getLocalizedMessage()),
                (ServletWebRequest) request);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, WebRequest request) {

        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        return buildApiErrorDto(HttpStatus.BAD_REQUEST.value(), "Bad request", errors,
                (ServletWebRequest) request);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        return buildApiErrorDto(HttpStatus.BAD_REQUEST.value(), "Bad request",
                List.of(ex.getName() + " should be of type " + ex.getRequiredType().getName(),
                        ex.getLocalizedMessage()),
                (ServletWebRequest) request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return buildApiErrorDto(HttpStatus.NOT_FOUND.value(), "Not found",
                List.of(ex.getRequestURL(), ex.getLocalizedMessage()),
                (ServletWebRequest) request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return buildApiErrorDto(HttpStatus.BAD_REQUEST.value(), "Bad request",
                List.of(ex.getLocalizedMessage()), (ServletWebRequest) request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(
                " method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        return buildApiErrorDto(HttpStatus.METHOD_NOT_ALLOWED.value(), "Method Not Allowed",
                List.of(ex.getLocalizedMessage(), builder.toString()), (ServletWebRequest) request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + ", "));

        return buildApiErrorDto(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Unsupported media type",
                List.of(ex.getLocalizedMessage(), builder.toString()), (ServletWebRequest) request);
    }

    @ExceptionHandler({AuthenticationException.class})
    protected ResponseEntity<Object> handleAuthenticationException(Exception ex, WebRequest request) {

        return buildApiErrorDto(HttpStatus.UNAUTHORIZED.value(), "Unauthorized",
                List.of(ex.getLocalizedMessage()), (ServletWebRequest) request);
    }

    @ExceptionHandler({AccessDeniedException.class})
    protected ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {

        return buildApiErrorDto(HttpStatus.FORBIDDEN.value(), "Forbidden",
                List.of(ex.getLocalizedMessage()), (ServletWebRequest) request);
    }

    @ExceptionHandler({UserExistsException.class, CategoryExistsException.class,
            MarketPlaceExistsException.class, ProductExistsException.class, ItemExistsException.class})
    protected ResponseEntity<Object> handleEntityExistsException(Exception ex, WebRequest request) {

        return buildApiErrorDto(HttpStatus.CONFLICT.value(), "Conflict",
                List.of(ex.getMessage()), (ServletWebRequest) request);

    }

    @ExceptionHandler({UserBadRequestException.class, CategoryBadRequestException.class,
            MarketPlaceBadRequestException.class, ProductBadRequestException.class,
            ItemBadRequestException.class, MappingException.class})
    protected ResponseEntity<Object> handleBadRequestException(Exception ex, WebRequest request) {

        return buildApiErrorDto(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                List.of(ex.getLocalizedMessage()), (ServletWebRequest) request);

    }

    @ExceptionHandler({UserNotFoundException.class, CategoryNotFoundException.class,
            MarketPlaceNotFoundException.class, RoleNotFoundException.class, ProductNotFoundException.class,
            ItemNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFoundException(Exception ex, WebRequest request) {

        return buildApiErrorDto(HttpStatus.NOT_FOUND.value(), "Not found",
                List.of(ex.getLocalizedMessage()), (ServletWebRequest) request);

    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {

        return buildApiErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error",
                List.of("Error occurred"), (ServletWebRequest) request);
    }

    private ResponseEntity<Object> getBindExceptionMessage(BindException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        ApiErrorDto apiErrorDto =
                new ApiErrorDto(HttpStatus.BAD_REQUEST.value(), ex.getLocalizedMessage(), errors,
                        request.getContextPath(),
                        new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));

        return new ResponseEntity<>(apiErrorDto, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> buildApiErrorDto(Integer httpStatusValue, String message, List<String> errors,
                                                    ServletWebRequest request) {
        ApiErrorDto apiError =
                new ApiErrorDto(httpStatusValue, message, errors, request.getRequest().getRequestURI(),
                        new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }
}
