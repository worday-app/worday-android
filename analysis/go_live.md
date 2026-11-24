# Worday — Google Play Go-Live Rehberi

**Durum:** Yayın öncesi kontrol listesi. Bu belge yayın onayı değildir.

## 1. Secret ve yapılandırma yönetimi

### Android istemcisi
- Gemini API secret'ını `:core:network` BuildConfig alanından kaldırın. `local.properties` veya GitHub Secret içine koymak dağıtılan uygulamada secret'ı gizlemez.
- Gemini çağrısını backend üzerinden yapın; Android yalnızca HTTPS backend endpoint'ini ve Firebase kullanıcı token'ını kullanmalıdır.
- Firebase Android `google-services.json` yapılandırması istemciye özel Firebase config içerir; service account private key değildir. Dosya yanlışlıkla commit edilmemeli; Firebase Security Rules, App Check ve API restriction ayrıca yapılandırılmalıdır.
- AdMob App ID/Ad Unit ID mobil istemci yapılandırmasıdır; bunlar private secret sayılmaz. Yayın kimlikleri test kimliklerinden ayrılmalı; reklam SDK izin/consent akışı tamamlanmalıdır.
- Signing keystore, keystore password ve key alias şifreleri kesinlikle repoya konmamalıdır.

### Backend
- `GEMINI_API_KEY`, Firebase Admin service account veya Workload Identity, DB credentials ve token signing secrets secret manager üzerinden sağlanmalıdır.
- Prod/staging secret'ları ayrılmalıdır.
- Anahtar rotasyonu, erişim politikası ve audit log tanımlanmalıdır.
- Loglarda Authorization header, API key, kullanıcı prompt'u veya kişisel veri maskelenmelidir.

### GitHub Actions Secrets
Önerilen isimler:
- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`
- `PLAY_SERVICE_ACCOUNT_JSON` (Play Console API erişimi için sınırlı servis hesabı)
- Backend deploy secrets (yalnız backend workflow environment'ında)

Secret'ları PR loglarına yazdırmayın. Fork PR'larında secrets erişimi kapalı kalmalıdır.

## 2. Release AAB ve R8

Mevcut `app/build.gradle.kts` içinde `release.isMinifyEnabled = false`; bu nedenle release obfuscation etkin değildir.

1. Release signing config'i environment/GitHub Secrets'tan yükleyin.
2. Release build type içinde `isMinifyEnabled = true` ve `isShrinkResources = true` ayarlayın.
3. `proguard-android-optimize.txt` ve proje kuralları kullanın. Rastgele geniş `-keep` kuralları eklemeyin; yalnız reflection/serialization kullanan modeller için gereken kuralları test ederek ekleyin.
4. Room, Gson DTO, Hilt ve Firebase kullanan release build'i gerçek cihazda test edin.
5. Mapping dosyasını build artifact'ından ayrı ve erişimi kısıtlı saklayın.
6. VersionCode artırın; versionName ürün sürümüyle eşleşsin.
7. Komut:
   ```bash
   ./gradlew clean test lint bundleRelease
   ```
8. AAB'yi `app/build/outputs/bundle/release/app-release.aab` konumundan alın.
9. Play Console Internal testing'e yükleyin; Play App Signing'i etkinleştirin ve upload key'i güvenli saklayın.

## 3. GitHub Actions dağıtımı

Mevcut CI lint, unit test ve debug APK üretir. Release AAB/Play dağıtımı bulunmuyor.

Önerilen akış:
1. PR: lint + unit tests + debug build.
2. `v*.*.*` tag veya manuel workflow_dispatch: release build.
3. Signing secrets ile imzalı AAB oluştur.
4. AAB ve mapping dosyasını artifact olarak yükle.
5. Google Play Android Publisher API ve sınırlı servis hesabıyla Internal track'e gönder.
6. Production track dağıtımını ayrı manuel approval ile yap.
7. Release notes ve sürüm numarası kontrolü ekle.

Play dağıtımı için Fastlane supply veya resmi Google Play Developer API istemcisi kullanılabilir. Servis hesabına yalnız ilgili uygulama ve gerekli track izinleri verilmelidir.

## 4. Play Console yayın öncesi

- Uygulama adı, kısa/uzun açıklama, ikon, feature graphic ve telefon ekran görüntüleri.
- Privacy Policy herkese açık HTTPS URL.
- Data safety formu: Firebase Analytics/Crashlytics, AdMob, Gemini/backend ve gerçek veri akışına göre doldurulmalı.
- Ads declaration, target audience, content rating ve app access formu.
- Hesap oluşturulabiliyorsa hesap silme talebi uygulama içinden ve web üzerinden erişilebilir olmalı.
- KVKK/GDPR, çocuk kullanıcı ve uluslararası veri aktarımı incelemesi.
- Uygulama içi reklam/consent ve Google Play Families politikaları (hedef kitleye göre).
- Crash/ANR, offline, düşük ağ, timezone/DST ve migration senaryoları.
- Support e-posta ve geliştirici iletişim bilgileri.

## 5. Release engelleyici mevcut bulgular

- Gemini anahtarı istemci BuildConfig'te.
- BODY HTTP logging açık.
- Room destructive migration kullanıyor.
- Release minification kapalı.
- Test kataloğunda JUnit4 tanımlı; JUnit5 entegrasyonu doğrulanmalı.
- CI release AAB/Play deployment yapmıyor.
- Manifest backup açık; veri kapsamı gözden geçirilmeli.

## 6. Yayın kararı

Aşağıdaki maddeler tamamlanmadan Production track'e çıkılmamalıdır: güvenlik incelemesi, gerçek veri envanteri ve Data safety beyanı, Room migration, SM-2/quiz testleri, release signing, internal testing ve crash/ANR kabul kriterleri. Hukuki metinler yayın öncesinde yetkin hukuk danışmanı tarafından gözden geçirilmelidir.
