# Worday — Proje Durumu

**İncelenen branch:** `develop`  
**İnceleme tarihi:** 24.11.2026  
**İnceleme yöntemi:** Kaynak kod ve proje yapılandırmalarının statik incelenmesi

> Bu rapor kodun mevcut halini anlatır. Uygulama bu inceleme sırasında cihazda çalıştırılmadı; Gradle build ve test sonuçları ayrıca doğrulanmalıdır.

## Genel durum

Worday, Kotlin ve Jetpack Compose ile geliştirilmiş, İngilizce kelime öğrenmeye yönelik çok modüllü bir Android uygulaması.

Projede Clean Architecture ve MVVM yaklaşımına uygun bir ayrım kurulmuş. Kelime kataloğu, Room veritabanı, DataStore tercihleri, SM-2 tekrar algoritması, onboarding, öğrenme ekranı, quiz, kelime defteri, istatistikler ve Gemini ile örnek cümle üretimi için kod bulunuyor.

**Mevcut değerlendirme: geliştirme aşamasında bir prototip.** Ana özelliklerin önemli bölümü yazılmış olsa da bazı akışlar tamamlanmamış ve yayın öncesi çözülmesi gereken veri, güvenlik ve test konuları var.

## Modüller

| Modül | Görevi | İncelemede görülen durum |
|---|---|---|
| `:app` | Uygulama başlangıcı ve navigasyon | Temel yapı mevcut |
| `:core:domain` | Modeller, repository arayüzleri, use case'ler ve SM-2 | İş kuralları mevcut; test ve zaman yönetimi iyileştirilmeli |
| `:core:data` | Room, DAO, kelime seed işlemi, DataStore ve WorkManager | Yerel veri katmanı mevcut; migration ve seed riskleri var |
| `:core:network` | Retrofit/OkHttp ve Gemini bağlantısı | İstemci bağlantısı mevcut; API anahtarı güvenli değil |
| `:core:common` | Ortak UI ve sonuç modelleri | Temel yardımcı yapılar mevcut |
| `:core:ui` | Tema, Compose bileşenleri ve TTS | Temel arayüz altyapısı mevcut |
| `:feature:onboarding` | İlk kullanım, seviye ve hedef seçimi | Akış mevcut; seviye testi tamamlanmalı |
| `:feature:learn` | Ana ekran ve kelime öğrenme | Temel ekranlar mevcut; kart cevapları SM-2 ile tamamıyla bağlı görünmüyor |
| `:feature:quiz` | Çoktan seçmeli quiz ve sonuç | Temel akış mevcut; boş liste ve kayıt sırası test edilmeli |
| `:feature:stats` | Seri ve çalışma istatistikleri | Ekran mevcut; bazı metriklerin hesabı doğrulanmalı |
| `:feature:wordbook` | Kaydedilen kelimeler, arama ve filtre | Temel özellikler mevcut |

## Domain ve SM-2

`Sm2Algorithm.calculate` kalite değerini 0–5 aralığında sınırlandırıyor. Yanlış cevapta tekrar sayısı sıfırlanıyor ve aralık bir güne çekiliyor. Başarılı cevaplarda ilk iki aralık 1 ve 6 gün; sonraki aralık ease factor üzerinden hesaplanıyor. Ease factor alt sınırı 1.3. Üç başarılı tekrar sonrasında kelime öğrenilmiş olarak işaretleniyor.

Kontrol edilmesi gerekenler:

- `DEFAULT_EASE_FACTOR` tanımlı ancak algoritmada kullanılmıyor.
- Zaman doğrudan `System.currentTimeMillis()` üzerinden alınıyor. Testlerde sabit zaman kullanabilmek için zaman kaynağı ayrılmalı.
- Yanlış cevapta önceki tekrar geçmişinin nasıl korunacağı net değil.
- Uzun süre gecikmiş kelimeler için ayrı bir önceliklendirme bulunmuyor.
- Geçersiz interval, tekrar sayısı ve ease factor değerleri test edilmeli.
- Öğrenilmiş kelime daha sonra yanlış cevaplanırsa durumunun nasıl değişeceği ürün kuralı olmalı.

## Yerel veri: Room ve DataStore

Room veritabanında `words`, `user_progress` ve `daily_sessions` tabloları bulunuyor. Kelime kataloğu `words.json` dosyasından yükleniyor. DataStore günlük hedef, seviye, ses, bildirim ve AI kullanım sayacı gibi tercihleri saklıyor.

Önemli sorunlar:

1. `fallbackToDestructiveMigration()` şema değişiminde kullanıcı verisini silebilir.
2. İlerleme sorgularında sabit `userId = 1` kullanılıyor. Hesap ve cihazlar arası senkronizasyon eklendiğinde bu yapı değişmeli.
3. Günlük oturum sorgusu timestamp eşitliği kullanıyor. Worker gün başlangıcı değerini aradığı için kayıtlar aynı standarda normalize edilmezse oturum bulunamayabilir.
4. Seed işlemi Room callback'i içinden ayrı bir CoroutineScope ile başlatılıyor. Hata ve uygulama yeniden başlatma durumları test edilmeli.
5. `onCreate` ve `onOpen` üzerinden seed başlatılması gereksiz tekrar veya yarış oluşturabilir.
6. Room migration dosyaları ve schema geçmişi görünmüyor.

