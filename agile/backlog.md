# Worday — İş Listesi

Bu liste iki teknik incelemede çıkan ortak ve ek bulgular birleştirilerek hazırlandı. Öncelik, kullanıcı verisinin korunması ve temel öğrenme akışının doğru çalışması. Bir iş ancak kodu tamamlanıp ilgili kontroller çalıştırıldığında kapatılmalı.

**Öncelik tanımı**
- **P0:** Veri kaybı, yanlış öğrenme kaydı veya güvenlik riski. Yayından önce çözülmeli.
- **P1:** Temel ürün akışını ve güvenilirliği etkiliyor.
- **P2:** Kalite ve sonraki sürüm iyileştirmesi.

## Güncel çalışma

`fix/room-migration-and-seed` dalında WD-01, WD-02 ve WD-03 için ilk kod değişiklikleri yapıldı. Build, test ve cihaz doğrulaması bekleniyor; bu işler henüz kapatılmadı.

## 1. Veri ve kelime kataloğu

### WD-01 — Room migration'ı güvenli hale getir [P0] — Doğrulama bekliyor
- Destructive migration kaldırılmalı. Kod değişikliği yapıldı; migration testi henüz çalıştırılmadı.
- Room schema dosyaları sürüm kontrolüne alınmalı.
- Mevcut kullanıcı verisini koruyan migration yazılmalı.
- Eski şemadan yeni şemaya geçiş testi geçmeli.

### WD-02 — Kelime ID'lerini kalıcı hale getir [P0] — Doğrulama bekliyor
- Kelime kimliği JSON sırasına bağlı olmamalı.
- Her kelimenin sabit ve benzersiz ID'si olmalı. Katalog kimliği ve deterministik Room ID eklendi; build ve katalog testi bekliyor.
- Var olan ilerleme kayıtları yeni ID'lerle eşleştirilmeli.
- Katalog sırası değiştiğinde kullanıcı ilerlemesi aynı kelimede kalmalı.

### WD-03 — Seed sürecini güvenilir yap [P0] — Doğrulama bekliyor
- Seed tek transaction içinde tamamlanmalı. Transaction ve katalog sürümü eklendi; gerçek veritabanında doğrulama bekliyor.
- Aynı katalog ikinci kez yüklenince kayıt çoğalmamalı.
- Seed sürümü saklanmalı.
- Hata durumunda ekranlar sonsuz yüklenmede kalmamalı.
- Seed'in uygulama açılışında birden fazla kez tetiklenmesi önlenmeli.

### WD-04 — Kelime içeriğini kontrol et [P0]
- Kısa/boş tanımlar, bozuk örnek cümleler ve eksik IPA alanları taranmalı.
- Atıf artıkları ve biçim bozuklukları temizlenmeli.
- Örnek cümlelerin dil ve seviye uygunluğu gözden geçirilmeli.
- Kontrol edilen kayıt sayısı ve kalan sorunlar raporlanmalı.

### WD-05 — Kelime kataloğunun lisansını netleştir [P0]
- Kelime listesi, tanımlar, örnekler ve IPA için kaynaklar belirlenmeli.
- Ticari kullanım ve yeniden dağıtım izinleri doğrulanmalı.
- Lisansı belirsiz içerik yayın paketinde kullanılmamalı.
- Kaynak ve izin bilgileri proje içinde kayıt altına alınmalı.

### WD-06 — Tarih ve günlük oturum standardını belirle [P0]
- Günlük oturumlar tek bir tarih/timezone kuralıyla saklanmalı.
- Sorgular ve Worker aynı gün başlangıcı standardını kullanmalı.
- Timezone ve yaz/kış saati değişimi test edilmeli.

## 2. Onboarding ve öğrenme

### WD-07 — Onboarding tercihlerini kalıcı kaydet [P0]
- Seçilen CEFR seviyesi ve günlük hedef doğru repository/use case üzerinden saklanmalı.
- Uygulama yeniden açıldığında tercihler korunmalı.
- Varsayılan seviye ve test atlama davranışı belirlenmeli.

### WD-08 — Seviye testini tamamla [P1]
- Placeholder seçenekler gerçek soru ve cevaplarla değiştirilmeli.
- Puanın CEFR seviyesine nasıl çevrildiği tanımlanmalı.
- Eksik cevap ve testten çıkma durumları ele alınmalı.
- Test sonucu onboarding tercihine aktarılmalı.

