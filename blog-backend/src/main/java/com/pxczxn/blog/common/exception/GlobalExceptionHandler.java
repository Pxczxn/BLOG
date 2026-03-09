package com.pxczxn.blog.common.exception;

import com.pxczxn.blog.auth.exception.LoginException;
import com.pxczxn.blog.category.exception.CategoryInUseException;
import com.pxczxn.blog.category.exception.CategoryNotFoundException;
import com.pxczxn.blog.comment.exception.CommentNotFoundException;
import com.pxczxn.blog.comment.exception.ParentCommentInvalidException;
import com.pxczxn.blog.common.response.Result;
import com.pxczxn.blog.content.exception.ArticleNotFoundException;
import com.pxczxn.blog.content.exception.SlugAlreadyExistsException;
import com.pxczxn.blog.tag.exception.TagNotFoundException;
import com.pxczxn.blog.upload.exception.UploadException;
import com.pxczxn.blog.user.exception.UserAlreadyExistsException;
import com.pxczxn.blog.user.exception.UserNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(Result.error(400, "Parameter validation failed", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result<String>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(Result.error(400, "Parameter validation failed", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(Result.error(400, "Parameter validation failed"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Result<Void>> handleNoResourceFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404, "Not Found"));
    }

    @ExceptionHandler({SlugAlreadyExistsException.class, UserAlreadyExistsException.class})
    public ResponseEntity<Result<Void>> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Result.error(409, ex.getMessage()));
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    public ResponseEntity<Result<Void>> handleArticleNotFound(ArticleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404, "Article not found"));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404, "Category not found"));
    }

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<Result<Void>> handleTagNotFound(TagNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404, "Tag not found"));
    }

    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<Result<Void>> handleCategoryInUse(CategoryInUseException ex) {
        return ResponseEntity.badRequest().body(Result.error(400, "Category is in use"));
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<Result<Void>> handleCommentNotFound(CommentNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404, "Comment not found"));
    }

    @ExceptionHandler(ParentCommentInvalidException.class)
    public ResponseEntity<Result<Void>> handleParentInvalid(ParentCommentInvalidException ex) {
        return ResponseEntity.badRequest().body(Result.error(400, "Parent comment invalid"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Result<Void>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(404, ex.getMessage()));
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Result<Void>> handleLoginException(LoginException ex) {
        int code = switch (ex.getErrorCode()) {
            case "USER_NOT_FOUND" -> 401;
            case "INVALID_PASSWORD" -> 402;
            case "ACCOUNT_LOCKED" -> 403;
            case "ACCOUNT_BANNED" -> 404;
            default -> 401;
        };
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error(code, ex.getMessage()));
    }

    @ExceptionHandler(UploadException.class)
    public ResponseEntity<Result<Void>> handleUploadException(UploadException ex) {
        int code = switch (ex.getErrorCode()) {
            case "EMPTY_FILE" -> 400;
            case "FILE_TOO_LARGE" -> 413;
            case "INVALID_FILE_TYPE" -> 415;
            case "INVALID_DIRECTORY" -> 400;
            case "UPLOAD_FAILED" -> 500;
            default -> 500;
        };
        return ResponseEntity.status(code >= 400 && code < 600 ? code : 500)
                .body(Result.error(code, ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Result.error(400, ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Result<Void>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error(401, ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Result<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Database constraint violated: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Result.error(409, "Database constraint violated"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "Internal server error"));
    }
}
