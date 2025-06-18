package gamestore.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Tienda {
    private final String nombre;
    private final List<Producto> productos = new ArrayList<>();

    public Tienda(String nombre) { this.nombre = nombre; }

    public void agregarProducto(Producto p) { productos.add(p); }

    public List<Producto> getProductos() { return productos; }

    public Optional<Producto> buscarPorId(int id) {
        return productos.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public String toString() { return nombre; }
}
