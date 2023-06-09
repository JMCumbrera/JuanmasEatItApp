package com.dam.juanmaseatitapp.Model;

/**
 * Clase encargada de los pedidos de los clientes
 */
public class Order {
    // Atributos pertenecientes a la BD
    private String ProductId, ProductName, Quantity, Price, Discount, Image;

    // Constructores
    public Order() {}

    public Order(String productId, String productName, String quantity, String price, String discount, String image) {
        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        Image = image;
    }

    // Getters y Setters
    public String getProductId() { return ProductId; }
    public void setProductId(String productId) { ProductId = productId; }

    public String getProductName() { return ProductName; }
    public void setProductName(String productName) { ProductName = productName; }

    public String getQuantity() { return Quantity; }
    public void setQuantity(String quantity) { Quantity = quantity; }

    public String getPrice() { return Price; }
    public void setPrice(String price) { Price = price; }

    public String getDiscount() { return Discount; }
    public void setDiscount(String discount) { Discount = discount; }

    public String getImage() { return Image; }
    public void setImage(String image) { Image = image; }

    // Métodos
    public String getProductNameFromId(String productId) {
        if (productId.equals(this.ProductId))
            return this.ProductName;
        else
            return "Not Found!";
    }
}
