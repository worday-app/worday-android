# Worday — Teknik Durum

**İncelenen kaynak:** `develop`  
**Güncelleme:** 24.11.2026  
**Yöntem:** Kaynak kod ve yapılandırma dosyalarının statik incelemesi; önceki iki teknik incelemenin karşılaştırılması.

> Bu belge mevcut kodun fotoğrafıdır. Uygulama bu incelemede cihazda çalıştırılmadı. Build, test ve gerçek cihaz davranışı doğrulanmadan bir özellik tamamlanmış sayılmaz.

## Kısa değerlendirme

Worday, Kotlin ve Jetpack Compose ile geliştirilen, İngilizce kelime öğrenme odaklı çok modüllü bir Android uygulaması. Domain, data, network ve feature ayrımı kurulmuş. Room üzerinde yerel veri, kelime kataloğu, onboarding, öğrenme, quiz, kelime defteri, istatistik ve Gemini bağlantısı için kod bulunuyor.

Proje yalnızca ekran taslaklarından ibaret değil; temel uygulama ve veri katmanlarının önemli kısmı yazılmış. Buna karşılık, ana öğrenme döngüsünün bazı parçaları birbirine tam bağlanmamış. Özellikle onboarding verisinin saklanması, kart cevaplarının tekrar sistemine etkisi, quiz sonucunun kalıcı kaydı ve günlük istatistiklerin doğruluğu çözülmeden kullanıcıya güvenilir bir öğrenme deneyimi sunulduğunu söyleyemeyiz.

**Genel karar: geliştirme aşamasında prototip. Henüz yayın adayı değil.**

## Modül yapısı

| Modül | Sorumluluk | Durum |
|---|---|---|
| `:app` | Uygulama başlangıcı ve navigasyon | Temel yapı var |
| `:core:domain` | Modeller, repository arayüzleri, use case ve SM-2 | İş kuralları var; test ve zaman yönetimi eksik |
| `:core:data` | Room, DAO, seed, DataStore ve WorkManager | Yerel veri altyapısı var; migration ve seed güvenilirliği kontrol edilmeli |
| `:core:network` | Retrofit/OkHttp ve Gemini bağlantısı | İstemci var; anahtarın mobil pakette bulunması güvenlik riski |
| `:core:common` | Ortak modeller ve yardımcılar | Temel yapı var |
| `:core:ui` | Tema, Compose bileşenleri ve TTS | Temel yapı var |
| `:feature:onboarding` | İlk kullanım, seviye ve hedef | Akış var; seviye/tercih aktarımı tamamlanmalı |
| `:feature:learn` | Günlük kelime öğrenme | Ekranlar var; cevapların ilerlemeye etkisi net değil |
| `:feature:quiz` | Çoktan seçmeli quiz | Temel akış var; kayıt ve boş veri senaryoları kontrol edilmeli |
| `:feature:wordbook` | Kaydedilen kelimeler | Listeleme ve filtreler var; uç durum testleri eksik |
| `:feature:stats` | Seri ve çalışma istatistikleri | Ekran var; hesaplamalar doğrulanmalı |

## Öğrenme akışı

### Onboarding

Kullanıcıdan seviye ve günlük hedef alınan bir akış bulunuyor. Ancak seçilen seviyenin ve hedefin Room tarafındaki kullanıcı verisine doğru aktarılıp aktarılmadığı kontrol edilmeli. İkinci incelemede seviye testi seçeneklerinde placeholder içerik ve seed tamamlanmadan onboarding ekranının boş kalabilmesi de raporlandı. Bu iki bulgu ilgili ekran ve veri akışı üzerinde yeniden doğrulanmalı.

### Kelime öğrenme ve SM-2

`Sm2Algorithm` içinde kalite 0–5 aralığında sınırlandırılıyor. Yanlış cevapta tekrar sayısı sıfırlanıyor ve aralık bir güne çekiliyor. Başarılı cevaplarda ilk iki aralık 1 ve 6 gün; sonraki aralık ease factor üzerinden hesaplanıyor. Ease factor alt sınırı 1.3, üç başarılı tekrar sonrası kelime öğrenilmiş sayılıyor.

Bunlar algoritmanın hesaplama kısmının bulunduğunu gösteriyor; tam bir tekrar sistemi olduğu anlamına gelmiyor.

