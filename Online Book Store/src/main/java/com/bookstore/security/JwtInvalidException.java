package com.bookstore.security;

public class JwtInvalidException extends RuntimeException {
    public JwtInvalidException(String message) { super(message); }
}
