package Vistas;

import Controlador.DatabaseManager;
import Entidad.Pais;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Montero
 */
public class Consultas extends JDialog {

    private final DatabaseManager managerDatabase;
    private final JComboBox<String> comboBoxContinente;
    private final JTextField inputNumeroDeHabitantes;
    private final JTextArea textAreaPaises;

    public Consultas(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.managerDatabase = new DatabaseManager();
        this.comboBoxContinente = new JComboBox<>();
        this.inputNumeroDeHabitantes = new JTextField("0");
        this.textAreaPaises = new JTextArea(12, 40);

        configurarVentana();
        construirFormulario();
        inicializarComboBoxContinente();
    }

    private void configurarVentana() {
        setTitle("Resumen por continente");
        getContentPane().setBackground(new java.awt.Color(247, 246, 255));
        setLayout(new BorderLayout(8, 8));
        setSize(720, 420);
        setLocationRelativeTo(getParent());
    }

    private void construirFormulario() {
        JPanel panelSuperior = new JPanel(new GridLayout(2, 2, 8, 8));
        panelSuperior.add(new JLabel("Continente"));
        panelSuperior.add(comboBoxContinente);
        panelSuperior.add(new JLabel("Habitantes totales"));
        panelSuperior.add(inputNumeroDeHabitantes);

        comboBoxContinente.addActionListener(evt -> actualizarConsulta());
        inputNumeroDeHabitantes.setEditable(false);
        textAreaPaises.setEditable(false);

        add(panelSuperior, BorderLayout.NORTH);
        add(new JScrollPane(textAreaPaises), BorderLayout.CENTER);
    }

    public void inicializarComboBoxContinente() {
        if (!managerDatabase.runMongoDatabase()) {
            return;
        }

        comboBoxContinente.removeAllItems();
        managerDatabase.getListaDeContinentes().forEach(c -> comboBoxContinente.addItem(c.getName()));
        managerDatabase.closeMongoDatabase();

        if (comboBoxContinente.getItemCount() > 0) {
            comboBoxContinente.setSelectedIndex(0);
            actualizarConsulta();
        }
    }

    private void actualizarConsulta() {
        if (comboBoxContinente.getSelectedItem() == null) {
            return;
        }

        String nombreContinente = (String) comboBoxContinente.getSelectedItem();
        textAreaPaises.setText("");
        inputNumeroDeHabitantes.setText("0");

        if (!managerDatabase.runMongoDatabase()) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar con la base de datos.");
            return;
        }

        int sumaTotalHabitantes = 0;
        String idBuscado = managerDatabase.getIdContinentePuro(nombreContinente);

        if (idBuscado != null) {
            List<Pais> listaFiltrada = managerDatabase.getListaPaises().stream()
                    .filter(p -> p.getContinenteId() != null && p.getContinenteId().trim().equals(idBuscado.trim()))
                    .sorted((p1, p2) -> p1.getNombrePais().compareToIgnoreCase(p2.getNombrePais()))
                    .toList();

            if (!listaFiltrada.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Pais p : listaFiltrada) {
                    sb.append(p.getNombrePais()).append(" | ").append(p.getNumHabitantes()).append("\n");
                    sumaTotalHabitantes += p.getNumHabitantes();
                }
                textAreaPaises.setText(sb.toString());
                inputNumeroDeHabitantes.setText(String.valueOf(sumaTotalHabitantes));
            } else {
                JOptionPane.showMessageDialog(this, "No hay países asociados a ese continente.");
            }
        }

        managerDatabase.closeMongoDatabase();
    }
}
