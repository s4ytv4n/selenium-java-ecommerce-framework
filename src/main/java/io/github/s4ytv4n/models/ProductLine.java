package io.github.s4ytv4n.models;

/** A product as shown in the listing or in the cart. Prices are whole rupees. */
public record ProductLine(String name, int price, int quantity, int total) {

    public static ProductLine listed(String name, int price) {
        return new ProductLine(name, price, 1, price);
    }

    public ProductLine withQuantity(int newQuantity) {
        return new ProductLine(name, price, newQuantity, price * newQuantity);
    }
}
