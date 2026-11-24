# Worday — Teknik Proje Röntgeni

- İncelenen dal: `develop`
- İnceleme tarihi: 24.11.2026
- Kapsam: Git ağacı (325 kayıt), Gradle yapılandırması, uygulama manifesti, navigasyon, `:core:domain`, `:core:data`, `:core:network` ve ana `:feature` ViewModel/screen dosyaları.
- Not: Bu rapor statik kod incelemesidir. Android cihaz/emülatör üzerinde çalıştırma, Gradle build, test ve Play Console doğrulaması yapılmamıştır. Bu nedenle çalışma zamanı davranışı kesinleşmiş kabul edilmemelidir.

## Yönetici özeti

Worday, Kotlin + Jetpack Compose tabanlı, çok modüllü bir İngilizce kelime öğrenme uygulamasıdır. Clean Architecture/MVVM yönünde anlamlı bir ayrıştırma yapılmış; yerel kelime kataloğu, Room, DataStore, SM-2, onboarding, günlük öğrenme, quiz, kelime defteri, istatistik ve Gemini örnek cümle entegrasyonu oluşturulmuştur.

Mevcut durum: **işlevsel prototip / geliştirme sürümü**. Ürün akışlarının önemli bölümü kodlanmış olsa da veri bütünlüğü, test kapsamı, güvenlik, hata yönetimi, erişilebilirlik, hesap/senkronizasyon ve mağaza hazırlığı tamamlanmadan üretim sürümü olarak değerlendirilmemelidir.

## Modül envanteri

| Modül | Sorumluluk | Durum |
|---|---|---|
| `:app` | Application, MainActivity, Navigation Compose | Başlangıç ve ana navigasyon mevcut |
| `:core:domain` | Word/UserProgress modelleri, repository sözleşmeleri, use case'ler, SM-2 | Temel iş mantığı mevcut; test ve tarih soyutlaması eksik |
| `:core:data` | Room, DAO, JSON seed, DataStore, repository, WorkManager | Yerel veri katmanı mevcut; migration ve veri bütünlüğü riski |
| `:core:network` | Retrofit/OkHttp, Gemini DTO ve repository | Gemini istemcisi mevcut; istemci tarafı sır yönetimi riskli |
| `:core:common` | UiState, UiEvent, NetworkResult, Flow yardımcıları | Ortak yapı mevcut |
| `:core:ui` | Compose bileşenleri, tema, TTS | Temel tasarım sistemi mevcut |
| `:feature:onboarding` | Hoş geldin, seviye testi, hedef seçimi | Akış mevcut; seviye testinde cevap/şık modeli tamamlanmalı |
| `:feature:learn` | Ana ekran, günlük kartlar, öğrenme ekranı | Temel akış mevcut; cevapların öğrenme motoruna etkisi netleştirilmeli |
| `:feature:quiz` | Çoktan seçmeli quiz, sonuç | Temel akış mevcut; boş liste ve oturum tamamlama edge-case'leri |
| `:feature:stats` | Seri, başarı, haftalık grafik | Görselleştirme mevcut; veri hesaplarının doğrulanması gerekli |
| `:feature:wordbook` | Kaydedilen kelimeler, arama ve filtre | Temel işlevler mevcut |

## Domain ve SM-2

`Sm2Algorithm.calculate` kalite değerini 0..5 ile sınırlar. Başarısız cevapta tekrar sayısı sıfırlanır ve interval bir güne çekilir; başarılı cevapta ilk iki aralık 1 ve 6 gündür, sonraki aralık ease factor ile çarpılır. Ease factor alt sınırı 1.3'tür. Üç başarılı tekrar sonrası `isLearned` true olur.

Bulunan riskler:
1. `DEFAULT_EASE_FACTOR` tanımlı ancak kullanılmıyor.
2. `System.currentTimeMillis()` algoritmaya gömülü; deterministik test için Clock/enjekte edilen zaman gerekir.
3. Yanlış cevapta önceki interval ve tekrar geçmişinin korunup korunmayacağı ürün kuralı olarak netleştirilmeli.
4. Büyük gecikme (ör. 100 gün) birikmiş tekrarları nasıl sunacak? Mevcut DAO yalnızca `nextReviewDate <= now` ile geciken kelimeleri listeler; gecikme süresine göre önceliklendirme yok.
5. Negatif/bozuk interval, taşma, geçersiz ease factor ve tekrar sayısı için giriş doğrulaması yok.
6. `isLearned` kriteri, kullanıcının daha sonra yanlış cevap vermesi halinde yeniden false olabilir; bu davranış açıkça tanımlanmalı.

## Data / Room

Room v1; `words`, `user_progress`, `daily_sessions` tablolarını kullanır. DAO'lar kelime arama/filtreleme, due review, kaydetme, ilerleme ve günlük oturum işlevleri sağlar. Kelime kataloğu `words.json` içinden seed edilir. DataStore günlük hedef, seviye, ses, bildirim ve AI kullanım sayaçlarını tutar.

