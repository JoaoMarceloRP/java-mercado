import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class Menu extends JFrame {

    private JTable tabelaUsuarios;
    private DefaultTableModel modeloTabela;
    private JButton btnRemoverUsuario;
    private JButton btnEditarUsuario;

    public Menu() {
        setTitle("Menu Principal");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));

        JButton btnAddUsuario = new JButton("Adicionar Usuário");
        btnRemoverUsuario = new JButton("Remover Usuário");
        btnEditarUsuario = new JButton("Editar Usuário");
        btnRemoverUsuario.setEnabled(false);
        btnEditarUsuario.setEnabled(false);

        btnAddUsuario.addActionListener(e -> {
            AddUsuario telaAdd = new AddUsuario();
            telaAdd.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    carregarUsuarios();
                }
            });
            telaAdd.setVisible(true);
        });

        btnRemoverUsuario.addActionListener(e -> removerUsuarioSelecionado());

        btnEditarUsuario.addActionListener(e -> editarUsuarioSelecionado());

        painelBotoes.add(btnAddUsuario);
        painelBotoes.add(Box.createRigidArea(new Dimension(0, 10)));
        painelBotoes.add(btnEditarUsuario);
        painelBotoes.add(Box.createRigidArea(new Dimension(0, 10)));
        painelBotoes.add(btnRemoverUsuario);

        add(painelBotoes, BorderLayout.WEST);

        modeloTabela = new DefaultTableModel(new String[]{"ID", "Usuário", "Senha", "Tipo"}, 0);
        tabelaUsuarios = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
        add(scrollPane, BorderLayout.CENTER);

        tabelaUsuarios.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                boolean linhaSelecionada = !tabelaUsuarios.getSelectionModel().isSelectionEmpty();
                btnRemoverUsuario.setEnabled(linhaSelecionada);
                btnEditarUsuario.setEnabled(linhaSelecionada);
            }
        });

        carregarUsuarios();
    }

    public void carregarUsuarios() {
        modeloTabela.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM usuarios")) {
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("senha"),
                        rs.getInt("tipo")
                        
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removerUsuarioSelecionado() {
        int linhaSelecionada = tabelaUsuarios.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para remover.");
            return;
        }

        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja realmente remover o usuário ID " + id + "?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db");
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM usuarios WHERE id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Usuário removido com sucesso!");
                carregarUsuarios();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao remover usuário.");
            }
        }
    }

    private void editarUsuarioSelecionado() {
        int linhaSelecionada = tabelaUsuarios.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário para editar.");
            return;
        }

        int id = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
        String usuario = (String) modeloTabela.getValueAt(linhaSelecionada, 1);
        String senha = (String) modeloTabela.getValueAt(linhaSelecionada, 2);
        int tipo = (int) modeloTabela.getValueAt(linhaSelecionada, 3);

        System.out.println("Abrindo tela de edição para ID: " + id);

        EditUsuario telaEditar = new EditUsuario(id, usuario, senha, tipo);
        telaEditar.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                carregarUsuarios();
            }
        });

        SwingUtilities.invokeLater(() -> {
            telaEditar.setVisible(true);
            telaEditar.toFront();
        });
    }
}
