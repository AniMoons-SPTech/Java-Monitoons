package componentsDoodle;

public class Utilitarios {

    public static Double formatDouble(Double valor, int casasDecimais){
        return Double.parseDouble(String.format("%."+casasDecimais+"f", valor).replace(",", "."));
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
}
