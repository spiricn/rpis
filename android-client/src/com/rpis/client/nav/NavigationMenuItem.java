
package com.rpis.client.nav;

public class NavigationMenuItem {
    public NavigationMenuItem(String name, Class<?> clazz) {
        this.clazz = clazz;
        this.name = name;
    }

    public Class<?> clazz = null;
    public String name = "";
}
