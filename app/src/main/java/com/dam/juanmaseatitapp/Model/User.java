package com.dam.juanmaseatitapp.Model;

public class User {
    // Atributos relacionados con los campos de la BD
    private String Name;
    private String Password;
    private String Phone;

    // Constructores
    public User() {}

    public User(String name, String password, String phone) {
        Name = name;
        Password = password;
        Phone = phone;
    }

    public User(String name, String password) {
        Name = name;
        Password = password;
    }

    // Getters y Setters
    public String getName() { return Name; }
    public void setName(String name) { Name = name; }
    public String getPassword() { return Password; }
    public void setPassword(String password) { Password = password; }

    public String getPhone() { return Phone; }
    public void setPhone(String phone) { Phone = phone; }
}
