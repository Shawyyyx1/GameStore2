package gamestore.model;

public class PedidoItem {
    private final Producto producto;
    private int cantidad;

    public PedidoItem(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return cantidad * producto.getPrecio(); }

    public void incrementarCantidad(int extra) { this.cantidad += extra; }

    @Override
    public String toString() {
        return String.format("%dx %-25s = €%.2f", cantidad, producto.getNombre(), getSubtotal());
    }
}
