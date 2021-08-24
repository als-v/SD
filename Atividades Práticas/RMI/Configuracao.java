/**
 * Define a interface para um serviço de gerenciamento de notas remoto
 * @author alisson
 * @author juan
 * data:20/08/2021
 * modificado em: 23/08/2021
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Configuracao extends Remote {
    public response alunoFunction(int opt, int ra, String codigoDisciplina, int ano, int semestre, float nota) throws RemoteException;
    public response consultaNotasFaltas(int opt, String codigoDisciplina, int ano, int semestre) throws RemoteException;
}