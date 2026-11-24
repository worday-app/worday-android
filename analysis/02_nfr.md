# Worday — Fonksiyonel Olmayan Gereksinimler (NFR)

**Sürüm:** 1.0 · **Durum:** Teknik taslak

## 1. Çalışma ilkesi: offline-first

1. Room, öğrenme ekranlarının yerel okuma kaynağı (source of truth) olmalıdır.
2. Günlük kelimeler, quiz, SM-2 hesaplaması, kelime defteri ve istatistikler ağ erişimine bağımlı olmamalıdır.
3. Ağ çağrıları UI thread'i bloke etmemeli; coroutine tabanlı olmalıdır.
4. Backend senkronizasyonu eklendiğinde yerel değişiklik önce Room'a yazılmalı, sonra WorkManager kuyruğuna alınmalıdır.
5. Ağ yokluğu, timeout, 429 ve 5xx durumlarında yerel öğrenme kesintiye uğramamalıdır.
6. Senkronizasyon tekrarları idempotent olmalı; aynı olay tekrar gönderildiğinde çift kayıt üretmemelidir.
7. Çakışma çözümü kullanıcı kimliği, olay kimliği ve sunucu revizyonu üzerinden tanımlanmalıdır.

## 2. Performans ve kaynak kullanımı

- İlk açılışta seed işlemi ana iş parçacığında yapılmamalıdır.
- 5.604 kelimelik katalog toplu/transactional olarak eklenmeli ve seed tamamlanma durumu güvenilir şekilde izlenmelidir.
- DAO sorguları indeks ve gerçek cihaz profiliyle değerlendirilmelidir.
- Büyük listeler LazyColumn ve sayfalama/filtreleme ile sunulmalıdır.
- AI yanıtları timeout, boyut sınırı ve kota ile korunmalıdır.
- Uygulama açılış, ekran geçişi ve quiz cevaplama süreleri düşük/orta seviye Android cihazlarda ölçülmelidir.

## 3. Veri bütünlüğü ve kalıcılık

- Room şema sürümleri artırılırken destructive migration kullanılmamalıdır.
- Migration testleri eski şemadan yeni şemaya veri kaybı olmadan geçişi doğrulamalıdır.
- Günlük oturum tarihi tek bir timezone ve gün başlangıcı standardıyla normalize edilmelidir.
- Quiz cevapları, SM-2 güncellemesi ve session tamamlanması tutarlı bir işlem sınırı içinde tasarlanmalıdır.
- Seed işlemi tekrar çalıştırıldığında aynı kelimeyi çoğaltmamalı; katalog sürümü kontrol edilmelidir.
- Tarih karşılaştırmaları cihaz timezone değişikliğine karşı test edilmelidir.
- Yedekleme kapsamı, kullanıcı ilerlemesi ve kimlik bilgileri açısından açıkça tanımlanmalıdır.

## 4. Güvenlik

- Tüm uzak trafik TLS/HTTPS üzerinden yapılmalıdır.
- Gemini, Firebase Admin ve diğer sunucu sırları yalnızca secret manager / deployment environment içinde tutulmalıdır.
- Android APK/AAB içine API secret, service account private key veya signing key gömülmemelidir.
- Backend kimlik doğrulaması Firebase ID token doğrulaması veya eşdeğer güvenilir bir mekanizmayla yapılmalıdır.
- Yetkilendirme her sorguda sunucu tarafından uygulanmalı; istemciden gelen userId güvenilir kabul edilmemelidir.
- AI uç noktalarında kullanıcı başına kota, rate limit, input uzunluk sınırı ve kötüye kullanım koruması olmalıdır.
- Loglarda token, API key, parola, hassas prompt veya kişisel veri tutulmamalıdır.
- Release ortamında HTTP BODY logging kapalı olmalıdır.
- Yedekleme ve cihaz kaybı senaryoları için veri saklama/şifreleme kararı verilmelidir.
- Güvenlik açıkları ve bağımlılık güncellemeleri düzenli taranmalıdır.

## 5. Gizlilik ve veri koruma

