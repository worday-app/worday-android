# ADR-003: Room ile çevrimdışı veri saklama

- **Durum:** Kabul edildi
- **Tarih:** 24.11.2026

## Neden?

Worday'in temel işlevleri internet bağlantısı olmasa da kullanılabilmeli. Kullanıcının kelimeleri, tekrar durumu ve çalışma geçmişi cihazda erişilebilir olmalı.

## Karar

Kelime kataloğu, öğrenme ilerlemesi, kaydedilen kelimeler ve günlük oturumlar Room veritabanında tutulur. Basit kullanıcı tercihleri için DataStore kullanılır.

Repository katmanı, bu verileri domain katmanına Flow ve suspend fonksiyonları üzerinden sunar.

## Sonuçları

- Öğrenme ve quiz ekranları internet bağlantısına bağlı kalmaz.
- Yerel veriler hızlı okunur ve SQL sorgularıyla filtrelenebilir.
- Şema değişiklikleri, yedekleme ve ileride eklenecek cihazlar arası senkronizasyon uygulamanın sorumluluğundadır.

## Yapılması gerekenler

- Üretim sürümünde veri kaybına yol açabilecek `fallbackToDestructiveMigration()` kaldırılmalı.
- Room şemaları kaynak kontrolünde saklanmalı.
- Eski sürümden yeni sürüme geçişi doğrulayan migration testleri eklenmeli.
- Günlük oturum tarihleri için tek bir gün ve timezone standardı kullanılmalı.
- Hesap özelliği eklendiğinde sabit `userId = 1` kullanımı kaldırılmalı ve veriler kullanıcıya göre ayrılmalı.
