package unormi.servidor;

import unormi.JuegoUNO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServidorUNO extends UnicastRemoteObject implements JuegoUNO {
    private final Map<String, List<String>> manos = new HashMap<>();
    private final List<String> ordenJugadores = new ArrayList<>();
    private int turno = 0;
    private boolean sentidoHorario = true;
    private String cartaCentral = "rojo_5";
    private final Stack<String> mazo = new Stack<>();
    private final Stack<String> descarte = new Stack<>();
    private boolean esperandoColor = false;
    private String ganador = null;

    public ServidorUNO() throws RemoteException {
        generarMazo();
    }

    private void generarMazo() {
        String[] colores = {"rojo", "verde", "azul", "amarillo"};
        for (String color : colores) {
            for (int i = 0; i <= 9; i++) {
                mazo.push(color + "_" + i);
            }
            mazo.push(color + "_reversa");
            mazo.push(color + "_saltar");
            mazo.push(color + "_masdos");
        }
        mazo.push("negro_cambio");
        mazo.push("negro_mas4");
        mazo.push("negro_cambio");
        mazo.push("negro_mas4");
        Collections.shuffle(mazo);
    }

    @Override
    public synchronized void registrarJugador(String nombre) throws RemoteException {
        if (manos.containsKey(nombre)) return;
        List<String> mano = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            if (mazo.isEmpty()) reciclarDescarte();
            if (!mazo.isEmpty()) mano.add(mazo.pop());
        }
        manos.put(nombre, mano);
        ordenJugadores.add(nombre);
    }

    @Override
    public synchronized void jugarCarta(String jugador, String carta) throws RemoteException {
        if (ganador != null) return;
        if (!esTurnoDe(jugador)) return;
        List<String> mano = manos.get(jugador);
        if (!mano.contains(carta)) return;

        String[] partesCarta = carta.split("_");
        String[] partesCentral = cartaCentral.split("_");

        boolean esNegra = carta.startsWith("negro");
        boolean jugable = false;

        if (esperandoColor && carta.startsWith(partesCentral[0])) {
            jugable = true;
        } else if (!esNegra) {
            if (partesCarta[0].equals(partesCentral[0]) || partesCarta[1].equals(partesCentral[1])) {
                jugable = true;
            }
        } else {
            jugable = true;
        }

        if (!jugable) return;

        // Aplicar carta
        mano.remove(carta);
        descarte.push(carta);
        esperandoColor = false;

        // Verificar si el jugador ganó
        if (mano.isEmpty()) {
            ganador = jugador;
            return;
        }

        // Efectos especiales
        switch (partesCarta[1]) {
            case "mas4":
                cartaCentral = carta;
                avanzarTurno();
                obtenerJugadorEnTurno().ifPresent(j -> robarVarias(j, 4));
                esperandoColor = true;
                return;
            case "cambio":
                cartaCentral = carta;
                esperandoColor = true;
                return;
            case "masdos":
                cartaCentral = carta;
                avanzarTurno();
                obtenerJugadorEnTurno().ifPresent(j -> robarVarias(j, 2));
                break;
            case "saltar":
                cartaCentral = carta;
                avanzarTurno();
                break;
            case "reversa":
                cartaCentral = carta;
                sentidoHorario = !sentidoHorario;
                break;
            default:
                cartaCentral = carta;
        }

        avanzarTurno();
    }

    private void avanzarTurno() {
        if (ordenJugadores.isEmpty()) return;
        if (sentidoHorario) {
            turno = (turno + 1) % ordenJugadores.size();
        } else {
            turno = (turno - 1 + ordenJugadores.size()) % ordenJugadores.size();
        }
    }

    private void robarVarias(String jugador, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            if (mazo.isEmpty()) reciclarDescarte();
            if (!mazo.isEmpty()) manos.get(jugador).add(mazo.pop());
        }
    }

    private void reciclarDescarte() {
        if (descarte.isEmpty()) return;
        String tope = descarte.pop();
        mazo.addAll(descarte);
        descarte.clear();
        descarte.push(tope);
        Collections.shuffle(mazo);
    }

    @Override
    public synchronized void robarCarta(String jugador) throws RemoteException {
        if (ganador != null) return;
        if (!esTurnoDe(jugador)) return;
        if (mazo.isEmpty()) reciclarDescarte();
        if (!mazo.isEmpty()) manos.get(jugador).add(mazo.pop());
        avanzarTurno();
    }

    @Override
    public synchronized List<String> obtenerMano(String jugador) throws RemoteException {
        return new ArrayList<>(manos.getOrDefault(jugador, new ArrayList<>()));
    }

    @Override
    public synchronized String obtenerCartaCentral() throws RemoteException {
        return cartaCentral;
    }

    @Override
    public synchronized boolean esTurnoDe(String jugador) throws RemoteException {
        if (ganador != null) return false;
        return !ordenJugadores.isEmpty() && ordenJugadores.get(turno).equals(jugador);
    }

    @Override
    public synchronized void elegirColor(String jugador, String color) throws RemoteException {
        if (ganador != null) return;
        if (esperandoColor) {
            // Visualmente usar una carta del color (para evitar errores de imagen)
            cartaCentral = color + "_1";
            esperandoColor = false;
            avanzarTurno();
        }
    }
    
        
    public synchronized List<String> obtenerJugadores() throws RemoteException {
        return new ArrayList<>(ordenJugadores);
    }

    @Override
    public synchronized String obtenerGanador() throws RemoteException {
        return ganador;
    }

    private Optional<String> obtenerJugadorEnTurno() {
        if (ordenJugadores.isEmpty()) return Optional.empty();
        return Optional.of(ordenJugadores.get(turno));
    }
    
}
