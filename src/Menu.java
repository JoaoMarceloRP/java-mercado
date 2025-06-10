import java.awt.*;
import javax.swing.*;

public class Menu extends JFrame {

    private JLabel lblUsuarioLogado;
    private String nomeUsuario;

    public Menu(String nomeUsuario) {

        this.nomeUsuario = nomeUsuario;

        setTitle("Tela Inicial");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Nome do usuário logado no topo
        this.nomeUsuario = (nomeUsuario == null || nomeUsuario.isEmpty())
        ? "Usuário Desconhecido"
        : nomeUsuario;
        
        lblUsuarioLogado = new JLabel(this.nomeUsuario, SwingConstants.RIGHT);
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.add(lblUsuarioLogado, BorderLayout.EAST);
        add(painelTopo, BorderLayout.NORTH);


        // Painel central com os botões
        JPanel painelCentro = new JPanel(new FlowLayout());


        JButton btnProduto = new JButton("Gerenciar Produtos");
        btnProduto.addActionListener(e -> {
        MenuGrupo grupo = new MenuGrupo(nomeUsuario);
        grupo.setVisible(true);
        dispose();
        });
        painelCentro.add(btnProduto);


        JButton btnUsuarios = new JButton("Gerenciar Usuários");
        btnUsuarios.addActionListener(e -> {
            MenuUsuario menuUsuario = new MenuUsuario(nomeUsuario);
            menuUsuario.setVisible(true);
            dispose();
        });
        painelCentro.add(btnUsuarios);


        JButton btnHistorico = new JButton("Histórico de Compras");
        btnHistorico.addActionListener(e -> {
            HistoricoCompras historico = new HistoricoCompras(MenuGrupo.usuarioIdLogado, nomeUsuario);
            historico.setVisible(true);
            dispose();
        });
        painelCentro.add(btnHistorico);


        add(painelCentro, BorderLayout.CENTER);


        // Painel inferior com o botão Sair
        JButton btnLogout = new JButton("Sair");
        btnLogout.addActionListener(e -> {
        dispose();
        new Login().setVisible(true);
        });

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelInferior.add(btnLogout);
        add(painelInferior, BorderLayout.SOUTH);
    }
}
