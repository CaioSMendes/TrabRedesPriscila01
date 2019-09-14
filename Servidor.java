package trabalho_redes;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {

    public static void main(String[] args) throws IOException {
        /* cria um socket "servidor" associado a porta 8000
         j� aguardando conex�es
         */
        ServerSocket servidor = new ServerSocket(8080);
        ExecutorService pool = Executors.newFixedThreadPool(20);
        System.out.println("Servidor iniciado...");
        while (true) {
            //cria uma nova thread para cada nova solicitacao de conexao
            pool.execute(new ThreadConexao(servidor.accept()));
            System.out.println("entrou");
        }
    }
}