- Algoritma zamanı doğrudan `System.currentTimeMillis()` ile alıyor. Test edilebilirlik için saat bağımlılığı ayrılmalı.
- `DEFAULT_EASE_FACTOR` tanımlı ancak mevcut hesaplamada kullanılmıyor.
- Öğrenilmiş kelimenin yeniden yanlış cevaplanması halinde ne olacağı ürün kuralı olarak belirlenmeli.
- Öğrenme kartındaki “Biliyorum/Bilmiyorum” seçimlerinin SM-2 kaydını hangi kuralla güncellediği netleştirilmeli.
- Quiz yarıda bırakıldığında cevaplanan kelimelerin ilerleme kaydının nasıl ele alındığı kontrol edilmeli.
- Tekrarı gelen kelimelerin yeniden çalışma listesine girdiği uçtan uca doğrulanmalı.

SM-2 için test dosyası hazırlanmış olsa da testlerin çalıştırıldığına dair doğrulanmış sonuç yok. Bu nedenle algoritma testleri tamamlanmış kabul edilmiyor.

### Quiz

Soru hazırlama, seçenek gösterme ve cevap değerlendirme kodu bulunuyor. Ancak aşağıdaki durumlar ürün davranışını bozabilir:

- Boş kelime listesinde yükleniyor durumunun kapanmaması.
- Soru sayısı sıfırken başarı oranının hesaplanması.
- Cevap, SM-2 ve günlük oturum kayıtlarının ayrı coroutine'lerde çalışması nedeniyle işlem sırasının garanti edilmemesi.
- Çeldirici havuzu yetersiz olduğunda aynı veya eksik seçeneklerin gösterilmesi.
- Quiz yarıda bırakıldığında ilerlemenin yanlışlıkla tamamlanmış gibi kaydedilmesi.

Bunlar için ViewModel ve repository testleri ile gerçek cihaz akışı birlikte kontrol edilmeli.

## Yerel veri

Room tarafında kelimeler, kullanıcı ilerlemesi ve günlük oturumlar için tablolar bulunuyor. Kelime kataloğu JSON dosyasından yükleniyor; kullanıcı tercihleri DataStore'da tutuluyor.

Öne çıkan riskler:

1. `fallbackToDestructiveMigration()` kullanımı, şema değişikliğinde kullanıcı verisini silebilir.
2. İlerleme sorgularında sabit `userId = 1` kullanılması, gerçek hesap ve çoklu kullanıcı senaryosuna uygun değil.
3. Günlük oturum sorguları ile Worker'ın kullandığı tarih değerleri aynı standarda bağlı değilse seri hesabı yanlış sonuç verebilir.
4. Seed işleminin Room callback içinden ayrı coroutine ile başlatılması, hata ve yeniden başlatma durumlarında yarış koşulu oluşturabilir.
5. Kelime kimliğinin JSON sırasına bağlı olması halinde katalog sırası değiştiğinde ilerleme yanlış kelimeye bağlanabilir. Kalıcı ve açık bir word ID kullanılmalı.
6. Seed sürümünün izlenmesi gerekiyor; yeni katalog sürümünde hangi kayıtların güncelleneceği tanımlı değil.
7. İkinci incelemede kelime içeriğinde kısa tanımlar, örnek cümlelerde kaynak/atıf artıkları ve eksik IPA alanları raporlandı. Sayılar bu güncellemede bağımsız olarak yeniden hesaplanmadı; içerik kalite kontrolü yapılmalı.

Room migration dosyaları ve schema geçmişi de yayın öncesinde doğrulanmalı.

## İçerik ve lisans

Kelime kataloğunun kaynağı ve kullanım hakkı netleştirilmeden uygulama ticari olarak dağıtılmamalı. İkinci incelemede Oxford 5000 ile ilişkilendirilen içerik için lisans belirsizliği not edilmişti. Kelime listesi, tanımlar, örnek cümleler ve IPA verilerinin her biri için kaynak ve kullanım izni kayıt altına alınmalı. Kaynak gösterilmesi tek başına yeniden dağıtım hakkı vermez.

## Gemini ve ağ katmanı

Gemini çağrısı Android uygulamasından doğrudan yapılıyor. API anahtarı BuildConfig'e `local.properties` üzerinden aktarılıyor olsa bile dağıtılan APK/AAB içinden çıkarılabilir. Anahtarın repoda olmaması bu riski ortadan kaldırmıyor.

