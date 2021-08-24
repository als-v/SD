/**
 * Inicializa o servidor
 * @author alisson
 * @author juan
 * data:20/08/2021
 * modificado em: 23/08/2021
 */
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Servidor {
    public static void main(String args[]) {
       try {
            /* inicializa um objeto remoto */
            Configuracao calc = new Config();

            /* registra o objeto remoto no Binder */
            Registry registry = LocateRegistry.getRegistry("localhost");
            registry.rebind("ServicoCalculadora", calc);

            /* aguardando invocacoes remotas */
	        System.out.println("Servidor pronto ...");
	    } catch (Exception e) {
	        System.out.println(e);
        } //catch
    } //main
} //Servidor