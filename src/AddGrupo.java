import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class AddGrupo extends JFrame {

    private JTextField nomeGrupoField;
    private JButton adicionarButton;

    public AddGrupo() {
        setTitle("Adicionar Grupo de Produtos");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel tituloLabel = new JLabel("Novo Grupo de Produtos", JLabel.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(tituloLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Nome do Grupo:"), gbc);
        nomeGrupoField = new JTextField(20);
        gbc.gridx = 1;
        add(nomeGrupoField, gbc);

        adicionarButton = new JButton("Adicionar Grupo");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(adicionarButton, gbc);

        adicionarButton.addActionListener(e -> adicionarGrupo());
    }

    private void adicionarGrupo() {
        String nomeGrupo = nomeGrupoField.getText().trim();

        if (nomeGrupo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o nome do grupo.");
            return;
        }

        String url = "jdbc:sqlite:mercado.db";
        String checkSql = "SELECT COUNT(*) FROM grupo_produtos WHERE nome = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, nomeGrupo);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Esse grupo já existe. Escolha outro nome.");
                return;
            }

            String insertSql = "INSERT INTO grupo_produtos (nome) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, nomeGrupo);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Grupo adicionado com sucesso!");
                nomeGrupoField.setText("");
                dispose();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar grupo: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
