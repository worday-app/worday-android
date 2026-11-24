# ADR-003: Room ile Yerel-Öncelikli Veri Saklama

- Durum: Kabul edildi (mevcut yaklaşım; migration kararı güncellenecek)
- Tarih: 24.11.2026

## Bağlam
Kelime kataloğu ve öğrenme ilerlemesi temel ürün deneyimidir. İnternet bağlantısı güvenilir olmayabilir; öğrenme/quiz akışları çevrimdışı çalışmalıdır.

## Karar
Kelime kataloğu, SM-2 durumu, kaydedilen kelimeler ve çalışma oturumları Room'da saklanır. DataStore basit kullanıcı tercihleri için kullanılır. Repository katmanı domain'e Flow ve suspend API'leri sunar.

## Gerekçe
- SQL sorguları ve ilişkisel tutarlılık.
- Flow ile reaktif UI güncellemesi.
- Çevrimdışı erişim ve hızlı yerel okuma.
- DAO seviyesinde test edilebilirlik.

## Sonuçlar
Olumlu: ağdan bağımsız temel kullanım, hızlı arama ve deterministik veri kaynağı.
Olumsuz: migration, yedekleme, cihazlar arası senkronizasyon ve şema sürümleme sorumluluğu uygulamaya aittir.

## Zorunlu iyileştirmeler
- `fallbackToDestructiveMigration()` üretimden kaldırılmalıdır.
- Room schema JSON'ları kaynak kontrolünde tutulmalıdır.
- Migration testleri eklenmelidir.
- Tarihler ortak gün başlangıcı/timezone standardına göre normalize edilmelidir.
- Hesap özelliği geldiğinde sabit `userId=1` yerine kullanıcı kapsamı uygulanmalıdır.
