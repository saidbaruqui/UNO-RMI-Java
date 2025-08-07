package unormi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface JuegoUNO extends Remote {
    void registrarJugador(String nombre) throws RemoteException;
    void jugarCarta(String jugador, String carta) throws RemoteException;
    void robarCarta(String jugador) throws RemoteException;
    List<String> obtenerMano(String jugador) throws RemoteException;
    String obtenerCartaCentral() throws RemoteException;
    boolean esTurnoDe(String jugador) throws RemoteException;
    void elegirColor(String jugador, String color) throws RemoteException;
    String obtenerGanador() throws RemoteException;
    List<String> obtenerJugadores() throws RemoteException;

}
