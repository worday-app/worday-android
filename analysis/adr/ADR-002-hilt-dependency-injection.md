# ADR-002: Dependency Injection için Hilt

- Durum: Kabul edildi (mevcut yapı)
- Tarih: 24.11.2026

## Bağlam
Room database, DAO, repository implementasyonları, Retrofit servisleri ve ViewModel'ler yaşam döngüsü ve bağımlılık yönetimi gerektirir.

## Karar
Android uygulamasında Hilt kullanılacaktır. SingletonComponent altında database, network client ve repository binding'leri sağlanır; ViewModel'ler `@HiltViewModel` ile oluşturulur. WorkManager entegrasyonu gerektiğinde HiltWorkerFactory üzerinden yapılır.

## Gerekçe
- Android lifecycle ile bütünleşik dependency graph.
- Constructor injection ile açık bağımlılıklar.
- Testlerde fake/mock implementasyonla değiştirme imkânı.
- Singleton kaynakların tek örnek olarak yönetilmesi.

## Sonuçlar
Olumlu: tekrarlanabilir wiring, daha az manuel factory, test edilebilirlik.
Olumsuz: annotation processing/build karmaşıklığı ve graph hatalarının derleme zamanında çözülmesi gereği.

## Uygulama kuralları
- Mümkün olduğunda constructor injection tercih edilir.
- `@Provides` üçüncü taraf/constructor ile üretilemeyen tiplerde; `@Binds` interface-implementation eşlemesinde kullanılır.
- Singleton yalnızca gerçekten uygulama kapsamı paylaşılması gereken kaynaklara verilir.
- Testlerde gerçek API anahtarı veya canlı servis kullanılmaz.
