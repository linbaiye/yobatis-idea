package org.nalby.yobatis.idea.ui;

public abstract class Command<T> {

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T t) {
        this.result = t;
    }

}
