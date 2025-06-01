import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Mercado extends JFrame implements ActionListener {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public Mercado() {
        setTitle("Tela de Login");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 2, 10, 10));

        JLabel userLabel = new JLabel("Usuário:");
        userField = new JTextField();

        JLabel passLabel = new JLabel("Senha:");
        passField = new JPasswordField();

        loginButton = new JButton("Entrar");
        loginButton.addActionListener(this);

        add(userLabel);
        add(userField);
        add(passLabel);
        add(passField);
        add(new JLabel());
        add(loginButton);
    }

    public void actionPerformed(ActionEvent e) {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if ("admin".equals(username) && "1234".equals(password)) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido!");
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Mercado().setVisible(true);
        });
    }
}