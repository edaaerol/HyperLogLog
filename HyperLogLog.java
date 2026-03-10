import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HyperLogLog {

    private final int p;        
    private final int m;        
    private final int[] registers; 
    private final double alpha_m;  

    public HyperLogLog(int p) {
        if (p < 4 || p > 16) {
            throw new IllegalArgumentException("p degeri 4 ile 16 arasinda olmalidir.");
        }
        this.p = p;
        this.m = 1 << p; 
        this.registers = new int[this.m];

        this.alpha_m = switch (m) {
            case 16 -> 0.673;
            case 32 -> 0.697;
            case 64 -> 0.709;
            default -> 0.7213 / (1.0 + 1.079 / m);
        };
    }

    private int hash(String item) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(item.getBytes(StandardCharsets.UTF_8));

            int h = 0;
            for (int i = 0; i < 4; i++) {
                h <<= 8;
                h |= (hashBytes[i] & 0xFF);
            }
            return h;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algoritmasi bulunamadi!", e);
        }
    }

    public void add(String item) {
        int x = hash(item);
        int bucketIndex = x & (m - 1);
        int w = x >>> p;
        int rho = Integer.numberOfLeadingZeros(w) - p + 1;

        if (w == 0) {
            rho = 32 - p + 1;
        }

        registers[bucketIndex] = Math.max(registers[bucketIndex], rho);
    }

    public long count() {
        double Z = 0.0;
        for (int val : registers) {
            Z += Math.pow(2.0, -val);
        }
        double E = alpha_m * m * m / Z;

        if (E <= 2.5 * m) {
            int V = 0; 
            for (int val : registers) {
                if (val == 0) V++;
            }
            if (V > 0) {
                E = m * Math.log((double) m / V);
            }
        }
        else if (E > (1.0 / 30.0) * (1L << 32)) {
            E = -(1L << 32) * Math.log(1.0 - (E / (1L << 32)));
        }

        return (long) E;
    }

    public HyperLogLog merge(HyperLogLog other) {
        if (this.p != other.p) {
            throw new IllegalArgumentException("Birlestirme icin 'p' degerleri ayni olmalidir.");
        }

        HyperLogLog merged = new HyperLogLog(this.p);
        for (int i = 0; i < m; i++) {
            merged.registers[i] = Math.max(this.registers[i], other.registers[i]);
        }
        return merged;
    }

    public static void main(String[] args) {
        // Terminalde UTF-8 desteği için bazen bu ayar gerekir
        System.out.println("--- Java HyperLogLog (HLL) Algoritmasi Testi ---\n");

        int p = 14; 
        HyperLogLog hll = new HyperLogLog(p);
        int gercekFarkliEleman = 100000;

        System.out.println("1. " + gercekFarkliEleman + " adet farkli eleman ekleniyor...");
        for (int i = 0; i < gercekFarkliEleman; i++) {
            hll.add("user_" + i);
        }

        long tahmin = hll.count();
        double hataOrani = Math.abs((double)(gercekFarkliEleman - tahmin)) / gercekFarkliEleman * 100;

        System.out.println("Gercek Eleman Sayisi: " + gercekFarkliEleman);
        System.out.println("HLL Tahmini         : " + tahmin);
        System.out.printf("Gerceklesen Hata    : %%%.2f\n\n", hataOrani);

        System.out.println("2. Merge Testi Baslatiliyor...");
        HyperLogLog hll_1 = new HyperLogLog(12);
        HyperLogLog hll_2 = new HyperLogLog(12);

        for (int i = 1; i <= 5000; i++) hll_1.add(String.valueOf(i));       
        for (int i = 3001; i <= 8000; i++) hll_2.add(String.valueOf(i));    

        HyperLogLog merged = hll_1.merge(hll_2);
        System.out.println("Birlestirilmis Sonuc (Gercek 8000): " + merged.count());
    }
}