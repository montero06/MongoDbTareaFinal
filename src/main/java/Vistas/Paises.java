package Vistas;

import Controlador.DatabaseManager;
import Entidad.Continente;
import Entidad.Pais;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Montero
 */
public class Paises extends JDialog {

    private final DatabaseManager managerDatabase;

    private final JComboBox<String> comboBoxContinentes;
    private final JTextField inputTextNumHabitantes;
    private final JTextField inputTextNombrePais;
    private final JComboBox<String> comboBoxPaises;

    public Paises(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.managerDatabase = new DatabaseManager();

        this.comboBoxContinentes = new JComboBox<>();
        this.inputTextNumHabitantes = new JTextField();
        this.inputTextNombrePais = new JTextField();
        this.comboBoxPaises = new JComboBox<>();

        configurarVentana();
        construirFormulario();
        inicializarComboBoxContinente();
        inicializarComboBoxPaises();
    }

    private void configurarVentana() {
        setTitle("Gestión de países");
        getContentPane().setBackground(new java.awt.Color(255, 247, 240));
        setLayout(new BorderLayout(8, 8));
        setSize(650, 300);
        setLocationRelativeTo(getParent());
    }

    private void construirFormulario() {
        JPanel panelCentro = new JPanel(new GridLayout(5, 2, 8, 8));
        panelCentro.add(new JLabel("Continente"));
        panelCentro.add(comboBoxContinentes);
        panelCentro.add(new JLabel("Nº habitantes"));
        panelCentro.add(inputTextNumHabitantes);
        panelCentro.add(new JLabel("Nombre país"));
        panelCentro.add(inputTextNombrePais);
        panelCentro.add(new JLabel("Eliminar país"));
        panelCentro.add(comboBoxPaises);

        JButton btnGuardar = new JButton("Guardar país");
        btnGuardar.addActionListener(evt -> añadirPais());
        panelCentro.add(btnGuardar);

        JButton btnEliminar = new JButton("Quitar país");
        btnEliminar.addActionListener(evt -> eliminarPais());
        panelCentro.add(btnEliminar);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(evt -> dispose());

        add(panelCentro, BorderLayout.CENTER);
        add(btnCerrar, BorderLayout.SOUTH);
    }

    public void inicializarComboBoxContinente() {
        if (!managerDatabase.runMongoDatabase()) {
            return;
        }

        comboBoxContinentes.removeAllItems();
        managerDatabase.getListaDeContinentes().forEach(c -> comboBoxContinentes.addItem(c.getName()));
        managerDatabase.closeMongoDatabase();
    }

    public void inicializarComboBoxPaises() {
        if (!managerDatabase.runMongoDatabase()) {
            return;
        }

        comboBoxPaises.removeAllItems();
        managerDatabase.getListaPaises().forEach(p -> comboBoxPaises.addItem(p.getNombrePais()));
        managerDatabase.closeMongoDatabase();
    }

    private void añadirPais() {
        try {
            String nombrePais = inputTextNombrePais.getText().trim();
            int numeroHabitantes = Integer.parseInt(inputTextNumHabitantes.getText());
            String nombreContinente = (String) comboBoxContinentes.getSelectedItem();

            if (!managerDatabase.runMongoDatabase()) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar con la base de datos.");
                return;
            }

            Continente continente = managerDatabase.getListaDeContinentes()
                    .stream()
                    .filter(c -> c.getName().equalsIgnoreCase(nombreContinente))
                    .findFirst()
                    .orElse(null);

            if (continente == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el continente seleccionado.");
                managerDatabase.closeMongoDatabase();
                return;
            }

            if (nombrePais.contains(" ") || inputTextNumHabitantes.getText().contains(" ")) {
                JOptionPane.showMessageDialog(this, "No uses espacios al inicio o final en los campos.");
                managerDatabase.closeMongoDatabase();
                return;
            }

            boolean existe = managerDatabase.getListaPaises().stream()
                    .anyMatch(c -> c.getNombrePais().equalsIgnoreCase(nombrePais));

            if (existe) {
                JOptionPane.showMessageDialog(this, "Ese país ya existe en el registro.");
                managerDatabase.closeMongoDatabase();
                return;
            }

            String continenteId = managerDatabase.getIdFromContinente(nombreContinente);
            managerDatabase.añadirPais(new Pais(numeroHabitantes, nombrePais, continenteId));
            inicializarComboBoxPaises();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: El campo número de habitantes solo acepta números.");
        }
    }

    private void eliminarPais() {
        String nombrePais = (String) comboBoxPaises.getSelectedItem();

        if (!managerDatabase.runMongoDatabase()) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con la base de datos.");
            return;
        }

        Pais pais = managerDatabase.getListaPaises().stream()
                .filter(p -> p.getNombrePais().equalsIgnoreCase(nombrePais))
                .findFirst()
                .orElse(null);

        if (pais == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el país seleccionado.");
            managerDatabase.closeMongoDatabase();
            return;
        }

        managerDatabase.deletePais(pais);
        inicializarComboBoxPaises();
    }
}
