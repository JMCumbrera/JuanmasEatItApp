package com.dam.juanmaseatitapp.Model;

/**
 * Clase encargada de representar las categorías en las que los diversos platos del restaurante
 * pueden entrar
 */
public class Category {
    // Atributos relacionados con los campos de la BD
    private String Name;
    private String Image;

    // Constructores
    public Category() {}

    public Category(String name , String image) {
        this.Name = name;
        this.Image = image;
    }

    // Getters y Setters
    public String getName() { return Name; }
    public void setName(String name) { this.Name = name; }
    public String getImage() { return Image; }
    public void setImage(String image) { this.Image = image; }
}