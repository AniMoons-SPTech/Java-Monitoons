import com.github.britooo.looca.api.group.discos.DiscoGrupo;
import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.sistema.Sistema;
import com.github.britooo.looca.api.group.temperatura.Temperatura;
import org.springframework.jdbc.core.JdbcTemplate;
import oshi.hardware.GraphicsCard;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class test {
    public static void main(String[] args) {
        Scanner inp = new Scanner(System.in);

        Conexao conexao = new Conexao();
        JdbcTemplate con = conexao.getConexaoDoBanco();

        Looca looca = new Looca();
        Sistema sistema = new Sistema();

        DiscoGrupo grupoDeDiscos = looca.getGrupoDeDiscos();
        List<Disco> discos = grupoDeDiscos.getDiscos();
        Disco disco = (Disco) looca.getGrupoDeDiscos().getDiscos().get(0);
        Memoria memoria = new Memoria();
        Temperatura temp = new Temperatura();
        Processador cpu =new Processador();
        Gpu gpu = new Gpu();
        List<GraphicsCard> listaGPU = gpu.getListaGPU();

        System.out.println("Bem vindo ao Monitoons!!");
        System.out.print("Insira seu email: ");
        String email = inp.nextLine();
        System.out.print("Insira sua senha: ");
        String senha = inp.nextLine();

        while(true) {

            String senhaCerta = con.queryForObject("SELECT senha FROM usuario WHERE email = ?",String.class,email);

            if (senhaCerta.equals(senha)){
                //Gravar informações localmente
                Integer idUsuario = con.queryForObject("SELECT idUsuario FROM usuario WHERE email = ?",Integer.class,email);
                List<String> Componentes = new ArrayList<>();
//
//                //Primeiro cadastro
//                List<Integer> maquinas = con.query("SELECT idMaquina FROM maquina",new BeanPropertyRowMapper<>(test.class));
//                Boolean jaExiste = false;
//                for (int i = 0; i < maquinas.size(); i++) {
//                    if (idUsuario.equals(maquinas.get(i))){
//                        jaExiste = true;
//                    }
//                    else{
//                        continue;
//                    }
//                }
//                if (!jaExiste){
//
//                }
                // memoria
                try {
                    while (true){
                        //gpu
                        Process process = Runtime.getRuntime().exec("nvidia-smi --query-gpu=utilization.gpu,memory.used,memory.free --format=csv,noheader,nounits");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                        String gpuNome = "";

                        String line;
                        Long gpuTotal = null;
                        Double gpuMemUso = null;
                        Double gpuMemDisp = null;
                        Double videoPorcetUso = null;
                        while ((line = reader.readLine()) != null) {
                            String[] memoryInfo = line.trim().split(",");
                            videoPorcetUso = Double.parseDouble(memoryInfo[0].trim());
                            gpuMemUso = Double.parseDouble(memoryInfo[1].trim());
                            gpuMemDisp = Double.parseDouble(memoryInfo[2].trim());
                            gpuTotal = (long) (gpuMemDisp + gpuMemUso);
//                            System.out.println("Uso da Gpu: " + videoPorcetUso + "%");
//                            System.out.println("Memória de Vídeo em Uso: " + gpuMemUso + " Mib");
//                            System.out.println("Memória de Vídeo Disponível: " + gpuMemDisp + " Mib");
                        }


                        //memoria

                        Double memUso = (double) memoria.getEmUso() / 1073741824;
                        Double memDisp = (double) memoria.getDisponivel() / 1073741824;
                        Long memTotal = memoria.getTotal() / 1073741824;
                        String memNome = "Memoria de " + memTotal + "GB";
    //                    System.out.println("--MEMORIA--\nMemoria Total: %dGB;\nMemoria Disponivel: %dGB\nMemoria em Uso: %dGB ".formatted(memTotal,memDisponivel,memUso));

                        //temperatura
                        Double temperatura = temp.getTemperatura();
//                        System.out.println("--TEMPERATURA--\nTemperatura atual: %.2f".formatted(temperatura));

                        //disco
                        String discoNome = disco.getNome();
                        Long discoTotal = grupoDeDiscos.getTamanhoTotal();
                        Double discoUso = 0.0;
                        Double discoDisp = Double.valueOf(grupoDeDiscos.getTamanhoTotal());
                        Double velGrav = Double.valueOf(disco.getBytesDeEscritas()) / 1024;
                        Double velLeit = Double.valueOf(disco.getBytesDeLeitura()) / 1024;

//                        System.out.println(discos.get(0));

                        // cpu
                        Integer cpuNucleos = cpu.getNumeroCpusLogicas();
                        Double cpuUso = cpu.getUso() * 10;
                        Double cpuFrequencia = Double.valueOf(cpu.getFrequencia()) / 1000000000;
                        String cpuNome = cpu.getNome();

    //                  System.out.println("--CPU--\nCpu Uso: %.2f%%\nFrequencia cpu: %.2fGHz".formatted(cpuUso,cpuFrequencia));

                        Componente componente01 = new Componente("cpu", cpuNucleos, null, cpuFrequencia, cpuNome);
                        Componente componente02 = new Componente("mem", null, memTotal, cpuFrequencia, memNome);
                        Componente componente03 = new Componente("disco", null, discoTotal, cpuFrequencia, discoNome);
                        Componente componente04 = new Componente("gpu", null, gpuTotal, cpuFrequencia, gpuNome);

    //                  System.out.println(componente01);
    //                  System.out.println(componente02);
    //                  System.out.println(componente03);
    //                  System.out.println(componente04);


                        Registro registro01 =new Registro(componente01,cpuUso,null,null,cpuFrequencia,null,null,temperatura);
                        Registro registro02 =new Registro(componente02,memUso,null,null,cpuFrequencia,memUso,memDisp,temperatura);
                        Registro registro03 =new Registro(componente03,discoUso,velGrav,velLeit,cpuFrequencia,discoUso,discoDisp,temperatura);
                        Registro registro04 =new Registro(componente04,videoPorcetUso,null,null,cpuFrequencia,gpuMemUso,gpuMemDisp,temperatura);

                        System.out.println(registro01);
                        System.out.println(registro02);
                        System.out.println(registro03);
                        System.out.println(registro04);
                        System.out.println("\n\n");
                        try {
                            Thread.sleep(5000); // Espera 1 segundo
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }




                } catch (IOException var15) {
                    var15.printStackTrace();
                }


                break;

            }else {
                System.out.println("Email ou senha incorreta");
            }
        }
    }
}
