/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author rogvold
 */
public class Entity {

    private String name;
    private String address;
    private String info;
    private int age;

    public Entity(String name, String address, String info, int age) {
        this.name = name;
        this.address = address;
        this.info = info;
        this.age = age;
        System.out.println("name = " + name);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
