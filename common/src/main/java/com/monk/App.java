package com.monk;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String msg = "[127.0.0.1:9090] 说: 我是3";

        System.out.println(msg.replaceAll("\\[(.*)\\]", "自己"));
    }
}
