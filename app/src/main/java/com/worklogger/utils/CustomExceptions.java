package com.worklogger.utils;

public class CustomExceptions{
    public void throwNoNetWorkException() throws NoNetWorkException {
        throw new NoNetWorkException();
    }
}

class NoNetWorkException extends Exception {
    NoNetWorkException() {
        super("Make Sure You Have Proper Internet Connection");
    }
}

