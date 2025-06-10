import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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

        JButton btnComprar = new JButton("Finalizar Compra");
        btnComprar.addActionListener(e -> finalizarCompra(itensCarrinho));

        JPanel painelInferior = new JPanel(new BorderLayout());
        painelInferior.add(lblTotal, BorderLayout.CENTER);
        painelInferior.add(btnComprar, BorderLayout.EAST);

        add(painelInferior, BorderLayout.SOUTH);
    }

    private void finalizarCompra(List<Object[]> itensCarrinho) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db")) {
            try (Statement pragma = conn.createStatement()) {
                pragma.execute("PRAGMA busy_timeout = 5000;");
            }

            conn.setAutoCommit(false);

            for (Object[] item : itensCarrinho) {
                int produtoId = (int) item[0];
                String nome = (String) item[1];
                double preco = (double) item[2];
                int quantidade = (int) item[3];

                try (PreparedStatement updateEstoque = conn.prepareStatement(
                        "UPDATE produtos SET quantidade = quantidade - ? WHERE id = ? AND quantidade >= ?")) {
                    updateEstoque.setInt(1, quantidade);
                    updateEstoque.setInt(2, produtoId);
                    updateEstoque.setInt(3, quantidade);

                    int rows = updateEstoque.executeUpdate();

                    if (rows == 0) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "Estoque insuficiente para o produto: " + nome);
                        return;
                    }
                }

                try (PreparedStatement insertHistorico = conn.prepareStatement(
                        "INSERT INTO historico_compras (usuario_id, produto_id, nome_produto, preco, quantidade) VALUES (?, ?, ?, ?, ?)")) {
                    insertHistorico.setInt(1, MenuGrupo.usuarioIdLogado);
                    insertHistorico.setInt(2, produtoId);
                    insertHistorico.setString(3, nome);
                    insertHistorico.setDouble(4, preco);
                    insertHistorico.setInt(5, quantidade);
                    insertHistorico.executeUpdate();
                }
            }

            conn.commit();
            JOptionPane.showMessageDialog(this, "Compra finalizada com sucesso!");
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar compra: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
