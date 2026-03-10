# 📊 Büyük Veri Analitiğinde Olasılıksal Veri Yapıları: HyperLogLog (HLL) Tasarımı

Bu proje, **Cardinality Estimation** (Küme Büyüklüğü Tahmini) problemini çözmek amacıyla geliştirilen **HyperLogLog (HLL)** algoritmasının Java dili ile sıfırdan gerçeklenmesini içermektedir.

---

## 📖 1. HLL Nedir ve Ne İşe Yarar?
HyperLogLog (HLL), çok büyük veri setlerinde (Big Data) benzersiz eleman sayısını (distinct elements) tahmin etmek için kullanılan **olasılıksal** bir veri yapısıdır. 

* **Sorun:** Milyarlarca veriyi bir `HashSet` veya `Set` içinde tutmak GB'larca bellek gerektirir ve sistemi yavaşlatır.
* **Çözüm:** HLL, veriyi doğrudan saklamak yerine hash değerlerindeki "ardışık sıfır sayılarını" takip ederek, sadece birkaç KB bellek ile %99'un üzerinde doğrulukla tahmin yapar.



---

## 🛠️ 2. Algoritma Bileşenleri ve Tasarım
Proje, yönergelerde belirtilen tüm teknik gereksinimleri karşılamaktadır:

* **Hash Fonksiyonu:** Veri dağılımının uniform (homojen) olması ve performans için yüksek kaliteli **MurmurHash3** mantığı kullanılmıştır.
* **Bucketing (Kovalama):** Hash değerinin ilk $p$ biti kullanılarak veriler $2^{p}$ adet kovaya ayrılmıştır.
* **Register Yapısı:** Her kova (register), o gruptaki maksimum "ardışık sıfır sayısını" ($\rho$) saklar.
* **Harmonik Ortalama:** Uç değerlerin (outliers) tahmini bozmaması için Harmonik Ortalama formülü kullanılmıştır:

* **Düzeltme Faktörleri:** Küçük veri setleri için *Linear Counting*, çok büyük veri setleri için logaritmik düzeltmeler eklenmiştir.
* **Birleştirilebilirlik (Merge):** İki farklı HLL yapısı, register bazlı `Math.max()` işlemiyle veri kaybı olmadan bir araya getirilebilir.

---

## 📊 3. Algoritma Analizi ve Hata Sınırları
HLL algoritmasında hata payı kova sayısı ($m$) ile ters orantılıdır. Teorik standart hata formülü:

**Standard Error** $\approx \frac{1.04}{\sqrt{m}}$

Bu uygulamada **p = 14** seçilmiştir:
* **Kova Sayısı (m):** $2^{14} = 16.384$
* **Teorik Hata Payı:** $\approx \%0.81$
* **Bellek Tasarrufu:** Milyonlarca kayıt için yaklaşık **16 KB** (16.384 x 4 byte) yer kaplar. $m$ değeri arttıkça hata payı matematiksel olarak düşer ancak bellek kullanımı logaritmik olarak artar.

---

## 🤖 4. Geliştirme Süreci (Agentic Kodlama)
* **Dil Modeli:** Gemini 3 Flash
* **IDE:** IntelliJ IDEA / Visual Studio Code
* **Yöntem:** Agentic kodlama yaklaşımı uygulanmıştır. Algoritma parçalara bölünerek (hashing -> bucketing -> estimation -> merging) adım adım AI asistanı ile analiz edilmiş, teorik hata sınırları ve harmonik ortalama mantığı interaktif bir şekilde doğrulanmıştır.

---

## 🚀 5. Çalıştırma ve Test

### Gereksinimler
* Java JDK 17+ (Önerilen: JDK 25)

### Komutlar
```bash
# Uygulamayı derleyin ve çalıştırın
java HyperLogLog.java

```
###  🚀 Örnek Terminal Çıktısı

=== HyperLogLog (HLL) Algoritma Analizi ===

Test: 100000 adet unique veri ekleniyor...
Gercek Sayi      : 100000
HLL Tahmini      : 99272
Hata Payi        : %0.73
Kova Sayisi (m)  : 16384
Teorik Hata Siniri: %0.81

2. Merge Testi Baslatiliyor...
Birlestirilmis Sonuc (Gercek 8000): 7994
