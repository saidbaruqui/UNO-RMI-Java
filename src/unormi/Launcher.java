package unormi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Launcher {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            JuegoUNO juego = new unormi.servidor.ServidorUNO();
            Naming.rebind("uno", juego);
            System.out.println("Servidor UNO RMI listo.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
