package ifrn.edu.br;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RegistroOperacao {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Path arquivo;

    public RegistroOperacao(Path arquivo) {
        this.arquivo = arquivo;
    }

    public synchronized String salvar(String mensagem) {
        String linha = montarLinha(mensagem);
        try {
            Files.writeString(arquivo, linha + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Nao consegui salvar no arquivo " + arquivo, e);
        }
        return linha;
    }

    String montarLinha(String mensagem) {
        String[] partes = mensagem.split(";");

        if (partes.length < 8) {
            return LocalDateTime.now().format(FORMATO) + " | MENSAGEM | " + mensagem;
        }

        String operacao = partes[0];
        String quando = LocalDateTime.parse(partes[1]).format(FORMATO);
        String usuario = partes[2];
        String carro = "carro #" + partes[3] + " " + partes[4] + " " + partes[5] + " " + partes[6];
        String requestId = partes[7];

        return quando + " | " + operacao + " | " + usuario + " | " + carro + " | req=" + requestId;
    }
}
