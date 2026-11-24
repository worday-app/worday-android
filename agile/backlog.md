# Worday — Yapılacak İşler

**Öncelikler:**  
- **P0:** Yayın veya temel veri güvenliği için gerekli  
- **P1:** Ürün kalitesi ve önemli eksikler  
- **P2:** Sonraki iyileştirmeler  

Bu liste kod incelemesinden çıkarıldı. Bir iş, kodu ve ilgili testleri tamamlanmadan bitmiş sayılmaz.

## A. Öğrenme ve SM-2

### US-01 — SM-2 sınır durumlarını test et [P0]
Yanlış cevap, uzun süre çalışmama ve geçersiz değerlerde tekrar planının bozulmamasını sağla.

**Tamamlanma koşulları**
- Kalite yalnızca 0–5 aralığında kabul edilir.
- Yanlış cevapta tekrar sayısı sıfırlanır.
- Ease factor 1.3 altına düşmez.
- İlk iki başarılı tekrar aralığı 1 ve 6 gündür.
- Uzun süredir gecikmiş kelime tekrar listesinde görünür.
- Tarih, aralık ve sınır durumları birim testleriyle doğrulanır.

### US-02 — Öğrenme kartı cevaplarını kaydet [P0]
Kullanıcının “Biliyorum/Bilmiyorum” seçimi ilerlemeye yansısın.

**Tamamlanma koşulları**
- Her cevap yalnızca bir kez kaydedilir.
- İlgili öğrenme durumu güncellenir.
- Uygulama kapatılıp açıldığında sonuç korunur.
- Yanlış ve doğru cevap akışları test edilir.

### US-03 — Quiz sonucunu tutarlı kaydet [P0]
Quiz, SM-2 ve günlük oturum verileri birbiriyle uyumlu olsun.

**Tamamlanma koşulları**
- Boş soru listesi hata üretmez.
- Aynı cevap iki kez işlenmez.
- Quiz sonucu ve günlük oturum tutarlı kaydedilir.
- Seri aynı gün içinde yanlışlıkla birden fazla artmaz.

### US-04 — Seviye testini tamamla [P1]
Kullanıcının cevaplarına göre CEFR seviyesi doğru belirlensin.

**Tamamlanma koşulları**
- Soruların seçenekleri ve doğru cevapları veri modelinde bulunur.
- Seviye hesaplama kuralları ürün tarafından onaylanır.
- Boş test ve eksik cevap durumları yönetilir.

## B. Yerel veri ve çevrimdışı kullanım

### US-05 — Room migration'ı güvenli hale getir [P0]
Uygulama güncellendiğinde kullanıcının ilerlemesi kaybolmasın.

**Tamamlanma koşulları**
- Destructive migration kaldırılır.
- Room schema dosyaları repoya eklenir.
- Eski sürümden yeni sürüme geçiş testi geçer.

### US-06 — Kelime kataloğu yüklemesini güvenilir yap [P0]
İlk açılışta katalog eksiksiz yüklensin.

**Tamamlanma koşulları**
- Seed işlemi transaction içinde yürür.
- Aynı kelime tekrar eklenmez.
- Hata durumu gözlenebilir.
- Ekranlar seed işleminin tamamlanmasını beklerken takılı kalmaz.

### US-07 — Günlük tarih hesaplarını düzelt [P0]
Seri ve istatistikler cihazın yerel takvim günüyle uyumlu olsun.

**Tamamlanma koşulları**
- Oturum tarihleri tek standarda göre saklanır.
- Gün değişimi ve timezone/DST senaryoları test edilir.

### US-08 — Room sorgularını gözden geçir [P1]
Kelime arama ve tekrar listeleri büyük katalogda hızlı çalışsın.

**Tamamlanma koşulları**
- Sorgular ve indeksler incelenir.
- Gereksiz tekrar eden DAO metotları kaldırılır.
- Performans gerçek cihazda ölçülür.

### US-09 — Çevrimdışı senkronizasyon kuyruğu ekle [P1]
İnternet yokken yapılan işlemler bağlantı geldiğinde sunucuya aktarılabilsin.

**Tamamlanma koşulları**
- Her olayın benzersiz ID'si vardır.
- Room'a kayıt ile outbox kaydı tutarlıdır.
- WorkManager geçici hatalarda tekrar dener.
- Sunucu aynı olayı iki kez kaydetmez.
- Çakışma kuralı belgelenir.

## C. Backend ve AI

### US-10 — Kotlin backend temelini kur [P0]
Çalıştırılabilir bir Spring Boot servisi oluştur.

**Tamamlanma koşulları**
- Kotlin Spring Boot uygulaması açılır.
- Health endpoint yanıt verir.
- PostgreSQL Docker Compose ile ayağa kalkar.
- Flyway migration çalışır.
- Otomatik testler geçer.

