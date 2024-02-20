/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Class;

public class Employee {
    private int id;
    private String name;
    private String email;
    private String contact;

    public Employee(int id, String name, String email, String contact) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "name: " + name + ", email: " + email + ", contact: " + contact;
    }
}