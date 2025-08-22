package com.example.substracker.API;

public class ApiException extends RuntimeException{
    public ApiException(String message){
        super(message);
    }
}
