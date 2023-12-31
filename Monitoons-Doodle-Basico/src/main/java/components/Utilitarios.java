package components;

import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.Volume;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utilitarios {

    public static Double formatDouble(Double valor, int casasDecimais) {
        return Double.parseDouble(String.format("%." + casasDecimais + "f", valor).replace(",", "."));
    }

    public static String formatBytes(long bytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    public static Double formatBytesToDouble(Long bytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return formatDouble(size, 2);
    }

    public static String getUnidadeBytes(long bytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return units[unitIndex];
    }

    public static String formatBytesPerSecond(long bytes) {
        String[] units = {"B/s", "KB/s", "MB/s", "GB/s", "TB/s"};
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    public static String getUnidadeBytesPerSecond(long bytes) {
        String[] units = {"B/s", "KB/s", "MB/s", "GB/s", "TB/s"};
        int unitIndex = 0;
        double size = bytes;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return units[unitIndex];
    }

    public static String formatFrequency(long frequency) {
        String[] units = {"Hz", "KHz", "MHz", "GHz"};
        int unitIndex = 0;
        double size = frequency;

        while (size >= 1000 && unitIndex < units.length - 1) {
            size /= 1000;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    public static String formatPercentage(Double percentage) {
        return String.format("%.2f %%", percentage);
    }

    public static Double formatPercentagetoDouble(Double percentage) {
        return formatDouble(percentage, 2);
    }

    public static Double calcPercent(Double valor, Double total) {
        return formatDouble((valor * 100) / total, 2);
    }

    public static Map<Disco, Volume> relacionarDiscosComVolumes() {
        SystemInfo system = new SystemInfo();
        OperatingSystem os = system.getOperatingSystem();
        HardwareAbstractionLayer hal = system.getHardware();

        List<Volume> volumes = os.getFileSystem().getFileStores().stream().map(Utilitarios::of).collect(Collectors.toList());
        List<Disco> discos = hal.getDiskStores().stream().map(Utilitarios::of).collect(Collectors.toList());

        Map<Disco, Volume> discoVolumeMap = new HashMap<>();

        Comparator<Volume> comparadorVolume = Comparator.comparing(Volume::getTotal);
        Comparator<Disco> comparadorDisco = Comparator.comparing(Disco::getTamanho);

        volumes.sort(comparadorVolume);
        discos.sort(comparadorDisco);

//        System.out.println(volumes);
//        System.out.println(discos);

        if (!discos.isEmpty()) {
            for (int i = 0; i < discos.size(); i++) {
                discoVolumeMap.put(discos.get(i), volumes.get(i));
            }
        } else {
            discoVolumeMap.put(discos.get(0), volumes.get(0));
        }
        return discoVolumeMap;
    }

    private static Volume of(OSFileStore volume) {
        return volume == null ? null : new Volume(volume);
    }

    private static Disco of(HWDiskStore disco) {
        return disco == null ? null : new Disco(disco);
    }
}
