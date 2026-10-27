package ifrn.edu.br;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistroOperacaoTest {

    @TempDir
    Path pasta;

    @Test
    void mensagemDaLojaCarroViraUmaLinhaLegivel() {
        RegistroOperacao registro = new RegistroOperacao(pasta.resolve("operacoes.log"));

        String linha = registro.montarLinha(
                "CARRO_CRIADO;2026-10-27T14:05:30;Maria Oliveira (#2);1;Toyota;Corolla;2024;web-abc123");

        assertEquals("27/10/2026 14:05:30 | CARRO_CRIADO | Maria Oliveira (#2) | carro #1 Toyota Corolla 2024 | req=web-abc123",
                linha);
    }

    @Test
    void mensagemSimplesTambemEhSalva() {
        RegistroOperacao registro = new RegistroOperacao(pasta.resolve("operacoes.log"));

        String linha = registro.montarLinha("augusto");

        assertTrue(linha.endsWith(" | MENSAGEM | augusto"));
    }

    @Test
    void cadaMensagemViraUmaLinhaNoArquivo() throws IOException {
        Path arquivo = pasta.resolve("operacoes.log");
        RegistroOperacao registro = new RegistroOperacao(arquivo);

        registro.salvar("CARRO_CRIADO;2026-10-27T14:05:30;Maria Oliveira (#2);1;Toyota;Corolla;2024;req1");
        registro.salvar("CARRO_EXCLUIDO;2026-10-27T14:06:00;Maria Oliveira (#2);1;Toyota;Corolla;2024;req2");

        List<String> linhas = Files.readAllLines(arquivo);
        assertEquals(2, linhas.size());
        assertTrue(linhas.get(0).contains("CARRO_CRIADO"));
        assertTrue(linhas.get(1).contains("CARRO_EXCLUIDO"));
    }

    @Test
    void pastaQueNaoExisteDaErro() {
        RegistroOperacao registro = new RegistroOperacao(pasta.resolve("nao/existe/operacoes.log"));

        assertThrows(RuntimeException.class, () -> registro.salvar("augusto"));
    }
}
