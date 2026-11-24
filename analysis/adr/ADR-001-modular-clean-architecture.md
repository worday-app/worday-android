# ADR-001: Çok Modüllü Clean Architecture

- Durum: Kabul edildi (mevcut yapı)
- Tarih: 24.11.2026

## Bağlam
Worday; domain kuralları, yerel veri erişimi, ağ entegrasyonu ve birden fazla kullanıcı arayüzü özelliği içerir. Tek modül zamanla derleme bağımlılıklarını ve değişiklik etkisini büyütebilir.

## Karar
Uygulama `:app`, `:core` ve `:feature` modüllerine ayrılır. Domain katmanı Android framework'ünden bağımsız iş modelleri, repository sözleşmeleri ve use case'leri barındırır. Data/network katmanları sözleşmeleri uygular. Feature modülleri ekran ve ViewModel sorumluluğunu üstlenir.

## Gerekçe
- İş kuralları UI ve veri kaynağından ayrılır.
- Modüller bağımsız test ve geliştirmeye daha elverişlidir.
- Derleme kapsamı ve sahiplik sınırları daha iyi yönetilir.
- Gelecekte backend, yeni veri kaynağı veya platform adaptasyonu eklenebilir.

## Sonuçlar
Olumlu: sorumluluk ayrımı, test edilebilirlik, ölçeklenebilir ekip çalışması.
Olumsuz: Gradle yapılandırması ve bağımlılık yönetimi karmaşıklaşır; aşırı modülerleştirme küçük değişikliklerde maliyet doğurabilir.

## Kurallar
- Domain, Android UI veya Retrofit/Room tiplerine bağımlı olmamalıdır.
- Feature modülleri başka feature modüllerini doğrudan çağırmamalıdır.
- Bağımlılık yönü dış katmanlardan domain sözleşmelerine doğru olmalıdır.
- `:app` composition root olarak modülleri birleştirmelidir.
