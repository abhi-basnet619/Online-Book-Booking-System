package com.bookstore.security;

public class JwtExpiredException extends RuntimeException {
    public JwtExpiredException(String message) { super(message); }
}
