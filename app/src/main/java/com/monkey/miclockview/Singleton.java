package com.monkey.miclockview;

/**
 * Created by a123 on 2017/8/3.
 */

public class Singleton{

    private static Singleton singleton;
    private Singleton(){}
    public static Singleton getInstance(){
        if( singleton==null){
            singleton=new Singleton();
        }
        return singleton;
    }
}
