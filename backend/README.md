# Worday Backend

Bu dosya backend için planlanan yapıyı ve ilk API taslağını açıklar. **Şu an çalışan bir backend uygulaması değildir.** Spring Boot projesi, veritabanı tabloları ve Android senkronizasyon bağlantısı henüz oluşturulmadı.

## Backend neden gerekli?

Android uygulaması şu anda Gemini API'ye doğrudan bağlanıyor. API anahtarı uygulamanın BuildConfig alanına eklendiğinde APK/AAB içinden çıkarılabilir. Bu nedenle Gemini çağrısını sunucuya taşımamız gerekiyor.

Backend ayrıca ileride kullanıcı hesabı, cihazlar arası ilerleme senkronizasyonu ve kullanıcı bazlı AI kotası sunmamızı sağlayacak.

## Teknoloji seçimi

- Kotlin
- Spring Boot
- Java 21 çalışma ortamı
- PostgreSQL
- Flyway ile veritabanı migration'ları
- Firebase Authentication ID token doğrulaması
- Gemini API için sunucu tarafı istemci
- Yerel geliştirme ve dağıtım için Docker

İlk aşamada mikroservisler yerine tek bir modüler Spring Boot uygulaması yeterli. Gereksiz servis ve operasyon yükü oluşturmadan ilerleyebiliriz.

## Genel yapı

```text
Android uygulaması
  ├── Ekranlar ve ViewModel'ler
  ├── Domain use case'leri
  ├── Room (yerel veri kaynağı)
  └── Retrofit
        |
        v
Worday API (Kotlin + Spring Boot)
  ├── Kimlik doğrulama: Firebase
  ├── Senkronizasyon: PostgreSQL
  └── AI istekleri: Gemini API
```

Room, öğrenme ekranlarının yerel veri kaynağı olmaya devam edecek. Backend eklemek, kelime öğrenmek için internet bağlantısını zorunlu hale getirmeyecek.

## İlk API taslağı

| Method | Endpoint | Açıklama |
|---|---|---|
| GET | `/api/v1/health` | Servisin ayakta olup olmadığını kontrol eder |
| GET | `/api/v1/me` | Giriş yapan kullanıcının hesap bilgilerini döndürür |
| GET | `/api/v1/sync/bootstrap` | İlk senkronizasyon için uzak veriyi getirir |
| POST | `/api/v1/sync/push` | Yerel değişiklikleri sunucuya gönderir |
| GET | `/api/v1/sync/pull?cursor=...` | Son senkronizasyondan sonraki değişiklikleri getirir |
| POST | `/api/v1/ai/example-sentences` | Gemini üzerinden örnek cümle üretir |

Kimlik doğrulaması gereken isteklerde Firebase ID token, Bearer token olarak gönderilir. Sunucu token'ı doğrular ve kullanıcı kimliğini doğrulanmış token içinden alır. İstek gövdesinde gönderilen bir `userId` değerine güvenilmez.

## İlk veri modeli

- `user_profile`: Firebase UID ve kullanıcı tercihleri.
- `word_progress`: Kullanıcının kelime bazında tekrar ve öğrenme durumu.
- `review_event`: Her tekrar işlemi için benzersiz olay kaydı.
- `daily_session`: Günlük çalışma oturumları.
- `sync_change`: Artımlı senkronizasyon için sıra numarası veya cursor.

Kelime kataloğu ilk aşamada Android uygulamasında kalacak. Sunucuya yalnızca kullanıcıya ait ilerleme ve tercih verileri aktarılacak.

## Senkronizasyon nasıl çalışacak?

1. Kullanıcı bir işlem yaptığında önce Room güncellenir.
2. Aynı yerel işlem içinde benzersiz kimlikli bir senkronizasyon olayı kuyruğa eklenir.
3. WorkManager bekleyen olayları sunucuya gönderir.
4. Sunucu aynı olayın ikinci kez kaydedilmesini benzersiz olay kimliğiyle engeller.
5. Android, sunucudaki yeni değişiklikleri cursor üzerinden alır ve Room'a yazar.
6. Giriş süresi dolmuşsa senkronizasyon durur; yerel öğrenme devam eder.

Çakışma çözümü, veri modeli ve revision kuralları uygulamaya başlamadan önce netleştirilecek.

## Gemini güvenliği

- Gemini API anahtarı yalnızca backend ortamında tutulur.
- İstek uzunluğu ve üretilecek cümle sayısı sınırlandırılır.
- Kullanıcı ve IP bazında hız/kota kontrolü uygulanır.
- Token, API anahtarı ve prompt içerikleri loglara yazılmaz.
- Sağlayıcı hataları Android'e anlaşılır hata türleriyle iletilir.
- AI çıktısı güvenilir kod veya HTML olarak değerlendirilmez; metin olarak gösterilir.

## Geliştirme sırası

### Aşama 1 — Backend temeli
Kotlin Spring Boot projesi, health endpoint, PostgreSQL, Docker Compose, Flyway ve temel testler.

### Aşama 2 — Kimlik doğrulama
Firebase ID token doğrulaması, `/me` endpoint'i ve kullanıcılar arası veri erişim testleri.

### Aşama 3 — Gemini bağlantısı
Sunucu tarafı Gemini istemcisi, kota, hız sınırı, timeout ve hata yönetimi. Ardından Android AI repository'si backend'e geçirilir.

### Aşama 4 — Senkronizasyon
Veri tabloları, bootstrap, push/pull, idempotency ve çakışma kuralları.

### Aşama 5 — Android entegrasyonu
Token sağlayıcısı, API modelleri, Room outbox ve WorkManager senkronizasyonu.

### Aşama 6 — Yayına hazırlık
Log/izleme, yedekleme, güvenlik testleri, veri silme akışı ve dağıtım prosedürü.

## İlk sürümde yapmayacaklarımız

- Room'u kaldırıp tüm ekranları sunucudan veri okuyacak hale getirmek.
- Kelime öğrenmeyi veya quiz'i internet bağlantısına bağlamak.
- SM-2 hesaplamasını ağ isteğine bağımlı hale getirmek.
- Sabit kelime kataloğunu her kurulumda sunucuya yüklemek.
- İlk sürümde mikroservis mimarisi kurmak.

## Mevcut durum

Bu aşamada yalnızca mimari ve API taslağı hazırlanmıştır. Çalışan Spring Boot kodu, PostgreSQL migration'ı, Docker Compose yapılandırması, backend testleri ve Android senkronizasyonu henüz yoktur.
