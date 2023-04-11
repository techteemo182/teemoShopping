package com.teemo.shopping.core.observer;

public interface Observer<C extends ObserverContext> {

    void onUpdate(C context);
}
