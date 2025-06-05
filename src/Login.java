import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class Login extends JFrame implements ActionListener {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public Login() {
        setTitle("Tela de Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel userLabel = new JLabel("Usuário:");
        userField = new JTextField(15);

        JLabel passLabel = new JLabel("Senha:");
        passField = new JPasswordField(15);

        loginButton = new JButton("Entrar");
        loginButton.addActionListener(this);

        gbc.gridx = 0; gbc.gridy = 0;
        add(userLabel, gbc);
        gbc.gridx = 1;
        add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(passLabel, gbc);
        gbc.gridx = 1;
        add(passField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        add(loginButton, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String usuario = userField.getText();
        String senha = new String(passField.getPassword());

        String nomeUsuario = validarLogin(usuario, senha);
        if (nomeUsuario != null) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido!");
            abrirTelaPrincipal(nomeUsuario);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.");
        }
    }

    private String validarLogin(String usuario, String senha) {
        String url = "jdbc:sqlite:mercado.db";
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND senha = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("usuario");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private void abrirTelaPrincipal(String nomeUsuario) {
        SwingUtilities.invokeLater(() -> {
            new Menu(nomeUsuario).setVisible(true);
        });
    }

    private static void garantirAdmin() {
        String url = "jdbc:sqlite:mercado.db";

        String criarTabela = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario TEXT NOT NULL UNIQUE," +
                "senha TEXT NOT NULL," +
                "tipo TEXT NOT NULL)"; 

        String checarAdmin = "SELECT COUNT(*) AS total FROM usuarios WHERE usuario = 'admin'";
        String inserirAdmin = "INSERT INTO usuarios (usuario, senha, tipo) VALUES ('admin', '1234', 'admin')";

        try (Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement()) {

            stmt.execute(criarTabela);

            try (ResultSet rs = stmt.executeQuery(checarAdmin)) {
                if (rs.next() && rs.getInt("total") == 0) {
                    stmt.executeUpdate(inserirAdmin);
                } 
           }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        garantirAdmin();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
