import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class AddUsuario extends JFrame {

    private JTextField novoUsuarioField;
    private JPasswordField novaSenhaField;
    private JTextField tipoField;
    private JButton adicionarButton;
    private JComboBox<String> tipoComboBox;

    public AddUsuario() {
        setTitle("Área do Sistema");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel bemVindoLabel = new JLabel("Bem-vindo ao sistema!", JLabel.CENTER);
        bemVindoLabel.setFont(new Font("Arial", Font.BOLD, 18));

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(bemVindoLabel, gbc);

        gbc.gridwidth = 1;

        JLabel userLabel = new JLabel("Novo Usuário:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(userLabel, gbc);

        novoUsuarioField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(novoUsuarioField, gbc);

        JLabel senhaLabel = new JLabel("Nova Senha:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(senhaLabel, gbc);

        novaSenhaField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(novaSenhaField, gbc);

        JLabel tipoLabel = new JLabel("Tipo:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(tipoLabel, gbc);

        tipoComboBox = new JComboBox<>(new String[]{"Usuário", "Administrador"});
        gbc.gridx = 1;
        add(tipoComboBox, gbc);

        tipoField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(tipoField, gbc);

        adicionarButton = new JButton("Adicionar Usuário");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        add(adicionarButton, gbc);


        adicionarButton.addActionListener(e -> adicionarUsuario());
    }

        private void adicionarUsuario() {
        String novoUsuario = novoUsuarioField.getText().trim();
        String novaSenha = new String(novaSenhaField.getPassword()).trim();
        int tipo = tipoComboBox.getSelectedIndex();

        if (novoUsuario.isEmpty() || novaSenha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        String url = "jdbc:sqlite:mercado.db";
        String sql = "INSERT INTO usuarios (usuario, senha, tipo) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoUsuario);
            stmt.setString(2, novaSenha);
            stmt.setInt(3, tipo);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuário adicionado com sucesso!");
            novoUsuarioField.setText("");
            novaSenhaField.setText("");
            tipoField.setText("");
            tipoComboBox.setSelectedIndex(0);
            dispose();

            } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao adicionar usuário: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
