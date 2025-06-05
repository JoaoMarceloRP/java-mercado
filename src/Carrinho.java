import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Carrinho extends JFrame {

    public Carrinho(List<Object[]> itensCarrinho) {
        setTitle("Carrinho de Compras");
        setSize(600, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] colunas = {"ID", "Nome", "Preço", "Quantidade"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);

        double total = 0;

        for (Object[] item : itensCarrinho) {
            modelo.addRow(item);
            double preco = (double) item[2];
            int qtd = (int) item[3];
            total += preco * qtd;
        }

        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JLabel lblTotal = new JLabel("Total: R$ " + String.format("%.2f", total));
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTotal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTotal, BorderLayout.SOUTH);
    }
}
