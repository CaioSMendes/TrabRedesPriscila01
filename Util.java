package trabalho_redes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Util {

    static String formatarDataGMT(Date date) {
        //cria um formato para o GMT espeficicado pelo HTTP
        SimpleDateFormat formatador = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);
        formatador.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date data = new Date();
        //Formata a dara para o padrao
        return formatador.format(data) + " GMT";
    }

}