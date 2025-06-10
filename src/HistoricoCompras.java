import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class HistoricoCompras extends JFrame {

    public HistoricoCompras(int usuarioId, String nomeUsuario) {
        setTitle("Histórico de Compras de " + nomeUsuario);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        String[] colunas = {"Produto", "Preço", "Quantidade", "Data"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tabela);

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT nome_produto, preco, quantidade, data_compra " +
                     "FROM historico_compras WHERE usuario_id = ? ORDER BY data_compra DESC")) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getString("nome_produto"),
                    String.format("R$ %.2f", rs.getDouble("preco")),
                    rs.getInt("quantidade"),
                    rs.getString("data_compra")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar histórico: " + e.getMessage());
        }

        add(scrollPane, BorderLayout.CENTER);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.addActionListener(e -> dispose());

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelInferior.add(btnFechar);
        add(painelInferior, BorderLayout.SOUTH);
    }
}
