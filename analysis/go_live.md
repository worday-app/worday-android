# Worday — Yayın Hazırlığı

Bu belge yayın öncesi kontrol listesidir. Bir maddenin burada yazması, tamamlandığı anlamına gelmez. Her başlık gerçek release build ve test sonuçlarıyla kapatılmalı.

**Mevcut karar: NO-GO.** Temel öğrenme akışı, veri migration'ı, Gemini anahtarının korunması, içerik lisansı ve release doğrulaması tamamlanmadan production yayını yapılmamalı.

## 1. Önce çözülmesi gerekenler

### Kullanıcı verisi
- Room'daki destructive migration kaldırılmalı ve veri koruyan migration yazılmalı.
- Room schema dosyaları sürüm kontrolünde tutulmalı.
- Kelime ID'leri katalog sırasından bağımsız olmalı.
- Seed sürümü ve tekrar çalıştırma davranışı belirlenmeli.
- Günlük oturum, streak ve istatistik tarihleri aynı timezone/gün standardını kullanmalı.

### Öğrenme akışı
- Onboarding seviye ve günlük hedefi doğru kaydetmeli.
- Öğrenme kartı cevapları ilerlemeye yansımalı.
- Quiz, SM-2 ve günlük oturum kaydı birbiriyle tutarlı olmalı.
- Quiz yarıda bırakıldığında yanlış tamamlanma kaydı oluşmamalı.
- Due kelimeler tekrar listesinde görünmeli.
- Streak ve haftalık istatistikler takvim günleri üzerinden doğrulanmalı.

### Gemini ve ağ güvenliği
- Gemini anahtarı Android BuildConfig'ten kaldırılmalı. `local.properties` üzerinden gelmesi, anahtarın APK/AAB'den çıkarılmasını engellemez.
- Gemini çağrısı sunucu tarafına taşınmalı.
- Sunucu anahtarı güvenli ortam değişkeni veya secret manager'da tutulmalı.
- İstek uzunluğu, timeout, kota ve rate limit uygulanmalı.
- Authorization header, token, prompt ve kişisel veri loglanmamalı.
- HTTP BODY logging release build'de kapalı olmalı.

### Kelime içeriği
- Kelime listesi, tanım, örnek cümle ve IPA kaynakları belirlenmeli.
- Ticari kullanım ve yeniden dağıtım hakları doğrulanmalı.
- Lisansı belirsiz içerik yayın paketine alınmamalı.
- Bozuk tanım, atıf artığı, eksik IPA ve hatalı örnekler taranmalı.

## 2. Android release build

Mevcut statik incelemede release minification kapalı ve hedef SDK 36 görünüyor. Yayın günü Google Play'in güncel target API şartı tekrar kontrol edilmeli.

Kontrol sırası:

1. Release signing bilgilerini güvenli ortam değişkenlerinden al.
2. Keystore ve parolaları Git deposuna ekleme.
3. R8/minification ayarlarını açmadan önce release build'i mevcut haliyle al.
4. Minification'ı etkinleştirip Room, Hilt, serialization ve Firebase kullanan akışları gerçek cihazda test et.
5. Yalnızca gerçek build hatalarının gerektirdiği ProGuard kurallarını ekle.
6. Mapping dosyasını AAB'den ayrı ve erişimi sınırlı sakla.
7. VersionCode ve versionName değerlerini kontrol et.
8. İmzalı AAB'yi Play Console Internal testing kanalına yükle.

Yerel kontrol için:

```bash
./gradlew clean test lint bundleRelease
```

Bu komutun başarılı olması tek başına yayın onayı değildir. Instrumentation testleri ve cihaz üzerinde kritik kullanıcı akışları da çalıştırılmalı.

## 3. CI/CD

Önerilen akış:

- Pull request: lint, unit test ve debug build.
- Sürüm adayı: release build ve instrumentation testleri.
- Release: imzalı AAB üretimi ve artifact saklama.
- İlk dağıtım: Internal testing.
- Production: manuel onay sonrası.

Workflow secret örnekleri:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`
- `PLAY_SERVICE_ACCOUNT_JSON`

Secret değerleri loglara yazdırılmamalı. Backend secret'ları Android build ortamına eklenmemeli.

## 4. Cihaz ve veri testleri

Internal testing öncesinde en az şu senaryolar denenmeli:

- Temiz kurulum ve ilk seed.
- Uygulama güncellemesi sonrası Room verisinin korunması.
- İnternet yokken kelime çalışma, quiz ve Wordbook.
- Aynı sorunun iki kez kaydedilmemesi.
- Quiz'i yarıda bırakıp yeniden açma.
- Gün değişimi, timezone değişimi ve cihaz saatinin ileri/geri alınması.
- WorkManager'ın planlanması ve başarısız işin yeniden denenmesi.
- Gemini kotası dolduğunda veya servis hata verdiğinde ana öğrenme akışının çalışmaya devam etmesi.
- Küçük ekran, büyük yazı boyutu ve ekran okuyucu.
- Release build'de log, crash ve performans kontrolü.

## 5. Play Console ve gizlilik

- Uygulama adı, açıklamalar, ikon ve ekran görüntüleri.
- Herkese açık HTTPS gizlilik politikası adresi.
- Release sürümündeki gerçek veri akışına göre Data safety formu.
- Reklam, hedef kitle ve içerik derecelendirmesi.
- Hesap oluşturma varsa uygulama içi ve web üzerinden hesap silme talebi.
- Veri saklama, silme ve varsa yurt dışına aktarım açıklaması.
- Destek e-postası ve geliştirici iletişim bilgileri.

Firebase veya AdMob bağımlılığının projede bulunması tek başına veri toplandığını kanıtlamaz. Play Console beyanları, release paketinde fiilen etkin olan SDK'lar ve gönderilen veriler incelenerek hazırlanmalı. UMP kullanmak tek başına KVKK/GDPR uyumluluğu sağlamaz. Hukuki metinler gerçek şirket/sağlayıcı bilgileriyle tamamlanmalı ve hukuk danışmanına inceletilmeli.

## 6. Yayın kararı

Production'a geçmeden önce şu koşulların hepsi sağlanmalı:

- Kullanıcı verisi migration sırasında korunuyor.
- Kelime kataloğunun ticari kullanım hakkı net.
- Onboarding, öğrenme, quiz, SM-2 ve streak akışı test edilmiş.
- Kritik unit ve instrumentation testleri geçiyor.
- Gemini anahtarı mobil pakette bulunmuyor.
- Release AAB imzalı ve cihazda test edilmiş.
- Gizlilik ve Play Console beyanları gerçek veri akışıyla uyumlu.
- Internal testing geri bildirimleri değerlendirilmiş.

Bu koşullardan biri eksikse production yayını ertelenmeli.
