import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class EditUsuario extends JFrame {

    private int idUsuario;
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JComboBox<String> tipoComboBox;
    private JButton btnSalvar;

    public EditUsuario(int id, String usuario, String senha, int tipo) {
        this.idUsuario = id;

        setTitle("Editar Usuário");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Usuário:"), gbc);
        txtUsuario = new JTextField(15);
        txtUsuario.setText(usuario);
        gbc.gridx = 1;
        add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Senha:"), gbc);
        txtSenha = new JPasswordField(15);
        txtSenha.setText(senha);
        gbc.gridx = 1;
        add(txtSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Tipo:"), gbc);
        tipoComboBox = new JComboBox<>(new String[]{"Usuário", "Administrador"});
        tipoComboBox.setSelectedIndex(tipo);
        gbc.gridx = 1;
        add(tipoComboBox, gbc);

        btnSalvar = new JButton("Salvar");
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(btnSalvar, gbc);

        btnSalvar.addActionListener(e -> salvarEdicao());
    }

    private void salvarEdicao() {
        String novoUsuario = txtUsuario.getText().trim();
        String novaSenha = new String(txtSenha.getPassword()).trim();
        int novoTipo = tipoComboBox.getSelectedIndex();

        if (novoUsuario.isEmpty() || novaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db");
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE usuarios SET usuario = ?, senha = ?, tipo = ? WHERE id = ?")) {

            stmt.setString(1, novoUsuario);
            stmt.setString(2, novaSenha);
            stmt.setInt(3, novoTipo);
            stmt.setInt(4, idUsuario);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao atualizar usuário.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao atualizar no banco.");
        }
    }
}
