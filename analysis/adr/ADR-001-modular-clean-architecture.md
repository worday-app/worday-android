# ADR-001: Uygulamanın modüllere ayrılması

- **Durum:** Kabul edildi
- **Tarih:** 24.11.2026

## Neden bu kararı aldık?

Worday; kelime öğrenme kuralları, yerel veritabanı, ağ bağlantıları ve farklı uygulama ekranlarından oluşuyor. Bunların tek modülde tutulması, proje büyüdükçe değişiklik yapmayı ve test yazmayı zorlaştırır.

## Karar

Projeyi `:app`, `:core` ve `:feature` modüllerine ayırıyoruz.

- `:core:domain`: İş modelleri, repository arayüzleri ve use case'ler.
- `:core:data`: Room ve yerel veri işlemleri.
- `:core:network`: API bağlantıları ve ağ modelleri.
- `:feature:*`: İlgili ekranlar ve ViewModel'ler.
- `:app`: Uygulamanın başlangıç noktası ve modüllerin bir araya geldiği katman.

## Bu kararın sonucu

İş kuralları ekranlardan ve veri kaynağından bağımsız kalır. Böylece domain kodunu ayrı test edebilir, veri kaynağını değiştirdiğimizde ekranları mümkün olduğunca koruyabiliriz.

Bunun karşılığında Gradle ayarları ve modüller arası bağımlılıklar daha dikkatli yönetilmelidir.

## Uyulacak kurallar

- Domain katmanı Android, Room veya Retrofit sınıflarını doğrudan kullanmaz.
- Feature modülleri birbirine doğrudan bağımlı olmaz.
- Veri ve ağ katmanları domain arayüzlerini uygular.
- Modüller uygulama başlangıcında `:app` içinde birleştirilir.
