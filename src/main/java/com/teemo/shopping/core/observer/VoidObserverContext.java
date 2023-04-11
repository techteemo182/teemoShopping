package com.teemo.shopping.core.observer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoidObserverContext extends ObserverContext {

    public VoidObserverContext empty() {
        return new VoidObserverContext();
    }
}
