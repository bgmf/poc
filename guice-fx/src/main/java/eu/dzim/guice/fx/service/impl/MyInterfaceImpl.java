package eu.dzim.guice.fx.service.impl;

import eu.dzim.guice.fx.service.MyInterface;

public class MyInterfaceImpl implements MyInterface {
    @Override
    public void doSomething() {
        System.out.println("It works!");
    }
}