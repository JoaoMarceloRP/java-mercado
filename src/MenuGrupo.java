import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

public class MenuGrupo extends JFrame {

    private static MenuGrupo instanciaAberta = null;

    private JTable tabelaGrupos;
    private DefaultTableModel modeloTabela;
    private JButton btnRemoverGrupo;
    private JButton btnEditarGrupo;

    public MenuGrupo() {
        if (instanciaAberta != null) {
            instanciaAberta.toFront();
            return;
        }
        instanciaAberta = this;

        setTitle("Grupos de Produtos");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                instanciaAberta = null;
            }
        });

        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.Y_AXIS));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnAddGrupo = new JButton("Adicionar Grupo");
        btnRemoverGrupo = new JButton("Remover Grupo");
        btnEditarGrupo = new JButton("Editar Grupo");

        btnRemoverGrupo.setEnabled(false);
        btnEditarGrupo.setEnabled(false);

        btnAddGrupo.addActionListener(e -> {
            AddGrupo telaAdd = new AddGrupo();
            telaAdd.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    carregarGrupos();
                }
            });
            telaAdd.setVisible(true);
        });

        btnRemoverGrupo.addActionListener(e -> removerGrupoSelecionado());
        btnEditarGrupo.addActionListener(e -> editarGrupoSelecionado());

        painelBotoes.add(btnAddGrupo);
        painelBotoes.add(Box.createVerticalStrut(10));
        painelBotoes.add(btnEditarGrupo);
        painelBotoes.add(Box.createVerticalStrut(10));
        painelBotoes.add(btnRemoverGrupo);

        add(painelBotoes, BorderLayout.WEST);

        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome do Grupo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaGrupos = new JTable(modeloTabela);
        tabelaGrupos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tabelaGrupos);
        add(scrollPane, BorderLayout.CENTER);

        tabelaGrupos.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            boolean selecionado = !tabelaGrupos.getSelectionModel().isSelectionEmpty();
            btnRemoverGrupo.setEnabled(selecionado);
            btnEditarGrupo.setEnabled(selecionado);
        });

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVoltar = new JButton("Voltar ao Menu");
        btnVoltar.addActionListener(e -> {
            new Menu("Nome do Usuário").setVisible(true);
            dispose();
        });
        painelInferior.add(btnVoltar);
        add(painelInferior, BorderLayout.SOUTH);

        carregarGrupos();
    }

    private void carregarGrupos() {
        modeloTabela.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM grupo_produtos")) {
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("nome")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar grupos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void removerGrupoSelecionado() {
        int linha = tabelaGrupos.getSelectedRow();
        if (linha == -1) return;

        int id = (int) modeloTabela.getValueAt(linha, 0);
        String nome = (String) modeloTabela.getValueAt(linha, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja remover o grupo \"" + nome + "\" (ID: " + id + ")?",
                "Confirmar remoção", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db");
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM grupo_produtos WHERE id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                carregarGrupos();
                JOptionPane.showMessageDialog(this, "Grupo removido com sucesso.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao remover grupo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void editarGrupoSelecionado() {
        int linha = tabelaGrupos.getSelectedRow();
        if (linha == -1) return;

        int id = (int) modeloTabela.getValueAt(linha, 0);
        String nome = (String) modeloTabela.getValueAt(linha, 1);

        EditGrupo telaEditar = new EditGrupo(id, nome);
        telaEditar.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                carregarGrupos();
            }
        });

        SwingUtilities.invokeLater(() -> telaEditar.setVisible(true));
    }
}