AI kullanım kotasının da tamamlanmış bir sunucu akışı olmadığı görülüyor; sabit sayaç ve tamamlanmamış tüketim mantığı bulunuyor. HTTP BODY logging açıkken istek/yanıt içeriği loglara düşebilir.

Planlanan Kotlin + Spring Boot backend henüz çalışan servis değil. PostgreSQL, Flyway, Firebase token doğrulama, Gemini proxy ve Android senkronizasyonu mimari taslak seviyesinde. İlk aşamada modüler monolit yeterli; Room yerel veri kaynağı olarak kalmalı ve SM-2 Android'de çalışmaya devam etmeli.

## İstatistik, kelime defteri ve arayüz

- Haftalık grafik `sessions.takeLast(7)` yaklaşımıyla hazırlanıyorsa çalışılmayan takvim günleri eksik kalabilir.
- `dueCount` alanının gecikmiş kelimeleri mi yoksa tamamlanmamış oturumları mı saydığı netleştirilmeli.
- Wordbook için arama ve filtreler bulunuyor; filtre kombinasyonları, sıralama ve boş sonuç durumları test edilmeli.
- Bazı ekran metinleri Türkçe, bazıları İngilizce. Sabit metinler string resource'a taşınmalı.
- Erişilebilirlik etiketleri, yazı boyutu ve renk kontrastı ayrıca kontrol edilmeli.

## Mimari ve bakım

Feature modüllerinin `:core:data` gibi somut katmanlara doğrudan bağımlı olup olmadığı Gradle bağımlılıkları üzerinden kontrol edilmeli. Feature katmanı mümkün olduğunca domain arayüzleri üzerinden ilerlemeli. Bu bağımlılık ihlali ikinci incelemede raporlandı; kesin modül bazlı doğrulama yapılmadan tüm feature'lar için genellenmemeli.

`WordayApplication` içinde `StreakResetWorker.schedule(WorkManager.getInstance(this))` çağrısı var. Ancak bu dosyada Hilt Worker factory yapılandırması görünmüyor. Worker'ın Hilt ile nasıl oluşturulduğu ve gerçekten çalışıp çalışmadığı ayrıca test edilmeli.

## Yayın tarafında görülenler

- `targetSdk` ve `compileSdk` 36.
- Release build'de minification kapalı.
- ProGuard dosyası varsayılan/başlangıç seviyesinde.
- Gemini anahtarı mobil BuildConfig'e aktarılıyor.
- HTTP BODY logging ayarı release için gözden geçirilmeli.
- CI'ın debug build ve temel kontrolleri çalıştırması, imzalı release AAB'nin hazır olduğu anlamına gelmiyor.
- Firebase ve AdMob bağımlılığının projede bulunması, SDK'nın release'te etkin olduğu veya veri topladığı anlamına tek başına gelmez.
- Gizlilik ve mağaza beyanları gerçek release veri akışı üzerinden hazırlanmalı.

## Öncelik sırası

### Önce veri kaybı ve içerik riskleri
- Kalıcı word ID ve seed sürümleme.
- Room migration ve mevcut kullanıcı verisini koruma.
- Kelime kataloğunun kaynak/lisans ve kalite kontrolü.
- Seed tamamlanma ve hata davranışı.

### Ardından ana öğrenme döngüsü
- Onboarding seviye/hedef aktarımı.
- Öğrenme kartı cevaplarının ilerlemeye kaydı.
- Quiz, SM-2 ve günlük oturum kayıtlarının tek ve tutarlı akışı.
- Tekrarı gelen kelimelerin yeniden gösterilmesi.
- Streak ve haftalık istatistiklerin takvim gününe göre hesaplanması.

### Sonra yayın altyapısı
- Gemini proxy ve sunucu tarafı kota.
- Testler, CI ve Worker doğrulaması.
- Release signing, R8, log ve backup kontrolleri.
- Gizlilik, mağaza metinleri ve içerik lisansı.

## İncelemenin sınırları

Bu rapor statik inceleme ve iki ayrı audit'in karşılaştırmasına dayanır. İkinci incelemede raporlanan kelime kataloğu sayıları ve bazı modül bağımlılıkları bu güncellemede bağımsız olarak yeniden sayılmadı. Uygulama cihazda çalıştırılmadı; Gradle build, unit/instrumentation testleri ve Play Console ayarları doğrulanmadı. Bu nedenle doğrulanmamış bulgular yapılacak iş olarak tutuldu, tamamlanmış hata gibi sunulmadı.
