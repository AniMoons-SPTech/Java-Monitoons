import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    LocalDateTime agora;
    DateTimeFormatter formatado;
    String dataFormatada;
    String systemDrive;
    String caminhoDiretorioLogs;
    String caminhoArquivo ;

//    public Log(Boolean gui) {
//        this.agora = LocalDateTime.now();
//        this.formatado = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss");
//        this.dataFormatada = agora.format(formatado);
//        if(!gui){
//            this.systemDrive = System.getenv("SYSTEMDRIVE");
//            this.caminhoDiretorioLogs = systemDrive + "\\logsMonitoons";
//            this.caminhoArquivo = caminhoDiretorioLogs + "\\%s.json".formatted(dataFormatada);
//        }else {
//            this.systemDrive = System.getenv("HOME");
//            this.caminhoDiretorioLogs = systemDrive + "/logsMonitoons";
//            this.caminhoArquivo = caminhoDiretorioLogs + "/%s.log".formatted(dataFormatada);
//        }
//
//    }
    public Log(String SO) {
        this.agora = LocalDateTime.now();
        this.formatado = DateTimeFormatter.ofPattern("dd-MM-yyyy HH-mm-ss");
        this.dataFormatada = agora.format(formatado);
        if(SO.equals("windows")){
            this.systemDrive = System.getenv("SYSTEMDRIVE");
            this.caminhoDiretorioLogs = systemDrive + "\\logsMonitoons";
            this.caminhoArquivo = caminhoDiretorioLogs + "\\%s.json".formatted(dataFormatada);
        }else {
            this.systemDrive = System.getenv("HOME");
            this.caminhoDiretorioLogs = systemDrive + "/logsMonitoons";
            this.caminhoArquivo = caminhoDiretorioLogs + "/%s.log".formatted(dataFormatada);
        }

    }
    void criarPasta() {
        Path diretorioPath = Paths.get(this.caminhoDiretorioLogs);
        // Verifica se o diretório já existe
        if (!Files.exists(diretorioPath)) {
            try {
                // Cria o diretório
                Files.createDirectories(diretorioPath);
                System.out.println("Diretório criado com sucesso: " + this.caminhoDiretorioLogs);
            } catch (IOException e) {
                System.out.println("Falha ao criar o diretório: " + e.getMessage());
            }
        } else {
            System.out.println("O diretório já existe: " + this.caminhoDiretorioLogs);
        }
    }
    void escreverLog(Double cpu,Double gpu,Long ram){
        criarPasta();
        // Escrever o log em um arquivo JSON
        try (FileWriter writer = new FileWriter(this.caminhoArquivo, true)) {

            writer.write(String.format("""
                    Time: %s;
                    CPU: %.2f%%;
                    GPU: %.2f%%;
                    RAM: %d%%.
                    """, dataFormatada,cpu,gpu,ram));
            System.out.println("Log adicionado ao arquivo com sucesso.");
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo de log: " + e.getMessage());
        }
    }

    public LocalDateTime getAgora() {
        return agora;
    }

    public void setAgora(LocalDateTime agora) {
        this.agora = agora;
    }

    public DateTimeFormatter getFormatado() {
        return formatado;
    }

    public void setFormatado(DateTimeFormatter formatado) {
        this.formatado = formatado;
    }

    public String getDataFormatada() {
        return dataFormatada;
    }

    public void setDataFormatada(String dataFormatada) {
        this.dataFormatada = dataFormatada;
    }

    public String getSystemDrive() {
        return systemDrive;
    }

    public void setSystemDrive(String systemDrive) {
        this.systemDrive = systemDrive;
    }

    public String getCaminhoDiretorioLogs() {
        return caminhoDiretorioLogs;
    }

    public void setCaminhoDiretorioLogs(String caminhoDiretorioLogs) {
        this.caminhoDiretorioLogs = caminhoDiretorioLogs;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    @Override
    public String toString() {
        return "Log{" +
                "agora=" + agora +
                ", formatado=" + formatado +
                ", dataFormatada='" + dataFormatada + '\'' +
                ", systemDrive='" + systemDrive + '\'' +
                ", caminhoDiretorioLogs='" + caminhoDiretorioLogs + '\'' +
                ", caminhoArquivo='" + caminhoArquivo + '\'' +
                '}';
    }
}
