import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class EditGrupo extends JFrame {

    private int grupoId;
    private JTextField nomeGrupoField;
    private JButton salvarButton;

    public EditGrupo(int grupoId, String nomeAtual) {
        this.grupoId = grupoId;

        setTitle("Editar Grupo de Produtos");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel tituloLabel = new JLabel("Editar Grupo", JLabel.CENTER);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(tituloLabel, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Novo Nome:"), gbc);
        nomeGrupoField = new JTextField(20);
        nomeGrupoField.setText(nomeAtual);
        gbc.gridx = 1;
        add(nomeGrupoField, gbc);

        salvarButton = new JButton("Salvar Alterações");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(salvarButton, gbc);

        salvarButton.addActionListener(e -> salvarAlteracoes());
    }

    private void salvarAlteracoes() {
        String novoNome = nomeGrupoField.getText().trim();

        if (novoNome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do grupo não pode estar vazio.");
            return;
        }

        String url = "jdbc:sqlite:mercado.db";
        String checkSql = "SELECT COUNT(*) FROM grupo_produtos WHERE nome = ? AND id != ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, novoNome);
            checkStmt.setInt(2, grupoId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Já existe um grupo com esse nome.");
                return;
            }

            String updateSql = "UPDATE grupo_produtos SET nome = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, novoNome);
                stmt.setInt(2, grupoId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Grupo atualizado com sucesso!");
                dispose();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar grupo: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
