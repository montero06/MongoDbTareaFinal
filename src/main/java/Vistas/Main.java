package Vistas;

import Controlador.DatabaseManager;
import Entidad.Continente;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

/**
 *
 * @author Montero
 */
public class Main extends JFrame {

    private final DatabaseManager managerDatabase;
    private final JLabel labelImagen;

    public Main() {
        this.managerDatabase = new DatabaseManager();
        this.labelImagen = new JLabel("", SwingConstants.CENTER);

        configurarVentana();
        construirMenu();
        cargarImagenPrincipal();
    }

    private void configurarVentana() {
        setTitle("GeoGestor - Montero");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new java.awt.Color(245, 248, 255));
        add(labelImagen, BorderLayout.CENTER);
        setSize(700, 480);
        setLocationRelativeTo(null);
    }

    private void construirMenu() {
        JMenuBar barra = new JMenuBar();
        JMenu menuPrincipal = new JMenu("Panel principal");

        JMenuItem itemContinentes = new JMenuItem("Gestionar continentes");
        itemContinentes.addActionListener(evt -> new AñadirContinentes(this, true).setVisible(true));

        JMenuItem itemPaises = new JMenuItem("Gestionar países");
        itemPaises.addActionListener(evt -> abrirVentanaConContinentes(Paises.class));

        JMenuItem itemConsultas = new JMenuItem("Resumen por continente");
        itemConsultas.addActionListener(evt -> abrirVentanaConContinentes(Consultas.class));

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(evt -> {
            dispose();
            System.exit(0);
        });

        menuPrincipal.add(itemContinentes);
        menuPrincipal.add(itemPaises);
        menuPrincipal.add(itemConsultas);
        menuPrincipal.addSeparator();
        menuPrincipal.add(itemSalir);

        barra.add(menuPrincipal);
        setJMenuBar(barra);
    }

    private void abrirVentanaConContinentes(Class<?> tipoVentana) {
        if (!managerDatabase.runMongoDatabase()) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con la base de datos.");
            return;
        }

        List<Continente> continentes = managerDatabase.getListaDeContinentes();
        managerDatabase.closeMongoDatabase();

        if (continentes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay continentes registrados.");
            return;
        }

        if (tipoVentana == Paises.class) {
            new Paises(this, true).setVisible(true);
        } else {
            new Consultas(this, true).setVisible(true);
        }
    }

    private void cargarImagenPrincipal() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/img/fotojavi.jpeg"));
        labelImagen.setIcon(icon);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Main().setVisible(true));
    }
}
