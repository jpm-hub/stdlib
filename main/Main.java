package main;
import jpm.jpm;

public class Main {
    public static void main(String[] args) {
        var j = jpm.require("jpm.JSON");
        var l = (String) j.call("list", String[].class,new String[]{ "hi", "bye"});
        System.out.println(l);
    }
}
