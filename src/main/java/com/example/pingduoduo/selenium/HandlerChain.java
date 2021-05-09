package com.example.pingduoduo.selenium;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HandlerChain implements Handler {

    private List<Handler> handlers = new ArrayList<>();
    private int index = 0;

    public HandlerChain addHandler(Handler handler) {
        handlers.add(handler);
        return this;
    }

    @Override
    public void doHandler(HandlerChain handlerChain) {
        if (index == handlers.size()) {
            return;
        }
        Handler next = handlers.get(index);
        index++;
        try {
            next.doHandler(handlerChain);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

    }
}
