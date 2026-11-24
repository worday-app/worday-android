# Worday — Ürün ve Teknik Backlog

**Format:** User Story · **Öncelik:** P0 yayın engelleyici, P1 yüksek, P2 iyileştirme  
**Not:** Maddeler statik kaynak incelemesinden türetilmiştir; her iş PR ve test kanıtıyla kapatılmalıdır.

## Epic A — Domain ve öğrenme motoru

### US-01 — SM-2 sınır durumları [P0]
Bir kullanıcı olarak, yanlış cevaplar ve uzun aralar sonrasında tekrar planımın tutarlı olmasını istiyorum; böylece öğrenme programım bozulmaz.
- Kabul: kalite 0..5 dışı reddedilir; yanlış cevap tekrar sayısını sıfırlar; EF 1.3 altına inmez; 1/6 gün aralıkları doğrulanır; 100 gün gecikmiş kelime due olarak gelir.
- Test: JUnit5; zaman kaynağı sabitlenmiş; MockK repository doğrulaması.

### US-02 — Öğrenme ekranı cevabının kaydı [P0]
Bir kullanıcı olarak, “Biliyorum/Bilmiyorum” cevabımın öğrenme motoruna işlenmesini istiyorum.
- Kabul: her kart yanıtı bir kez kaydedilir; SM-2 güncellenir; ilerleme uygulama kapanıp açılsa da korunur.

### US-03 — Quiz tamamlanma bütünlüğü [P0]
Bir kullanıcı olarak, quiz sonucumun ve günlük ilerlememin tek bir tutarlı kayıt olmasını istiyorum.
- Kabul: boş soru seti yönetilir; cevaplar çift işlenmez; quiz sonucu/session/seri güncellemesi atomik veya telafi edilebilir olur.

### US-04 — Onboarding seviye testi [P1]
Bir kullanıcı olarak, doğru cevaplarıma göre uygun CEFR seviyesine yerleştirilmek istiyorum.
- Kabul: her sorunun seçenekleri ve doğru cevap kimliği modelde tutulur; UI doğru index üretir; 0 soru bölme hatası yoktur; seviye eşikleri ürün onaylıdır.

## Epic B — Yerel veri ve offline-first

### US-05 — Güvenli Room migration [P0]
Bir kullanıcı olarak, uygulama güncellendiğinde ilerlememi kaybetmek istemiyorum.
- Kabul: destructive migration kaldırılır; schema export edilir; v1→v2 migration testi geçer.

### US-06 — Güvenilir seed [P0]
Bir kullanıcı olarak, ilk açılışta kelime kataloğunun eksiksiz ve bir kez yüklenmesini istiyorum.
- Kabul: seed transaction içinde; hata state'i gözlenebilir; duplicate yok; UI seed completion için deadlock yaşamaz.

### US-07 — Günlük oturum tarih standardı [P0]
Bir kullanıcı olarak, seri ve günlük istatistiklerimin yerel takvim günüyle eşleşmesini istiyorum.
- Kabul: tüm session tarihleri normalize; timezone/DST ve gün değişimi testleri var.

### US-08 — Room sorgu ve indeks optimizasyonu [P1]
Bir kullanıcı olarak, kelime arama ve tekrar listelerinin büyük katalogda hızlı açılmasını istiyorum.
- Kabul: sorgu planları incelenir; uygun indeksler eklenir; yinelenen DAO fonksiyonları kaldırılır.

### US-09 — Offline senkronizasyon kuyruğu [P1]
Bir kullanıcı olarak, internet yokken yaptığım ilerlemenin bağlantı gelince kaybolmadan aktarılmasını istiyorum.
- Kabul: yerel event UUID; WorkManager retry; idempotent push; conflict policy; kullanıcı bazlı scope.

## Epic C — Backend ve AI güvenliği

### US-10 — Backend temel servis [P0]
Bir kullanıcı olarak, hesabımın verilerinin güvenli bir sunucu alanında saklanmasını istiyorum.
- Kabul: health endpoint, environment config, DB migration, request validation ve integration tests.