### WD-09 — Öğrenme kartı cevaplarını ilerlemeye bağla [P0]
- “Biliyorum/Bilmiyorum” seçimlerinin iş kuralı belirlenmeli.
- Her cevap yalnızca bir kez işlenmeli.
- İlerleme Room'a kalıcı yazılmalı.
- Uygulama yeniden açıldığında sonuç korunmalı.

### WD-10 — SM-2 algoritmasını test et [P0]
- Kalite 0–5 dışında kabul edilmemeli.
- Yanlış cevapta tekrar sayısı ve aralık beklenen şekilde güncellenmeli.
- İlk iki başarılı aralık 1 ve 6 gün olmalı.
- Ease factor 1.3 altına düşmemeli.
- Zaman sabitlenebilir olmalı.
- Öğrenilmiş kelimenin yeniden yanlış cevaplanma kuralı netleştirilmeli.
- Uzun süre gecikmiş kelimeler için tekrar davranışı doğrulanmalı.

### WD-11 — Gerçek tekrar döngüsünü tamamla [P0]
- Due kelimeler tekrar listesine girmeli.
- Cevap sonrası yeni tekrar tarihi kaydedilmeli.
- Öğrenilmiş kelimeler ürün kuralına göre yeniden değerlendirilebilmeli.
- Tekrar listesi uygulama kapatılıp açıldığında bozulmamalı.

### WD-12 — Quiz kayıtlarını tek akışta tutarlı hale getir [P0]
- Cevap, SM-2 ve günlük oturum kaydı aynı işlem sırasına göre yönetilmeli.
- Aynı cevap iki kez işlenmemeli.
- Quiz yarıda bırakıldığında yalnızca gerçekten cevaplanan sorular kaydedilmeli.
- Boş soru listesi ve sıfır soru durumu hata üretmemeli.
- Quiz sonucu tekrar açılışta doğru görünmeli.

### WD-13 — Quiz seçeneklerini güvenilir üret [P1]
- Çeldiriciler doğru cevaptan farklı olmalı.
- Aynı seçenek birden fazla kez gösterilmemeli.
- Yeterli çeldirici yoksa ekran kontrollü davranmalı.
- Seviye ve kelime havuzuna göre seçenek kalitesi kontrol edilmeli.

## 3. İstatistik ve kullanıcı ekranları

### WD-14 — Streak hesabını takvim gününe göre düzelt [P0]
- Seri sabit 24 saat hesabıyla değil, yerel takvim günüyle hesaplanmalı.
- Aynı gün içindeki birden fazla çalışma seriyi birden fazla artırmamalı.
- Worker'ın gecikmesi seri hesabını bozmamalı.
- WorkManager işi gerçek cihazda doğrulanmalı.

### WD-15 — WorkManager/Hilt yapılandırmasını doğrula [P0]
- Worker'ın nasıl oluşturulduğu ve bağımlılıklarını nasıl aldığı netleştirilmeli.
- Gerekliyse HiltWorkerFactory Application/WorkManager yapılandırmasına eklenmeli.
- İşin tekrar planlanması ve başarısızlıkta davranışı test edilmeli.

### WD-16 — Haftalık istatistikleri takvim günleriyle hesapla [P1]
- Son yedi takvim günü gösterilmeli.
- Çalışılmayan günler sıfır olarak görünmeli.
- Timezone değişimi ele alınmalı.
- Due kelime sayısı ile oturum sayısı birbirinden ayrılmalı.

### WD-17 — Wordbook akışını tamamla [P1]
- Arama ve filtreler birlikte doğru çalışmalı.
- Kaydet/kaldır işlemi kalıcı olmalı.
- Boş liste, boş arama sonucu ve hata durumları gösterilmeli.
- Sıralama ve filtre kombinasyonları test edilmeli.

### WD-18 — Ekran durumlarını tamamla [P1]
- Ana ekranlar loading, success, empty ve error durumlarını yönetmeli.
- Seed veya ağ hatası ekranı sonsuz yüklemede bırakmamalı.
- Tekrar deneme gereken yerde kullanıcıya sunulmalı.

### WD-19 — Yerelleştirme ve erişilebilirliği düzelt [P1]
- Sabit metinler string resource'a taşınmalı.
- Türkçe ve İngilizce metinler tutarlı olmalı.
- TalkBack etiketleri, yazı boyutu ve kontrast kontrol edilmeli.

