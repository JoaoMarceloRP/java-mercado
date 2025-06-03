import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class Mercado extends JFrame implements ActionListener {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public Mercado() {
        setTitle("Tela de Login");
        setSize(400, 200);
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
        abrirTelaPrincipal();
        dispose();
    }

    // login sim ou nao 
    /*
    public void actionPerformed(ActionEvent e) {
        String usuario = userField.getText();
        String senha = new String(passField.getPassword());

        if (validarLogin(usuario, senha)) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido!");
            abrirTelaPrincipal();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.");
        }
    }
    */

    private boolean validarLogin(String usuario, String senha) {
    String url = "jdbc:sqlite:mercado.db";
    String sql = "SELECT * FROM usuarios WHERE usuario = ? AND senha = ?";

    try (Connection conn = DriverManager.getConnection(url)) {
        System.out.println("✅ Conectado ao banco com sucesso!");

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, usuario);
            stmt.setString(2, senha);

            System.out.println("Usuário digitado: " + usuario);
            System.out.println("Senha digitada: " + senha);

            ResultSet rs = stmt.executeQuery();
            boolean encontrado = rs.next();
            System.out.println("Encontrou usuário? " + encontrado);
            return encontrado;
        }

    } catch (SQLException ex) {
        System.err.println("❌ Erro ao conectar no banco: " + ex.getMessage());
        ex.printStackTrace();
        return false;
    }
}

    private void abrirTelaPrincipal() {
    SwingUtilities.invokeLater(() -> {
        new Menu().setVisible(true);
    });
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Mercado().setVisible(true));
    }
}
