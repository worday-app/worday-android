# Worday — Google Play Yayın Kontrol Listesi

**Durum:** Hazırlık dokümanı. Bu listedeki maddelerin yazılı olması, yayın için tamamlandıkları anlamına gelmez.

## 1. API anahtarları ve imzalama bilgileri

### Android uygulaması
- Gemini API anahtarını Android BuildConfig'ten kaldırın. Anahtar `local.properties` üzerinden gelse bile APK/AAB içine girerse çıkarılabilir.
- Gemini isteklerini backend üzerinden gönderin.
- Firebase istemci yapılandırmasını service account anahtarıyla karıştırmayın. Service account private key mobil uygulamaya konulmamalıdır.
- Firebase Security Rules, App Check ve API kısıtlamalarını ayrıca kontrol edin.
- AdMob kullanılıyorsa test ve gerçek reklam kimliklerini ayırın; gerekli consent akışını tamamlayın.
- Keystore ve imzalama parolalarını Git deposuna koymayın.

### Backend
- Gemini anahtarı, Firebase Admin kimlik bilgileri ve veritabanı parolaları sunucu secret manager veya güvenli deployment değişkenlerinde tutulmalıdır.
- Test, staging ve production ortamlarının bilgileri ayrılmalıdır.
- Anahtar erişimi ve yenileme süreci belirlenmelidir.
- Authorization header, token, prompt ve kişisel veriler loglardan çıkarılmalıdır.

### GitHub Actions
Kullanılabilecek secret adları:
- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`
- `PLAY_SERVICE_ACCOUNT_JSON`

Backend dağıtım secret'ları yalnızca ilgili backend workflow ortamına tanımlanmalıdır. PR loglarında secret değerleri yazdırılmamalıdır.

## 2. Release paketi

Mevcut incelemede release minification kapalı görünüyordu. Açmadan önce gerçek cihazda test edilmelidir.

1. Release signing bilgilerini güvenli ortam değişkenlerinden yükleyin.
2. R8/minification ve resource shrinking ayarlarını etkinleştirin.
3. Gerekli ProGuard kurallarını gerçek build hatalarına göre ekleyin; geniş ve gereksiz keep kuralları kullanmayın.
4. Room, Hilt, serialization ve Firebase kullanan release paketini cihazda test edin.
5. Mapping dosyasını AAB'den ayrı ve erişimi sınırlı şekilde saklayın.
6. VersionCode ve versionName değerlerini kontrol edin.
7. Yerel doğrulama komutu:

```bash
./gradlew clean test lint bundleRelease
```

8. Oluşan AAB'yi Play Console Internal testing kanalına yükleyin.

## 3. CI/CD

Mevcut CI'ın lint, unit test ve debug APK adımlarını çalıştırdığı görülüyor. Release AAB üretimi ve Play dağıtımı ayrıca hazırlanmalı.

Önerilen akış:
- Pull request: lint, unit test ve debug build.
- Sürüm etiketi veya manuel tetikleme: release build.
- İmzalı AAB ve mapping dosyasını artifact olarak saklama.
- Önce Internal testing kanalına dağıtım.
- Production dağıtımı için ayrı manuel onay.
- Sürüm notları ve geri alma adımlarını kontrol etme.

## 4. Play Console kontrolü

- Uygulama adı, kısa ve uzun açıklama.
- İkon, feature graphic ve ekran görüntüleri.
- Herkese açık HTTPS gizlilik politikası adresi.
- Gerçek veri akışına göre Data safety formu.
- Reklam, hedef kitle, içerik derecelendirmesi ve uygulama erişim formları.
- Hesap oluşturma varsa hesap silme talebi için uygulama içi ve web yolu.
- KVKK/GDPR ve uluslararası veri aktarımı incelemesi.
- Destek e-postası ve geliştirici iletişim bilgileri.
- Farklı ağ koşulları, uçak modu, timezone değişimi ve migration testleri.

## 5. Mevcut yayın engelleri

İnceleme sırasında aşağıdaki konular yayın öncesi çözülmesi gereken işler olarak belirlendi:

- Gemini anahtarı Android BuildConfig içinde.
- HTTP BODY logging etkin.
- Room destructive migration kullanıyor.
- Release minification kapalı.
- JUnit5 geçişi ve test yapılandırması doğrulanmalı.
- CI release AAB üretip Play'e dağıtmıyor.
- Android backup ayarları kullanıcı verisi açısından gözden geçirilmeli.

Bu maddelerin güncel branch'te tekrar kontrol edilmesi gerekir; kod değiştikçe durumları değişebilir.

## 6. Yayın kararı

Production yayınına geçmeden önce güvenlik, veri migration'ı, kritik testler, imzalama, gizlilik beyanları ve Internal testing tamamlanmalıdır. Gizlilik politikası ve kullanım koşulları gerçek hizmet sağlayıcı bilgileriyle doldurulmalı ve hukuk danışmanına inceletilmelidir.
