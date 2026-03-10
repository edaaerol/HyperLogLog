import java.nio.charset.StandardCharsets;

public class HyperLogLog {

    private final int p;           
    private final int m;           
    private final int[] registers; 
    private final double alpha_m;  

    public HyperLogLog(int p) {
        if (p < 4 || p > 16) {
            throw new IllegalArgumentException("Hata: p degeri 4 ile 16 arasinda olmalidir.");
        }
        this.p = p;
        this.m = 1 << p;
        this.registers = new int[m];

        switch (m) {
            case 16:
                this.alpha_m = 0.673;
                break;
            case 32:
                this.alpha_m = 0.697;
                break;
            case 64:
                this.alpha_m = 0.709;
                break;
            default:
                this.alpha_m = 0.7213 / (1.0 + 1.079 / m);
                break;
        }
    }

    private int murmurHash3(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        int hash = 0x811c9dc5; 
        for (byte b : bytes) {
            hash ^= (b & 0xff);
            hash *= 0x01000193; 
        }
        return hash;
    }

    public void add(String item) {
        int x = murmurHash3(item);
        int bucketIndex = x >>> (32 - p);
        int w = (x << p) | (1 << (p - 1)); 
        int rho = Integer.numberOfLeadingZeros(w) + 1;
        registers[bucketIndex] = Math.max(registers[bucketIndex], rho);
    }

    public long count() {
        double sum = 0.0;
        for (int val : registers) {
            sum += Math.pow(2.0, -val);
        }
        
        double estimate = alpha_m * m * m / sum;

        if (estimate <= 2.5 * m) {
            int zeroBuckets = 0;
            for (int val : registers) {
                if (val == 0) zeroBuckets++;
            }
            if (zeroBuckets > 0) {
                estimate = m * Math.log((double) m / zeroBuckets);
            }
        } 
        else if (estimate > (1.0 / 30.0) * (1L << 32)) {
            estimate = -(1L << 32) * Math.log(1.0 - (estimate / (1L << 32)));
        }

        return (long) estimate;
    }

    public HyperLogLog merge(HyperLogLog other) {
        if (this.p != other.p) {
            throw new IllegalArgumentException("Merge islemi icin p degerleri esit olmalidir!");
        }
        HyperLogLog merged = new HyperLogLog(this.p);
        for (int i = 0; i < m; i++) {
            merged.registers[i] = Math.max(this.registers[i], other.registers[i]);
        }
        return merged;
    }

    public static void main(String[] args) {
        // UTF-8 uyumlulugu icin çıktı metinlerini standardize ettik
        System.out.println("=== HyperLogLog (HLL) Algoritma Analizi ===\n");

        int precision = 14; 
        HyperLogLog hll = new HyperLogLog(precision);
        int realCount = 100_000;

        System.out.println("Test: " + realCount + " adet unique veri ekleniyor...");
        for (int i = 0; i < realCount; i++) {
            hll.add("user_id_session_" + i);
        }

        long prediction = hll.count();
        double error = Math.abs((double)(realCount - prediction)) / realCount * 100;

        // Karakter hatalarini onlemek icin ASCII uyumlu Turkce karakterler kullanildi
        System.out.println("Gercek Sayi      : " + realCount);
        System.out.println("HLL Tahmini      : " + prediction);
        System.out.printf("Hata Payi        : %%%.2f\n", error);
        System.out.println("Kova Sayisi (m)  : " + (1 << precision));
        System.out.printf("Teorik Hata Siniri: %%%.2f\n", (1.04 / Math.sqrt(1 << precision)) * 100);
    }
}
