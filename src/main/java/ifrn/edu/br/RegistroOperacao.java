package ifrn.edu.br;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
    private static final ObjectMapper JSON = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

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
        OperacaoCarroDTO operacao;
        try {
            operacao = JSON.readValue(mensagem, OperacaoCarroDTO.class);
        } catch (JsonProcessingException e) {
            return LocalDateTime.now().format(FORMATO) + " | MENSAGEM | " + mensagem;
        }

        LocalDateTime quando = operacao.dataHora() == null ? LocalDateTime.now() : operacao.dataHora();
        String usuario = operacao.usuarioNome() + " (#" + operacao.usuarioId() + ")";
        String carro = "carro #" + operacao.carroId() + " " + operacao.marca() + " " + operacao.modelo() + " "
                + operacao.ano();

        return quando.format(FORMATO) + " | " + operacao.operacao() + " | " + usuario + " | " + carro + " | req="
                + operacao.requestId();
    }
}