Önemli riskler:
- `fallbackToDestructiveMigration()` veri kaybına yol açabilir.
- `user_progress` sabit `userId = 1` ile sorgulanır; hesap/çoklu cihaz modeline uygun değildir.
- `DailySessionDao.getSessionByDate` tam timestamp eşitliği kullanırken Worker gün başlangıcı millis'i arıyor; oturum kaydı aynı günün başlangıcına normalize edilmezse eşleşme kaçabilir.
- Seed işlemi Room callback içinde bağımsız `CoroutineScope` ile başlatılıyor. İlk ekranın `SeedState` sinyaline bağlı kalması; callback, hata ve uygulama süreci yeniden yaratma durumlarında gözden geçirilmeli.
- `onCreate` ve `onOpen` seed tetikleyicileri birlikte gereksiz/yarışan iş oluşturabilir.
- Kelime sayısı sorgularında yinelenen DAO metotları mevcut.
- Şema migration dosyaları ve Room schema geçmişi görünmüyor.

## Network / Gemini

Gemini doğrudan Android istemcisinden çağrılıyor. API anahtarı `:core:network` BuildConfig alanına `local.properties` üzerinden aktarılıyor. Bu, kaynak kontrolüne girmese bile dağıtılan APK/AAB içinden çıkarılabilir; gizli anahtar koruması değildir.

Ayrıca AI kullanım sayacı sabit/boş implementasyon. BODY HTTP logging prompt ve yanıtları loglayabilir. Üretim öncesi çağrı backend'e taşınmalı, kullanıcı bazlı kota sunucuda uygulanmalı ve loglarda hassas içerik maskelenmelidir.

## Feature bulguları

### Learn
- HomeViewModel günlük/yarın kelimelerini offset ile yüklüyor ve seed sinyalini bekliyor.
- Kart kaydırma yalnızca görülen kart sayısını güncelliyor.
- LearnViewModel `onKnow` ile ilerliyor; `onDontKnow` quiz rotasına geçiyor.
- Bu akışta “biliyorum/bilmiyorum” cevaplarının SM-2 güncellemesine doğrudan bağlandığı görülmüyor.
- Kaydetme ve TTS mevcut. TTS yaşam döngüsü temizliği var.
- Ekran metinlerinin bir bölümü İngilizce, bir bölümü Türkçe; yerelleştirme standardı gerekli.

### Quiz
- Günlük kelimelerden deterministik ilk karıştırma, seçenek üretimi ve 60% geçme eşiği var.
- Cevap seçimi SM-2 use case'ini çağırıyor.
- Boş kelime listesinde loading state kapanmayabilir; toplam kelime sıfırken başarı oranı hesaplaması risklidir.
- İlerleme, cevap güncellemesi ve oturum kaydı farklı coroutine'lerde yürütülüyor; işlem sıralaması/veri tutarlılığı test edilmeli.
- Kodda debug logları var; release log politikası belirlenmeli.
- Kullanılmayan importlar/yardımcı fonksiyonlar temizlenmeli.

### Stats
- UserProgress ve son 30 günlük oturumlar izleniyor.
- Haftalık veri `sessions.takeLast(7)` ile oluşturuluyor; takvim günlerine göre boş günleri doldurma ve timezone normalize etme garantisi yok.
- `dueCount` alanı gecikmiş kelime sayısı yerine tamamlanmamış session sayısını sayıyor olabilir; metrik tanımı doğrulanmalı.

### Wordbook
- Kaydedilen kelimeler Flow ile gözleniyor; arama ve All/Learning/Learned filtreleri mevcut.
- Kaydı kaldırma mevcut.
- Sıralama, boş durum, hata görünümü ve filtrelerin testleri tamamlanmalı.

## Güvenlik ve yayın durumu

- Manifest INTERNET ve ACCESS_NETWORK_STATE izinleri içeriyor.
- Android backup açık; backup/data extraction kuralları hassas kullanıcı verisi için gözden geçirilmeli.
- Release minification kapalı (`isMinifyEnabled = false`).
- ProGuard dosyası varsayılan şablon.
- CI lint, unit test ve debug APK üretiyor; release AAB / Play dağıtımı yok.
- Firebase ve AdMob SDK'ları version catalog'da listeleniyor ancak mevcut manifest/uygulama akışında gerçek kullanım ve veri toplama beyanı ayrıca doğrulanmalı.
- Gizlilik politikası ve kullanım koşulları gerçek veri akışına göre hukuk danışmanı tarafından onaylanmalı.

## Önceliklendirilmiş teknik sonuç

**P0 — Yayın engelleyici:** Gemini anahtarını backend'e taşıma; Room migration; günlük oturum tarih bütünlüğü; AI kota/abuse koruması; release log ve backup politikası; SM-2/quiz testleri; crash-free release doğrulaması.

**P1 — Ürün bütünlüğü:** kullanıcı hesabı ve bulut senkronizasyonu; learn cevaplarının SM-2 ile bağlanması; haftalık istatistik hesaplarının düzeltilmesi; onboarding seviye testi; hata/empty state ve yerelleştirme.

**P2 — Kalite:** erişilebilirlik, UI testleri, performans, analitik olay sözlüğü, mağaza varlıkları, destek/iletişim kanalları.

## İnceleme sınırlaması

Git ağacı eksiksiz alınmıştır; ancak her UI bileşeninin ve her kaynak satırının çalışma zamanı davranışı çalıştırılarak doğrulanmamıştır. Bu raporda “TODO bulunamadı” ifadesi, tüm kod tabanında kesinlikle TODO olmadığı anlamına gelmez. Yayın öncesi yerel `rg -n "TODO|FIXME|NotImplementedError|TODO\(" .` taraması ve tam test/build çalıştırması yapılmalıdır.
