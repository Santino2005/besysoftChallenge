package com.besysoft.common.result;

public record CorrectResult<T>(T value) implements Result<T> {
    @Override
    public boolean isCorrect() {
        return true;
    }
}


