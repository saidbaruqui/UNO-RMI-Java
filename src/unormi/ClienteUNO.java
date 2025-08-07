package unormi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.util.List;

public class ClienteUNO extends JFrame {
    private JuegoUNO juego;
    private String jugador;
    private JPanel panelMano, panelCentral, panelJugadores;
    private JLabel lblTurno;
    private JButton btnRobar;

    public ClienteUNO(String jugador) {
        this.jugador = jugador;

        setTitle("UNO - " + jugador);
        setSize(900, 500);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panelCentral = new JPanel(new FlowLayout());
        add(panelCentral, BorderLayout.CENTER);

        panelMano = new JPanel(new FlowLayout());
        add(panelMano, BorderLayout.SOUTH);

        panelJugadores = new JPanel();
        panelJugadores.setLayout(new BoxLayout(panelJugadores, BoxLayout.Y_AXIS));
        add(panelJugadores, BorderLayout.EAST);

        /*
        lblTurno.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTurno, BorderLayout.NORTH);*/

        conectar();

        Timer refrescar = new Timer(1000, e -> actualizarVista());
        refrescar.start();
    }

    private void conectar() {
        try {
            juego = (JuegoUNO) Naming.lookup("rmi://192.168.137.59/uno");
            juego.registrarJugador(jugador);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error de conexión al servidor UNO.");
        }
    }

    private void actualizarVista() {
        try {
            panelMano.removeAll();
            panelCentral.removeAll();
            panelJugadores.removeAll();

            String posibleGanador = juego.obtenerGanador();
            if (posibleGanador != null) {
                JOptionPane.showMessageDialog(this, (posibleGanador.equals(jugador) ? "🎉 ¡Ganaste!" : "💀 " + posibleGanador + " ganó la partida."), "FIN DEL JUEGO", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }

            List<String> mano = juego.obtenerMano(jugador);
            for (String carta : mano) {
                String ruta = "src/unormi/assets/cartas/" + carta + ".png";
                if (!new java.io.File(ruta).exists()) continue;

                ImageIcon icon = new ImageIcon(ruta);
                Image img = icon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
                JButton btn = new JButton(new ImageIcon(img));
                btn.setToolTipText(carta);
                btn.setBorder(null);
                btn.setContentAreaFilled(false);
                btn.addActionListener(e -> {
                    try {
                        if (juego.esTurnoDe(jugador)) {
                            if (carta.startsWith("negro")) {
                                String[] colores = {"rojo", "verde", "azul", "amarillo"};
                                String colorElegido = (String) JOptionPane.showInputDialog(this, "Elige un color:", "Cambio de color", JOptionPane.QUESTION_MESSAGE, null, colores, colores[0]);
                                if (colorElegido != null) {
                                    juego.jugarCarta(jugador, carta);
                                    juego.elegirColor(jugador, colorElegido);
                                }
                            } else {
                                juego.jugarCarta(jugador, carta);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "No es tu turno.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                panelMano.add(btn);
            }

            // Imagen del mazo (robar)
            String rutaRobar = "src/unormi/assets/cartas/robar.png";
            if (new java.io.File(rutaRobar).exists()) {
                ImageIcon iconRobar = new ImageIcon(rutaRobar);
                Image imgRobar = iconRobar.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
                btnRobar = new JButton(new ImageIcon(imgRobar));
                btnRobar.setBorder(null);
                btnRobar.setContentAreaFilled(false);
                btnRobar.setToolTipText("Robar carta");
                btnRobar.addActionListener(e -> {
                    try {
                        if (juego.esTurnoDe(jugador)) {
                            juego.robarCarta(jugador);
                        } else {
                            JOptionPane.showMessageDialog(this, "No es tu turno.");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                panelCentral.add(btnRobar);
            }

            // Mostrar carta central
            String cartaCentral = juego.obtenerCartaCentral();
            String rutaCentral = "src/unormi/assets/cartas/" + cartaCentral + ".png";
            if (new java.io.File(rutaCentral).exists()) {
                ImageIcon icon = new ImageIcon(rutaCentral);
                Image img = icon.getImage().getScaledInstance(100, 150, Image.SCALE_SMOOTH);
                JLabel lbl = new JLabel(new ImageIcon(img));
                panelCentral.add(lbl);
            } else {
                panelCentral.add(new JLabel("Carta central no encontrada: " + cartaCentral));
            }

            // Mostrar jugadores conectados y turno actual
           for (String nombre : juego.obtenerJugadores()) {
                JLabel lbl = new JLabel(nombre + (juego.esTurnoDe(nombre) ? " ← TURNO" : ""));
                lbl.setFont(new Font("Arial", Font.BOLD, 14));
                panelJugadores.add(lbl);
            }

            panelMano.revalidate();
            panelMano.repaint();
            panelCentral.revalidate();
            panelCentral.repaint();
            panelJugadores.revalidate();
            panelJugadores.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String nombre = JOptionPane.showInputDialog("Nombre del jugador:");
        SwingUtilities.invokeLater(() -> {
            ClienteUNO cliente = new ClienteUNO(nombre);
            cliente.setVisible(true);
        });
    }
}
