package Vistas;

import Controlador.DatabaseManager;
import Entidad.Continente;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Montero
 */
public class AñadirContinentes extends JDialog {

    private final DatabaseManager managerDatabase;
    private final JTextField inputTextContinentes;

    public AñadirContinentes(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.managerDatabase = new DatabaseManager();
        this.inputTextContinentes = new JTextField();

        configurarVentana();
        construirFormulario();
    }

    private void configurarVentana() {
        setTitle("Gestor de continentes");
        getContentPane().setBackground(new java.awt.Color(241, 250, 242));
        setLayout(new BorderLayout(8, 8));
        setSize(420, 220);
        setLocationRelativeTo(getParent());
    }

    private void construirFormulario() {
        JPanel panelCentro = new JPanel(new GridLayout(3, 1, 6, 6));
        panelCentro.add(new JLabel("Registrar continente"));
        panelCentro.add(inputTextContinentes);

        JButton btnGuardar = new JButton("Guardar continente");
        btnGuardar.addActionListener(evt -> guardarContinente());
        panelCentro.add(btnGuardar);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(evt -> dispose());

        add(panelCentro, BorderLayout.CENTER);
        add(btnCerrar, BorderLayout.SOUTH);
    }

    private void guardarContinente() {
        String nombreContinente = inputTextContinentes.getText().trim();

        if (!managerDatabase.runMongoDatabase()) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con la base de datos.");
            return;
        }

        List<Continente> listaContinente = managerDatabase.getListaDeContinentes();
        boolean existe = listaContinente.stream().anyMatch(c -> c.getName().equalsIgnoreCase(nombreContinente));

        if (nombreContinente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes escribir un nombre válido para el continente.");
            managerDatabase.closeMongoDatabase();
            return;
        }

        if (existe) {
            JOptionPane.showMessageDialog(this, "Ese continente ya está registrado.");
            managerDatabase.closeMongoDatabase();
            return;
        }

        managerDatabase.añadirContinentes(new Continente(nombreContinente));
    }
}
