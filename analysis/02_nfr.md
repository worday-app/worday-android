# Worday — Teknik Kalite Gereksinimleri

**Sürüm:** 1.1  
**Durum:** Teknik taslak

Bu belge, Worday'in yalnızca hangi özellikleri sunduğunu değil, bu özelliklerin hangi koşullarda güvenilir çalışması gerektiğini açıklar.

## 1. Çevrimdışı çalışma

- Günlük kelimeler, quiz, SM-2, kelime defteri ve temel istatistikler internet olmadan çalışmalıdır.
- Room, öğrenme ekranlarının ana veri kaynağı olarak kalmalıdır.
- Kullanıcı işlemi önce Room'a kaydedilmeli; backend senkronizasyonu daha sonra yapılmalıdır.
- Ağ hatası, timeout, 429 veya 5xx yanıtı öğrenme akışını durdurmamalıdır.
- Senkronizasyon tekrarlandığında aynı işlem ikinci kez kaydedilmemelidir.
- Sunucu ile cihazdaki veriler çakışırsa uygulanacak kural açıkça tanımlanmalıdır.

## 2. Performans

- Kelime kataloğu ilk açılışta ana iş parçacığını bloke etmeden yüklenmelidir.
- 5.604 kelimelik katalog mümkünse transaction içinde eklenmeli; işlemin tamamlandığı güvenilir biçimde izlenmelidir.
- Arama ve tekrar sorguları gerçek cihazlarda ölçülmelidir.
- Uzun listelerde LazyColumn ve gerektiğinde sayfalama kullanılmalıdır.
- AI isteklerinde timeout, yanıt boyutu ve kullanım kotası sınırlandırılmalıdır.
- Açılış, ekran geçişi ve quiz yanıt süreleri düşük/orta seviye cihazlarda kontrol edilmelidir.

## 3. Veri güvenliği ve bütünlüğü

- Room migration sırasında mevcut kullanıcı verisi silinmemelidir.
- Şema değişiklikleri Room schema dosyalarıyla takip edilmelidir.
- Her migration için eski verinin korunduğunu doğrulayan test bulunmalıdır.
- Günlük oturum tarihleri aynı timezone ve gün başlangıcı kuralıyla saklanmalıdır.
- Quiz sonucu, SM-2 güncellemesi ve oturum tamamlanması birbiriyle tutarlı olmalıdır.
- Seed işlemi tekrar çalıştığında aynı kelimeleri çoğaltmamalıdır.
- Cihazın saat veya timezone ayarı değiştiğinde tarih hesapları test edilmelidir.

## 4. Güvenlik

- Sunucuya giden tüm trafik HTTPS kullanmalıdır.
- Gemini anahtarı, Firebase Admin kimlik bilgileri ve veritabanı parolaları Android paketinde veya Git deposunda tutulmamalıdır.
- Backend, Firebase ID token'ını kendisi doğrulamalıdır.
- Kullanıcı verisine erişirken istemciden gelen UID'ye güvenilmemelidir.
- AI endpoint'lerinde istek uzunluğu, kullanıcı kotası ve hız sınırı kontrol edilmelidir.
- Token, API anahtarı, parola ve kişisel veriler loglara yazılmamalıdır.
- Release sürümünde HTTP BODY logging kapalı olmalıdır.
- Bağımlılıklar ve güvenlik açıkları düzenli kontrol edilmelidir.

## 5. Gizlilik

- Uygulamanın gerçekten topladığı veriler listelenmelidir; yalnızca projede SDK bulunması veri toplandığını kanıtlamaz.
- Firebase, AdMob ve Gemini'nin release sürümündeki gerçek kullanımı kontrol edilmelidir.
- AI hizmetine hangi verilerin gönderildiği kullanıcıya açıklanmalıdır.
- Saklama süreleri, silme yöntemi ve varsa yurt dışına veri aktarımı belirlenmelidir.
- KVKK/GDPR metinleri gerçek uygulama davranışına göre hazırlanmalı ve hukuk danışmanına inceletilmelidir.
- Hesap özelliği eklendiğinde hesap ve ilişkili veriler için silme akışı sunulmalıdır.

## 6. Arka plan işleri

- WorkManager işleri aynı görevin gereksiz yere çoğalmasını önleyecek şekilde planlanmalıdır.
- Android'in işleri tam saatte çalıştıracağı varsayılmamalıdır.
- Günlük seri hesabı sabit 24 saat farkına göre değil, takvim gününe göre yapılmalıdır.
- Yalnızca geçici hatalar yeniden denenmelidir.
- Arka plan işi başarısız olduğunda kullanıcı temel öğrenme özelliklerini kullanabilmelidir.
- Bildirimler kullanıcı izni ve tercihlerine uymalıdır.

## 7. Hata yönetimi ve kayıtlar

- Uygulama çökmesi, ANR, veritabanı, seed, senkronizasyon ve AI hataları izlenebilir olmalıdır.
- Kullanıcıya teknik hata ayrıntıları yerine ne yapabileceğini anlatan mesaj gösterilmelidir.
- Release loglarında token, prompt veya kişisel veri bulunmamalıdır.
- Analitik olaylar kullanılacaksa adları ve neyi ölçtükleri önceden tanımlanmalıdır.

## 8. Test

- SM-2 testlerinde zaman sabitlenebilir olmalıdır.
- Domain kuralları için birim testleri yazılmalıdır.
- Room DAO ve migration testleri çalıştırılmalıdır.
- WorkManager işleri test ortamında doğrulanmalıdır.
- ViewModel coroutine akışları test edilmelidir.
- CI en azından lint, unit test ve build adımlarını çalıştırmalıdır.
- Release build ve güvenlik kontrolleri yayın sürecine eklenmelidir.

## 9. Bakım ve erişilebilirlik

- Android SDK hedefleri yayın tarihinde Google Play gerekliliklerine uygun olmalıdır.
- Release build için R8/minification etkinleştirilmeli ve gerçek cihazlarda test edilmelidir.
- API ve veritabanı şemaları sürümlenmelidir.
- Ekran metinleri string resource üzerinden yönetilmeli; Türkçe ve İngilizce desteklenmelidir.
- Ekran okuyucu etiketleri, yazı boyutu ve renk kontrastı kontrol edilmelidir.
- Feature modülleri birbirine doğrudan bağımlı olmamalıdır.

## 10. Yayın öncesi kontrol edilecek ölçütler

- Temel öğrenme akışı internetsiz tamamlanabiliyor.
- Uygulama güncellemesinde kullanıcı verisi korunuyor.
- Aynı quiz veya tekrar olayı iki kez işlenmiyor.
- Release paketinde sunucu sırrı bulunmuyor.
- Kritik domain kuralları otomatik testlerle doğrulanıyor.
- Çökme takibi ve sürüm geri alma planı hazır.
