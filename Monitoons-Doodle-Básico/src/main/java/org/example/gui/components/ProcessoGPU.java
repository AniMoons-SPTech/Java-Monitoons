package org.example.gui.components;


import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStreamReader;

public class ProcessoGPU {
    public static void main(String[] args) {
        int processId = 5320; // Substitua pelo PID do processo desejado
        String nvidiaSMICommand = "nvidia-smi ";

        try {
            Process process = Runtime.getRuntime().exec(nvidiaSMICommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    int pid = Integer.parseInt(parts[0].trim());
                    if (pid == processId) {
                        double usedGpuMemory = Double.parseDouble(parts[1].trim());
                        double gpuUtilization = Double.parseDouble(parts[2].trim());
                        System.out.println("Porcentagem de uso da GPU: " + gpuUtilization + "%");
                        System.out.println("Memória da GPU usada: " + usedGpuMemory + " MB");
                        break;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


