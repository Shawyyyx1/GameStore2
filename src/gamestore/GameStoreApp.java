package gamestore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import gamestore.model.Producto;

/**
 * Game Store – aplicación de consola en un único archivo.
 * No requiere base de datos; todos los datos se guardan en memoria.
 */
public class GameStoreApp {

    // --- Datos en memoria --------------------------------------------------
    private final Tienda tienda = new Tienda("Pixel Planet");
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Pedido>  pedidos  = new ArrayList<>();

    private final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        new GameStoreApp().ejecutar();
    }

    private void ejecutar() {
        precargarDatos();

        System.out.println("Bienvenido/a a " + tienda);
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");
            switch (opcion) {
                case 1 -> listarProductos();
                case 2 -> crearPedido();
                case 3 -> listarPedidos();
                case 0 -> System.out.println("¡Hasta pronto!");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    // --------------------- Menú principal ----------------------------------
    private void mostrarMenu() {
        System.out.println("""
                \n===== MENÚ =====
                1. Listar productos
                2. Realizar un nuevo pedido
                3. Ver pedidos realizados
                0. Salir
                """);
    }

    // --------------------- Operaciones -------------------------------------
    private void listarProductos() {
        System.out.println("\n--- Catálogo de productos ---");
        tienda.getProductos().forEach(System.out::println);
    }

    private void crearPedido() {
        Cliente cliente = seleccionarCliente();
        Pedido  pedido  = new Pedido(cliente);

        while (true) {
            listarProductos();
            int idProd = leerEntero("Introduce ID de producto (0 para terminar): ");
            if (idProd == 0) break;

            tienda.buscarPorId(idProd).ifPresentOrElse(prod -> {
                int cantidad = leerEntero("Cantidad: ");
                pedido.agregarProducto(prod, cantidad);
                System.out.println("Producto añadido.\n");
            }, () -> System.out.println("ID no encontrado\n"));
        }

        if (pedido.getTotal() == 0) {
            System.out.println("No se añadieron productos; pedido cancelado.");
        } else {
            pedidos.add(pedido);
            System.out.println("\n" + pedido);
        }
    }

    private void listarPedidos() {
        if (pedidos.isEmpty()) {
            System.out.println("\nNo hay pedidos aún.");
            return;
        }
        System.out.println("\n--- Historial de pedidos ---");
        pedidos.forEach(p -> {
            System.out.println(p);
            System.out.println();
        });
    }

    // --------------------- Helpers -----------------------------------------
    private Cliente seleccionarCliente() {
        System.out.println("\n--- Clientes ---");
        clientes.forEach(System.out::println);
        int id = leerEntero("ID de cliente (0 = nuevo cliente): ");
        if (id == 0) {
            String nombre = leerCadena("Nombre del nuevo cliente: ");
            Cliente nuevo = new Cliente(clientes.size() + 1, nombre);
            clientes.add(nuevo);
            return nuevo;
        }
        return clientes.stream()
                       .filter(c -> c.getId() == id)
                       .findFirst()
                       .orElseGet(() -> {
                           System.out.println("ID no encontrado, creando nuevo.");
                           return seleccionarCliente();
                       });
    }

    private void precargarDatos() {
        tienda.agregarProducto(new Producto(1, "The Witcher 3", 12.99));
        tienda.agregarProducto(new Producto(2, "Hades", 19.99));
        tienda.agregarProducto(new Producto(3, "Elden Ring", 59.90));
        tienda.agregarProducto(new Producto(4, "Stardew Valley", 11.50));

        clientes.add(new Cliente(1, "Ana"));
    }

    private int leerEntero(String mensaje) {
        System.out.print(mensaje);
        while (!sc.hasNextInt()) {
            sc.next(); // descartamos entrada inválida
            System.out.print("Introduce un número: ");
        }
        int val = sc.nextInt();
        sc.nextLine(); // limpiar salto pendiente
        return val;
    }

    private String leerCadena(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine();
    }
}

// ==========================================================================
// Clases de dominio (no públicas) ------------------------------------------
// ==========================================================================


class Cliente {
    private final int id;
    private final String nombre;

    public Cliente(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return String.format("Cliente #%d: %s", id, nombre);
    }
}

class PedidoItem {
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

class Pedido {
    private static int contador = 1;

    private final int idPedido;
    private final Cliente cliente;
    private final LocalDateTime fecha;
    private final List<PedidoItem> items = new ArrayList<>();

    public Pedido(Cliente cliente) {
        this.idPedido = contador++;
        this.cliente = cliente;
        this.fecha = LocalDateTime.now();
    }

    public void agregarProducto(Producto p, int cantidad) {
        Optional<PedidoItem> existente = items.stream()
                .filter(it -> it.getProducto().getId() == p.getId())
                .findFirst();

        if (existente.isPresent()) {
            existente.get().incrementarCantidad(cantidad);
        } else {
            items.add(new PedidoItem(p, cantidad));
        }
    }

    public double getTotal() {
        return items.stream().mapToDouble(PedidoItem::getSubtotal).sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("== Pedido #").append(idPedido)
          .append(" (").append(fecha.withNano(0)).append(") ==")
          .append("\nCliente: ").append(cliente.getNombre())
          .append("\n----------------------------------------\n");
        items.forEach(i -> sb.append(i).append('\n'));
        sb.append("----------------------------------------\n")
          .append(String.format("TOTAL: €%.2f\n", getTotal()));
        return sb.toString();
    }
}

class Tienda {
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

