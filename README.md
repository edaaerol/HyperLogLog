# Büyük Veri Analitiğinde Olasılıksal Veri Yapıları: HyperLogLog (HLL) Tasarımı

Bu proje, **Cardinality Estimation** (Küme Büyüklüğü Tahmini) problemini çözmek amacıyla geliştirilen **HyperLogLog (HLL)** algoritmasının Java dili ile sıfırdan gerçeklenmesini içermektedir.

---

## 📖 1. HLL Nedir ve Ne İşe Yarar?
HyperLogLog (HLL), çok büyük veri setlerinde (Big Data) benzersiz eleman sayısını (distinct elements) tahmin etmek için kullanılan **olasılıksal** bir veri yapısıdır. 

* **Sorun:** Milyarlarca veriyi bir `Set` içinde tutmak GB'larca bellek gerektirir.
* **Çözüm:** HLL, veriyi doğrudan saklamak yerine hash değerlerindeki "ardışık sıfır sayılarını" takip ederek, sadece birkaç KB bellek ile %99'un üzerinde doğrulukla tahmin yapar.

---

## 🛠️ 2. Algoritma Bileşenleri ve Tasarım
Proje, yönergelerde belirtilen tüm teknik gereksinimleri karşılamaktadır:

* **Hash Fonksiyonu:** Veri dağılımının uniform olması ve çakışmaların önlenmesi için yüksek kaliteli `SHA-256` kullanılmıştır.
* **Bucketing (Kovalama):** Hash değerinin ilk $p$ biti kullanılarak veriler alt kümelere ayrılmıştır.
* **Register Yapısı:** Her kova (register), o gruptaki maksimum "ardışık sıfır sayısını" ($\rho$) saklar.
* **Harmonik Ortalama:** Uç değerlerin tahmini bozmaması için Harmonik Ortalama formülü kullanılmıştır: 
    $$E = \alpha_m \cdot m^2 \cdot \left( \sum_{j=1}^{m} 2^{-M_j} \right)^{-1}$$
* **Düzeltme Faktörleri:** Küçük veri setleri için *Linear Counting*, çok büyük veri setleri için logaritmik düzeltmeler eklenmiştir.
* **Birleştirilebilirlik (Merge):** İki farklı HLL yapısı, register bazlı `Math.max()` işlemiyle veri kaybı olmadan bir araya getirilebilir.



---

## 📊 3. Algoritma Analizi ve Hata Sınırları
HLL algoritmasında hata payı kova sayısı ($m$) ile ters orantılıdır. Teorik standart hata formülü:

$$Standard Error \approx \frac{1.04}{\sqrt{m}}$$

Bu uygulamada $p = 14$ seçilmiştir:
* **Kova Sayısı ($m$):** $2^{14} = 16.384$
* **Teorik Hata Payı:** $\approx \%0.81$
* **Bellek Tasarrufu:** Milyonlarca kayıt için yaklaşık **16 KB** yer kaplar. $m$ değeri (ve dolayısıyla $p$) artırıldığında hata payı matematiksel olarak düşer ancak bellek kullanımı logaritmik olarak artar.



---

## 🤖 4. Geliştirme Süreci (Agentic Kodlama)
* **Dil Modeli:** Gemini 3 Flash.
* **IDE:** Visual Studio Code / IntelliJ IDEA.
* **Yöntem:** Agentic kodlama yaklaşımı uygulanmıştır. Algoritma parçalara bölünerek (hashing -> bucketing -> estimation -> merging) adım adım Gemini'ye doğrulatılmış ve mantıksal hatalar terminal çıktıları üzerinden interaktif olarak giderilmiştir.

---

## 🚀 5. Çalıştırma ve Test

### Gereksinimler
* Java JDK 17+ (Önerilen: JDK 25)

### Komutlar
```bash
# Uygulamayı çalıştırın
java HyperLogLog.java
```

## 🚀 Örnek Terminal Çıktısı

--- Java HyperLogLog (HLL) Algoritmasi Testi ---
1. 100000 adet farkli eleman ekleniyor...
Gercek Eleman Sayisi: 100000 | HLL Tahmini: 99603 | Hata: %0.40
2. Merge Testi Baslatiliyor...
Birlestirilmis Sonuc (Gercek 8000): 7992

