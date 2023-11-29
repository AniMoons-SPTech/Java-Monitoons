package Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GerarLog {
    private final LocalDateTime dataAtual = LocalDateTime.now();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final String fileName = String.format("Log - %s.txt", dataAtual.format(formatter));
    private final File file = new File(fileName);

    public void gravar(String info) {
        if (!file.exists()) {
            criarNovoArquivo();
        }
        escreverArquivo(info);
    }

    private void criarNovoArquivo() {
        try {
            if (file.createNewFile()) {
                Log.logInfo("Novo arquivo criado: " + file.getName());
            } else {
                Log.logWarning("Arquivo já existe.");
            }
        } catch (IOException e) {
            Log.logError("Erro ao criar arquivo: " + e.getMessage());
        }
    }

    private void escreverArquivo(String texto) {
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.write(texto + "\n");
            Log.logInfo("Informações gravadas com sucesso.");
        } catch (IOException e) {
            Log.logError("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }
}
