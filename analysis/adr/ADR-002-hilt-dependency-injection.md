# ADR-002: Bağımlılık yönetimi için Hilt

- **Durum:** Kabul edildi
- **Tarih:** 24.11.2026

## Neden gerekli?

Worday; veritabanı, DAO'lar, repository'ler, Retrofit servisleri ve ViewModel'ler gibi birbirine bağlı bileşenler kullanıyor. Bu nesnelerin nerede ve nasıl oluşturulduğunu tek bir yerde yönetmek gerekiyor.

## Karar

Android tarafında bağımlılık yönetimi için Hilt kullanıyoruz. Uygulama genelinde paylaşılması gereken kaynaklar uygun Hilt bileşenlerinde tanımlanır. ViewModel'ler Hilt üzerinden oluşturulur. WorkManager ile Hilt entegrasyonu gereken işlerde WorkerFactory kullanılır.

## Uygulama kuralları

- Mümkün olduğunda constructor injection kullanılır.
- `@Binds`, interface ve implementasyon eşleştirmelerinde tercih edilir.
- `@Provides`, Hilt'in kendisinin oluşturamadığı üçüncü taraf sınıflarda kullanılır.
- `@Singleton` yalnızca gerçekten uygulama boyunca paylaşılması gereken nesnelere verilir.
- Testlerde gerçek API anahtarları veya canlı servisler kullanılmaz.

## Dikkat edilmesi gerekenler

Hilt derleme yapılandırmasına ek yük getirir. Modüller ve scope'lar gereksiz yere büyütülmemeli; bağımlılık grafiği sade tutulmalıdır.
