import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

public class MenuProduto extends JFrame {

    private JTable tabelaProdutos;
    private DefaultTableModel modeloTabela;
    private JButton btnEditar, btnRemover;
    private int grupoId;
    private String grupoNome;
    private String nomeUsuario;
    private java.util.List<Object[]> carrinho = new java.util.ArrayList<>();

    public MenuProduto(int grupoId, String grupoNome, String nomeUsuario) {
        this.grupoId = grupoId;
        this.grupoNome = grupoNome;
        this.nomeUsuario = nomeUsuario;

        setTitle("Produtos do Grupo: " + grupoNome);
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        modeloTabela = new DefaultTableModel(new String[]{"ID", "Nome", "Preço", "Quantidade"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaProdutos = new JTable(modeloTabela);
        tabelaProdutos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tabelaProdutos);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdicionar = new JButton("Adicionar Produto");
        btnEditar = new JButton("Editar Produto");
        btnRemover = new JButton("Remover Produto");
        JButton btnVoltar = new JButton("Voltar");

        JPanel painelCarrinho = new JPanel();
        painelCarrinho.setLayout(new BoxLayout(painelCarrinho, BoxLayout.Y_AXIS));
        painelCarrinho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // espaçamento

        JButton btnCarrinho = new JButton("Abrir Carrinho");
        JButton btnAdicionarCarrinho = new JButton("Adicionar ao Carrinho");
        btnAdicionarCarrinho.setEnabled(false);

        painelCarrinho.add(btnCarrinho);
        painelCarrinho.add(Box.createVerticalStrut(10));
        painelCarrinho.add(btnAdicionarCarrinho);

        add(painelCarrinho, BorderLayout.EAST);

        btnEditar.setEnabled(false);
        btnRemover.setEnabled(false);

        btnAdicionar.addActionListener(e -> abrirFormularioProduto(null));
        btnEditar.addActionListener(e -> editarProdutoSelecionado());
        btnRemover.addActionListener(e -> removerProdutoSelecionado());

        btnVoltar.addActionListener(e -> {
            if (MenuGrupo.instanciaAberta != null) {
                MenuGrupo.instanciaAberta.toFront();
                MenuGrupo.instanciaAberta.setVisible(true);
            } else {
                new MenuGrupo(nomeUsuario).setVisible(true);
            }
        dispose();
        });

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);
        painelBotoes.add(btnVoltar);
        add(painelBotoes, BorderLayout.SOUTH);

        tabelaProdutos.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            boolean selecionado = !tabelaProdutos.getSelectionModel().isSelectionEmpty();
            btnEditar.setEnabled(selecionado);
            btnRemover.setEnabled(selecionado);
            btnAdicionarCarrinho.setEnabled(selecionado);
        });

        btnCarrinho.addActionListener(e -> {
            if (carrinho.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Carrinho vazio.");
                return;
            }
            new Carrinho(carrinho).setVisible(true);
            });

        carregarProdutos();

        btnAdicionarCarrinho.addActionListener(e -> {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha != -1) {
            Object[] produto = new Object[4];
            for (int i = 0; i < 4; i++) {
                produto[i] = modeloTabela.getValueAt(linha, i);
            }
            boolean jaExiste = carrinho.stream().anyMatch(p -> (int) p[0] == (int) produto[0]);
            if (!jaExiste) {
                carrinho.add(produto);
                JOptionPane.showMessageDialog(this, "Produto adicionado ao carrinho.");
            } else {
                JOptionPane.showMessageDialog(this, "Produto já está no carrinho.");
            }
            }
        });
    }

    private void carregarProdutos() {
        modeloTabela.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM produtos WHERE grupo_id = ?")) {
            stmt.setInt(1, grupoId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                modeloTabela.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getInt("quantidade")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage());
        }
    }

    private void abrirFormularioProduto(Integer produtoId) {
        JFrame form = new JFrame(produtoId == null ? "Adicionar Produto" : "Editar Produto");
        form.setSize(300, 250);
        form.setLocationRelativeTo(this);
        form.setLayout(new GridLayout(5, 2, 5, 5));

        JTextField campoNome = new JTextField();
        JTextField campoPreco = new JTextField();
        JTextField campoQtd = new JTextField();

        if (produtoId != null) {
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db");
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM produtos WHERE id = ?")) {
                stmt.setInt(1, produtoId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    campoNome.setText(rs.getString("nome"));
                    campoPreco.setText(String.valueOf(rs.getDouble("preco")));
                    campoQtd.setText(String.valueOf(rs.getInt("quantidade")));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar produto: " + e.getMessage());
            }
        }

        form.add(new JLabel("Nome:"));
        form.add(campoNome);
        form.add(new JLabel("Preço:"));
        form.add(campoPreco);
        form.add(new JLabel("Quantidade:"));
        form.add(campoQtd);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.addActionListener(e -> {
            String nome = campoNome.getText();
            double preco = Double.parseDouble(campoPreco.getText());
            int quantidade = Integer.parseInt(campoQtd.getText());

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db")) {
                if (produtoId == null) {
                    PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO produtos (nome, preco, quantidade, grupo_id) VALUES (?, ?, ?, ?)");
                    stmt.setString(1, nome);
                    stmt.setDouble(2, preco);
                    stmt.setInt(3, quantidade);
                    stmt.setInt(4, grupoId);
                    stmt.executeUpdate();
                } else {
                    PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE produtos SET nome = ?, preco = ?, quantidade = ? WHERE id = ?");
                    stmt.setString(1, nome);
                    stmt.setDouble(2, preco);
                    stmt.setInt(3, quantidade);
                    stmt.setInt(4, produtoId);
                    stmt.executeUpdate();
                }
                carregarProdutos();
                form.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(form, "Erro ao salvar produto: " + ex.getMessage());
            }
        });

        btnCancelar.addActionListener(e -> form.dispose());

        form.add(btnSalvar);
        form.add(btnCancelar);
        form.setVisible(true);
    }

    private void editarProdutoSelecionado() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha != -1) {
            int produtoId = (int) modeloTabela.getValueAt(linha, 0);
            abrirFormularioProduto(produtoId);
        }
    }

    private void removerProdutoSelecionado() {
        int linha = tabelaProdutos.getSelectedRow();
        if (linha != -1) {
            int id = (int) modeloTabela.getValueAt(linha, 0);
            String nome = (String) modeloTabela.getValueAt(linha, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                "Remover produto \"" + nome + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:mercado.db");
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM produtos WHERE id = ?")) {
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    carregarProdutos();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Erro ao remover produto: " + e.getMessage());
                }
            }
        }
    }
}