- Veri envanteri çıkarılmalı: hesap tanımlayıcıları, kullanıcı tercihleri, öğrenme ilerlemesi, cihaz/diagnostic verileri ve reklam tanımlayıcıları yalnızca gerçekten toplanıyorsa listelenmelidir.
- Firebase Analytics/Crashlytics ve AdMob SDK'larının etkinliği, olayları, saklama ve paylaşım koşulları gerçek build üzerinden doğrulanmalıdır.
- Gemini'ye gönderilen kelime/prompt verisi, aktarım amacı ve sağlayıcı şartları açıklanmalıdır.
- Gereksiz veri toplanmamalı; saklama süreleri ve silme süreci belirlenmelidir.
- KVKK/GDPR kapsamındaki aydınlatma, hukuki dayanak, kullanıcı hakları, çocuk kullanıcılar, uluslararası aktarım ve veri sorumlusu/işleyen rolleri hukuk incelemesinden geçirilmelidir.
- Kullanıcı hesabı eklendiğinde hesap silme ve ilişkili verileri silme akışı sunulmalıdır.

## 6. Arka plan işleri

- WorkManager işleri unique work adıyla idempotent planlanmalıdır.
- Periyodik işler tam saat garantisi vermez; iş mantığı çalıştığı andaki takvim gününe göre hesaplanmalıdır.
- Seri sıfırlama, “dün” kontrolünü sabit 24 saat çıkararak değil, yerel takvim günü üzerinden yapmalıdır.
- Retry yalnızca geçici hatalarda uygulanmalı; kalıcı veri/şema hataları loglanıp görünür hale getirilmelidir.
- Arka plan işi başarısızlığı öğrenme akışını engellememelidir.
- Bildirimler kullanıcı iznine ve tercihlerine saygı göstermelidir.

## 7. Gözlemlenebilirlik ve hata yönetimi

- Crash, ANR, ağ başarısızlığı, seed hatası, sync başarısızlığı ve AI kota aşımı ölçülebilir olmalıdır.
- Kullanıcıya teknik stack trace değil, eyleme dönük mesaj sunulmalıdır.
- Release logları PII ve secret içermemelidir.
- Kritik akışlar için yapılandırılmış olay adları ve sürüm bilgisi tanımlanmalıdır.
- Telemetri devre dışı bırakma/izin tercihleri ürün ve mevzuata göre uygulanmalıdır.

## 8. Test edilebilirlik

- SM-2 saf fonksiyon olmalı; zaman kaynağı enjekte edilmelidir.
- Domain testleri JUnit 5 ile; repository ve use case etkileşimleri gerektiğinde MockK ile test edilmelidir.
- Room DAO ve migration testleri in-memory database ile yapılmalıdır.
- WorkManager testleri TestDriver/TestListenableWorkerBuilder ile yapılmalıdır.
- ViewModel akışları coroutine test dispatcher ve Turbine ile doğrulanmalıdır.
- CI lint, unit test, migration test, release build ve dependency/security kontrolünü çalıştırmalıdır.

## 9. Uyumluluk ve bakım

- Min/target SDK, Play target API gereklilikleri yayın tarihinde doğrulanmalıdır.
- Release R8/minification etkinleştirilmeli; mapping dosyası güvenli saklanmalıdır.
- Room schema ve API sözleşmeleri sürümlenmelidir.
- Ekran metinleri string resource üzerinden yönetilmeli; Türkçe ve İngilizce yerelleştirme sağlanmalıdır.
- Erişilebilirlik etiketleri, dinamik yazı boyutu ve kontrast test edilmelidir.
- Her yeni modül bağımlılık yönünü korumalı; feature modülleri birbirine doğrudan bağlanmamalıdır.

## 10. Ölçülebilir kabul hedefleri

- Offline temel akış: ağ bağlantısı olmadan başarılı.
- Veri kaybı: normal upgrade/migration senaryolarında sıfır.
- Duplicate quiz/review event: idempotency ile sıfır.
- Release secret sızıntısı: sıfır.
- Kritik domain iş kuralları: otomatik test kapsamı zorunlu; hedef oran ekipçe belirlenmeli.
- Üretim hataları: crash/ANR izleme ve sürüm geri alma planı mevcut.
