package trabalho_redes;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ThreadConexao implements Runnable {

    private final Socket socket;
    private boolean conectado;

    public ThreadConexao(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        conectado = true;
        //imprime na tela o IP do cliente
        System.out.println(socket.getInetAddress());
        while (conectado) {
            try {
                //cria uma requisicao a partir do InputStream do cliente
                RequisicaoHTTP requisicao = RequisicaoHTTP.lerRequisicao(socket.getInputStream());
                //se a conexao esta marcada para se mantar viva entao seta keepalive e o timeout
                if (requisicao.isManterViva()) {
                    socket.setKeepAlive(true);
                    socket.setSoTimeout(requisicao.getTempoLimite());
                } else {
                    //se nao seta um valor menor suficiente para uma requisicao
                    socket.setSoTimeout(300);
                }

                //se o caminho foi igual a / entao deve pegar o /index.html
                if (requisicao.getRecurso().equals("/")) {
                    requisicao.setRecurso("index.html");
                }
                //abre o arquivo pelo caminho
                File arquivo = new File(requisicao.getRecurso().replaceFirst("/", ""));

                RespostaHTTP resposta;

                //se o arquivo existir ent�o criamos a reposta de sucesso, com status 200
                if (arquivo.exists()) {
                    resposta = new RespostaHTTP(requisicao.getProtocolo(), 200, "OK");
                } else {
                    //se o arquivo n�o existe ent�o criamos a reposta de erro, com status 404
                    resposta = new RespostaHTTP(requisicao.getProtocolo(), 404, "Not Found");
                    arquivo = new File("404.html");
                }
                //l� todo o conte�do do arquivo para bytes e gera o conteudo de resposta
                resposta.setConteudoResposta(Files.readAllBytes(arquivo.toPath()));
                //converte o formato para o GMT espeficicado pelo protocolo HTTP
                String dataFormatada = Util.formatarDataGMT(new Date());
                //cabe�alho padr�o da resposta HTTP/1.1
                resposta.setCabecalho("Location", "http://localhost:8000/");
                resposta.setCabecalho("Date", dataFormatada);
                resposta.setCabecalho("Server", "MeuServidor/1.0");
                resposta.setCabecalho("Content-Type", "text/html");
                resposta.setCabecalho("Content-Length", resposta.getTamanhoResposta());
                //cria o canal de resposta utilizando o outputStream
                resposta.setSaida(socket.getOutputStream());
                resposta.enviar();
            } catch (IOException ex) {
                //quando o tempo limite terminar encerra a thread
                if (ex instanceof SocketTimeoutException) {
                    try {
                        conectado = false;
                        socket.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(ThreadConexao.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }

        }
    }

}