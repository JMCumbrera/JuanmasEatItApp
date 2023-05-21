package com.dam.juanmaseatitapp.Model;

/**
 * Clase que da vida al banner rotatorio que muestra ciertos platos, en el Home, encima de las
 * categorías de comida.
 */
public class Banner {
    // Atributos
    private String id, name, image;

    // Constructores
    public Banner() {}

    public Banner(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
