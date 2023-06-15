package com.dam.juanmaseatitapp.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import com.dam.juanmaseatitapp.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encaragada de administrar la BD SQLite interna. Esta BD tendrá el cometido de gestionar los pedidos
 * y enviarlos a Firebase
 */
public class Database extends SQLiteAssetHelper {
    // Atributos de clase
    private static final String DB_NAME = "EatItDB.db";
    private static final int DB_VER = 1;

    // Constructor
    public Database(Context context) { super(context, DB_NAME, null, DB_VER); }

    // Métodos

    /**
     * Método que nos devolverá el carro de compra completo del usuario, para luego poder enviarlo
     * a la base de datos de Firebase
     * @return Lista con los platos incluidos en el carro de compra
     */
    public List<Order> getCarts() {
        final List<Order> result = new ArrayList<>();

        try {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

            String[] sqlSelect = {"ProductName", "ProductId", "Quantity", "Price", "Discount", "Image"};
            String sqlTable = "OrderDetail";

            qb.setTables(sqlTable);
            Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

            if (c.moveToFirst()) {
                do {
                    result.add(new Order(c.getString(c.getColumnIndex("ProductId")),
                            c.getString(c.getColumnIndex("ProductName")),
                            c.getString(c.getColumnIndex("Quantity")),
                            c.getString(c.getColumnIndex("Price")),
                            c.getString(c.getColumnIndex("Discount")),
                            c.getString(c.getColumnIndex("Image"))));
                } while (c.moveToNext());
            }

        } catch (SQLiteAssetException ex) {
            System.err.println("No se pudo abrir la base de datos " + DB_NAME);
        }

        return result;
    }

    /**
     * Método que permite la acción de añadir un plato de comida al carro de compra
     * @param order Parámetro de tipo Order, el cual hace referencia al plato en cuestión
     */
    public void addToCart(Order order) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount, Image) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');",
                    order.getProductId(),
                    order.getProductName(),
                    order.getQuantity(),
                    order.getPrice(),
                    order.getDiscount(),
                    order.getImage());

        db.execSQL(query);
    }

    /**
     * Este método permitirá limpiar el carro de compra
     * @throws SQLException
     */
    public void cleanCart() throws SQLException {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");

        db.execSQL(query);
    }

    /**
     * Método con la utilidad de añadir platos de comida favoritos, simplemente haciendo clic en
     * el logo correspondiente, el cual se encuentra debajo y a la derecha de cada plato.
     * @param foodId Identificador de cada plato de comida
     */
    public void addFavorites(String foodId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(FoodId) VALUES ('%s');", foodId);
        db.execSQL(query);
    }

    /**
     * Este método nos da la capacidad opuesta al anterior, puesto que nos permite eliminar un
     * plato de la tabla de favoritos, simplemente volviendo a hacer clic sobre un plato ya marcado
     * como favorito
     * @param foodId Identificador de cada plato de comida
     */
    public void removeFromFavorites(String foodId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE FoodId = '%s';", foodId);
        db.execSQL(query);
    }

    /**
     * Método que posee la utilidad de comprobar si un plato de comida tiene el estatus de
     * "Favorito" o no lo tiene, devolviendo un boolean para ello
     * @param foodId Parámetro identificador del plato de comida en cuestión
     * @return Boolean que indicará si el plato de comida es "Favorito" o no lo es
     */
    public boolean isFavorite(String foodId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE FoodId='%s';", foodId);
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }

        cursor.close();
        return true;
    }
}
