package com.work.work;

import java.sql.Driver;
import java.sql.DriverManager;

public class ConnectionTest {
    public static void main(String[] args) {
        try {
            // 1. 显式加载驱动
            Class.forName("com.kingbase8.Driver");
            
            // 2. 获取驱动实例
            Driver driver = DriverManager.getDriver("jdbc:kingbase://localhost:54321/test");
            System.out.println("Driver loaded: " + driver.getClass().getName());
            
            // 3. 测试URL接受性
            System.out.println("Accepts URL? " + 
                driver.acceptsURL("jdbc:kingbase://localhost:54321/test"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
