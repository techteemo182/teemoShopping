package com.teemo.shopping.core.observer;

public interface Subject<C extends ObserverContext, O extends Observer<C>> {

    void registerObserver(O observer);

    void unregisterObserver(O observer);

    void notifyObservers(C context);
}