### US-11 — Kimlik doğrulama ve yetkilendirme [P0]
Bir kullanıcı olarak, yalnızca kendi ilerleme verime erişilmesini istiyorum.
- Kabul: Firebase ID token sunucuda doğrulanır; UID token'dan çıkarılır; istemci UID'sine güvenilmez; cross-user testleri geçer.

### US-12 — Gemini proxy ve kota [P0]
Bir kullanıcı olarak, AI örnek cümlelerini güvenli ve adil kota ile kullanmak istiyorum.
- Kabul: Gemini anahtarı yalnız backend'de; server-side quota/rate limit; prompt uzunluk kontrolü; log redaction.

### US-13 — AI kullanım DataStore geçişi [P1]
Bir kullanıcı olarak, ücretsiz AI kullanım hakkımı doğru görmek istiyorum.
- Kabul: sabit 2 ve boş consumeRequest kaldırılır; kota kaynağı tek otorite olur; offline cache davranışı tanımlanır.

## Epic D — Feature kalitesi

### US-14 — İstatistik takvim doğruluğu [P1]
Bir kullanıcı olarak, son yedi takvim gününün eksiksiz grafiğini görmek istiyorum.
- Kabul: boş günler sıfır; timezone normalize; tamamlanmamış session ve due kelime metrikleri ayrıştırılır.

### US-15 — Kelime defteri filtreleri [P2]
Bir kullanıcı olarak, kaydettiğim kelimeleri seviyeye ve kelime türüne göre bulmak istiyorum.
- Kabul: filtre kombinasyonları, boş durum ve arama testleri var.

### US-16 — Hata/empty state standardı [P1]
Bir kullanıcı olarak, veri yokken veya ağ/DB hatasında ne yapacağımı anlamak istiyorum.
- Kabul: her ekran loading/success/empty/error durumlarını gösterir; retry mümkün.

### US-17 — Türkçe/İngilizce yerelleştirme [P1]
Bir kullanıcı olarak, tüm arayüzü seçtiğim dilde görmek istiyorum.
- Kabul: hardcoded metinler string resource'a taşınır; TR/EN kaynakları tamamlanır.

## Epic E — Yayın ve güvence

### US-18 — Test ve CI standardı [P0]
Bir geliştirici olarak, her değişikliğin otomatik doğrulanmasını istiyorum.
- Kabul: JUnit5, MockK, coroutine test, Room migration, UI test; CI lint/test/build raporları.

### US-19 — Release güvenliği [P0]
Bir kullanıcı olarak, yayın paketinde gizli anahtar ve hassas debug verisi bulunmamasını istiyorum.
- Kabul: release R8 etkin; BODY logging kapalı; secret scanning; backup politikası onaylı.

### US-20 — Google Play dağıtımı [P0]
Bir yayın sorumlusu olarak, imzalı AAB'yi tekrarlanabilir şekilde üretip test kanalına göndermek istiyorum.
- Kabul: signing secrets GitHub Secrets; release workflow; Play internal testing; rollback/track prosedürü.

### US-21 — Gizlilik ve yasal metinler [P0]
Bir kullanıcı olarak, verimin nasıl işlendiğini ve haklarımı açıkça öğrenmek istiyorum.
- Kabul: gerçek SDK veri akışı envanteri; KVKK/GDPR incelemesi; yayınlanabilir privacy URL; hesap silme ve veri saklama açıklaması.

### US-22 — Mağaza listelemesi ve görseller [P1]
Bir kullanıcı olarak, uygulamanın faydasını mağazada doğru anlamak istiyorum.
- Kabul: TR/EN başlık/açıklama limitleri; ekran görüntüleri; içerik derecelendirme; Data safety beyanı.

## Definition of Done
- Kod review tamam.
- Otomatik testler geçiyor.
- Yeni iş kuralı dokümante edildi.
- Hata ve loading durumları ele alındı.
- Güvenlik/gizlilik etkisi değerlendirildi.
- İlgili doküman güncellendi.
