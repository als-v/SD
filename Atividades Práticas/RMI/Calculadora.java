/**
 * Define a interface para uma calculadora remota
 * Autor: Rodrigo Campiolo
 * Data: 22/11/2006
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Calculadora extends Remote {
    public response alunoFunction(int opt, int ra, String codigoDisciplina, int ano, int semestre, float nota) throws RemoteException;
    public response consultaNotasFaltas(int opt, String codigoDisciplina, int ano, int semestre) throws RemoteException;
}