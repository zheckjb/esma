package com.bib.esma;

public class T24Test {

    public static void main(String[] arg) {
        System.out.println("This is main method");
        System.out.println("This is first argument passed " + arg[0]);
        System.out.println("This is second argument passed " + arg[1]);
    }

    public String MyMethod (String arg){
        System.out.println("This is simple method");
        return arg;
    }
    public static String MyStaticMethod (String arg) {
        System.out.println("This is static method");
        return arg;
    }

}