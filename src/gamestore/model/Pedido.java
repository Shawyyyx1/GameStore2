package gamestore.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Pedido {
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