### US-11 — Firebase kimlik doğrulamasını ekle [P0]
Kullanıcı yalnızca kendi verisine erişebilsin.

**Tamamlanma koşulları**
- Firebase ID token backend'de doğrulanır.
- UID doğrulanmış token'dan alınır.
- Geçersiz ve süresi dolmuş token reddedilir.
- Başka kullanıcının verisine erişim testle engellenir.

### US-12 — Gemini isteklerini backend'e taşı [P0]
Mobil uygulamada Gemini API anahtarı bulunmasın.

**Tamamlanma koşulları**
- Gemini anahtarı sunucu ortamında tutulur.
- İstek uzunluğu ve kota kontrol edilir.
- Hız sınırı ve timeout uygulanır.
- Prompt ve kimlik bilgileri loglanmaz.
- Android AI repository backend'i kullanır.

### US-13 — AI kullanım kotasını tamamla [P1]
Kullanıcı kalan hakkını doğru görebilsin.

**Tamamlanma koşulları**
- Sabit sayaç kaldırılır.
- Kota tek bir güvenilir kaynaktan yönetilir.
- Günlük/aylık sıfırlanma kuralı belirlenir.
- Çevrimdışı durumda gösterilecek bilgi tanımlanır.

## D. Ekran ve ürün kalitesi

### US-14 — İstatistikleri takvim günlerine göre hesapla [P1]
Son yedi günün verisi eksiksiz gösterilsin.

**Tamamlanma koşulları**
- Çalışılmayan günler sıfır olarak gösterilir.
- Timezone doğru uygulanır.
- Due kelime sayısı ile tamamlanmamış oturum sayısı ayrı tutulur.

### US-15 — Kelime defterini iyileştir [P2]
Arama ve filtreler birlikte doğru çalışsın.

**Tamamlanma koşulları**
- Filtre kombinasyonları test edilir.
- Boş liste ve arama sonucu bulunamaması için durum tasarlanır.

### US-16 — Loading, empty ve error durumlarını tamamla [P1]
Kullanıcı ekranın neden boş olduğunu veya ne yapması gerektiğini anlayabilsin.

**Tamamlanma koşulları**
- Her ana ekran yükleniyor, başarılı, boş ve hata durumlarını yönetir.
- Uygun yerlerde tekrar deneme sunulur.

### US-17 — Türkçe ve İngilizce metinleri tamamla [P1]
Arayüzün tüm metinleri seçilen dile uygun gösterilsin.

**Tamamlanma koşulları**
- Sabit ekran metinleri string resource'a taşınır.
- Türkçe ve İngilizce kaynaklar tamamlanır.

## E. Test ve yayın

### US-18 — CI testlerini genişlet [P0]
Her değişiklikte temel kalite kontrolleri otomatik çalışsın.

**Tamamlanma koşulları**
- Lint, unit test ve build çalışır.
- Room migration ve kritik domain testleri CI'a eklenir.
- Release build doğrulanır.

### US-19 — Release güvenliğini tamamla [P0]
Yayın paketinde gizli anahtar ve debug verisi bulunmasın.

**Tamamlanma koşulları**
- R8/minification test edilerek etkinleştirilir.
- HTTP BODY logging release'te kapalıdır.
- Secret taraması yapılır.
- Backup kapsamı belirlenir.

### US-20 — Google Play test dağıtımını hazırla [P0]
İmzalı AAB güvenli ve tekrarlanabilir şekilde üretilebilsin.

**Tamamlanma koşulları**
- İmzalama bilgileri GitHub Secrets'ta tutulur.
- Release workflow oluşturulur.
- Internal testing yapılır.
- Dağıtım ve geri alma adımları belgelenir.

### US-21 — Gizlilik ve kullanım metinlerini doğrula [P0]
Kullanıcıya gerçek veri işleme süreci doğru anlatılsın.

**Tamamlanma koşulları**
- Release sürümündeki veri akışı incelenir.
- KVKK/GDPR metinleri hukuk danışmanına inceletilir.
- Gizlilik politikası erişilebilir bir URL'de yayımlanır.
- Veri silme ve saklama süreçleri açıklanır.

### US-22 — Mağaza sayfasını hazırla [P1]
Kullanıcı uygulamanın sunduğu özellikleri doğru anlayabilsin.

**Tamamlanma koşulları**
- Türkçe ve İngilizce başlık/açıklamalar hazırlanır.
- Ekran görüntüleri ve grafikler tamamlanır.
- İçerik derecelendirmesi ve Data safety formu doğru doldurulur.

## Bir işi tamamlanmış sayma kuralı

- Kod gözden geçirildi.
- İlgili testler çalıştırıldı ve geçti.
- Hata, boş veri ve yüklenme durumları ele alındı.
- Güvenlik ve gizlilik etkisi kontrol edildi.
- Gerekli doküman güncellendi.