## Ağ bağlantısı ve Gemini

Gemini isteği şu anda Android uygulamasından doğrudan gönderiliyor. API anahtarı `:core:network` içindeki BuildConfig alanına `local.properties` üzerinden aktarılıyor. Anahtar repoya eklenmese bile dağıtılan APK/AAB içinden çıkarılabilir.

AI kullanım sayacı da tamamlanmış görünmüyor: sayaç için sabit değer ve boş tüketim implementasyonu bulunuyor. HTTP BODY logging açık olduğunda istek ve yanıt içeriği loglara düşebilir.

**Yapılacak:** Gemini çağrısını backend'e taşımak, kotayı sunucuda uygulamak ve loglardan prompt/kimlik bilgilerini çıkarmak.

## Ekran ve özellik bulguları

### Öğrenme
- Ana ekran günlük kelimeleri yüklüyor ve seed durumunu bekliyor.
- Kart kaydırma görülen kart sayısını güncelliyor.
- “Biliyorum” seçimi sonraki kelimeye ilerliyor; “Bilmiyorum” quiz akışına yönlendiriyor.
- Kart cevaplarının SM-2 ilerlemesini doğrudan güncellediği görülmüyor.
- Kaydetme ve TTS işlevleri mevcut.
- Bazı ekran metinleri Türkçe, bazıları İngilizce. Metinler ortak yerelleştirme kaynaklarına taşınmalı.

### Quiz
- Günlük kelimelerden soru hazırlanıyor; seçenekler ve %60 geçme eşiği kullanılıyor.
- Cevap seçimi SM-2 use case'ini çağırıyor.
- Boş kelime listesinde loading durumunun kapanmaması ve toplam soru sıfırken başarı oranının hesaplanması sorun çıkarabilir.
- İlerleme, cevap ve oturum kaydı farklı coroutine'lerde yürütülüyor. İşlem sırası ve veri tutarlılığı test edilmeli.
- Debug logları release politikasıyla uyumlu hale getirilmeli.

### İstatistikler
- Kullanıcı ilerlemesi ve son 30 günlük oturumlar izleniyor.
- Haftalık grafik `sessions.takeLast(7)` ile oluşturuluyor. Çalışılmayan günler ve takvim günü sınırları doğru temsil edilmeyebilir.
- `dueCount` alanının gecikmiş kelimeleri mi yoksa tamamlanmamış oturumları mı saydığı kontrol edilmeli.

### Kelime defteri
- Kaydedilen kelimeler Flow ile izleniyor.
- Arama ve All/Learning/Learned filtreleri bulunuyor.
- Kayıt kaldırılabiliyor.
- Filtre kombinasyonları, sıralama ve boş/hata durumları test edilmeli.

## Güvenlik ve yayın hazırlığı

- Manifest'te internet ve ağ durumu izinleri bulunuyor.
- Android backup açık; hangi kullanıcı verilerinin yedekleneceği belirlenmeli.
- Release minification kapalı.
- ProGuard dosyası varsayılan şablon.
- CI lint, unit test ve debug APK üretiyor; release AAB dağıtımı görünmüyor.
- Firebase ve AdMob bağımlılıkları version catalog'da bulunuyor. Ancak bu, SDK'ların uygulamada etkin olduğu veya veri topladığı anlamına gelmez. Release build ayrıca kontrol edilmeli.
- Gizlilik ve kullanım metinleri gerçek veri akışına göre tamamlanmalı.

## Öncelikli işler

### P0 — Yayından önce
- Gemini anahtarını mobil paketten kaldırıp backend'e taşımak.
- Room destructive migration kullanımını kaldırmak ve migration testleri eklemek.
- Günlük oturum tarihlerini tek standarda geçirmek.
- SM-2 ve quiz için otomatik testleri tamamlamak.
- Release log, backup ve imzalama ayarlarını kontrol etmek.
- Gerçek veri akışını inceleyip gizlilik beyanlarını buna göre güncellemek.

### P1 — Ürün akışları
- Öğrenme kartı cevaplarını SM-2 ile doğru şekilde ilişkilendirmek.
- Onboarding seviye testini tamamlamak.
- Haftalık istatistikleri takvim günlerine göre hesaplamak.
- Loading, boş ve hata ekranlarını tamamlamak.
- Arayüzdeki Türkçe/İngilizce metinleri tutarlı hale getirmek.
- Hesap ve senkronizasyon altyapısını geliştirmek.

### P2 — Sonraki kalite işleri
- Erişilebilirlik ve performans testleri.
- UI testleri.
- Analitik olaylarının tanımlanması.
- Mağaza görselleri ve destek kanalları.

## İncelemenin sınırları

Bu rapor kaynak kodun statik incelemesine dayanır. Uygulamanın cihazdaki davranışı, Gradle build, otomatik testler ve Play Console ayarları bu rapor kapsamında çalıştırılarak doğrulanmadı.

Bu nedenle tabloda “mevcut” yazan bir özellik için bile yayın öncesinde build ve cihaz testi gerekir. Özellikle kritik akışlar için test sonucu alınmadan tamamlandı kabul edilmemelidir.