## 4. Mimari, backend ve AI

### WD-20 — Modül bağımlılıklarını gözden geçir [P1]
- Feature modüllerinin `:core:data` gibi somut katmanlara doğrudan bağımlılığı kontrol edilmeli.
- Mümkün olan yerde domain arayüzleri kullanılmalı.
- Gradle bağımlılık yönü ve modül sınırları dokümante edilmeli.

### WD-21 — Kotlin backend temelini kur [P1]
- Kotlin + Spring Boot modüler monolit oluşturulmalı.
- Health endpoint çalışmalı.
- PostgreSQL Docker Compose ile ayağa kalkmalı.
- Flyway migration ve temel testler geçmeli.

### WD-22 — Firebase token doğrulamasını ekle [P1]
- ID token backend'de doğrulanmalı.
- UID yalnızca doğrulanmış token'dan alınmalı.
- Başka kullanıcının verisine erişim testle engellenmeli.

### WD-23 — Gemini çağrısını backend'e taşı [P0]
- Android BuildConfig içindeki Gemini anahtarı kaldırılmalı.
- Anahtar yalnızca sunucu ortamında tutulmalı.
- İstek boyutu, timeout ve hata yönetimi eklenmeli.
- Prompt ve token loglanmamalı.
- Android AI repository backend'i kullanmalı.

### WD-24 — AI kotasını sunucuda yönet [P1]
- Sabit sayaç ve boş tüketim implementasyonu kaldırılmalı.
- Kota kullanıcı bazında tek kaynaktan yönetilmeli.
- Günlük/aylık yenilenme kuralı belirlenmeli.
- Rate limit ve kötüye kullanım kontrolleri eklenmeli.

### WD-25 — Çevrimdışı senkronizasyonu tasarla ve uygula [P2]
- Room yerel veri kaynağı olarak kalmalı.
- Yerel işlemle outbox kaydı atomik olmalı.
- Olayların benzersiz ID'si olmalı.
- Sunucu aynı olayı ikinci kez kaydetmemeli.
- Pull cursor ve çakışma çözümü test edilmeli.
- Oturum süresi dolduğunda çevrimdışı öğrenme devam etmeli.

## 5. Test ve yayın

### WD-26 — Kritik testleri CI'a ekle [P0]
- Domain/SM-2 testleri çalışmalı.
- Room DAO ve migration testleri çalışmalı.
- Quiz ve onboarding ViewModel testleri eklenmeli.
- WorkManager davranışı test edilmeli.
- CI lint, unit test ve build adımlarını çalıştırmalı.

### WD-27 — Release paketini güvenli hale getir [P0]
- Release loglarında BODY logging kapalı olmalı.
- Secret taraması yapılmalı.
- R8/minification gerçek modellerle test edilerek açılmalı.
- Backup kapsamı incelenmeli.
- İmzalama anahtarları repoda tutulmamalı.

### WD-28 — Internal testing dağıtımını hazırla [P0]
- İmzalı AAB üretimi doğrulanmalı.
- VersionCode ve versionName kontrol edilmeli.
- Play Console Internal testing kanalında test edilmeli.
- Temiz kurulum ve uygulama güncelleme senaryoları denenmeli.
- Geri alma ve sürüm notları hazırlanmalı.

### WD-29 — Gizlilik ve mağaza beyanlarını doğrula [P0]
- Release sürümünün gerçek veri akışı çıkarılmalı.
- Firebase, AdMob ve Gemini'nin fiili kullanımı kontrol edilmeli.
- Data safety ve gizlilik metinleri bu akışla eşleşmeli.
- Veri saklama/silme ve yurt dışı aktarım bilgileri netleştirilmeli.
- Hukuki metinler yetkin bir hukuk danışmanına inceletilmeli.

### WD-30 — Mağaza sayfasını tamamla [P1]
- Türkçe ve İngilizce açıklamalar hazırlanmalı.
- Ekran görüntüleri, ikon ve grafikler tamamlanmalı.
- İçerik derecelendirmesi ve uygulama erişim bilgileri doldurulmalı.
- Destek iletişim kanalı belirlenmeli.

## İş kapatma kuralı

Bir iş ancak ilgili kod değişikliği tamamlandığında, uygun testler gerçekten çalıştırılıp geçtiğinde ve gerekiyorsa doküman güncellendiğinde kapatılır. Sadece dosya veya test yazılmış olması yeterli değildir.
