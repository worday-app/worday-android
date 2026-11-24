# ADR-004: Arka plan işleri için WorkManager

- **Durum:** Kabul edildi
- **Tarih:** 24.11.2026

## Neden?

Bazı işler uygulama ekranda açık değilken de çalışabilmeli. Örneğin günlük kontrol işleri ve ileride eklenecek veri senkronizasyonu, uygulama kapatılıp yeniden açılsa bile planını korumalı.

Android, arka plan işlerinin tam belirlenen saatte çalışacağını garanti etmez. Bu nedenle iş mantığı gecikmeye dayanıklı olmalıdır.

## Karar

Kalıcı ve ertelenebilir arka plan işleri için WorkManager kullanılır. Aynı işin tekrar tekrar eklenmesini önlemek için uygun unique work politikası seçilir.

## Uygulama kuralları

- İşler birden fazla kez çalışsa da aynı sonucu üretmeli (idempotent olmalı).
- Günlük seri hesabı sabit 24 saat farkına göre değil, yerel takvim gününe göre yapılmalı.
- Geçici ağ hatalarında sınırlı retry ve backoff uygulanmalı.
- Kalıcı hatalar sessizce tekrar denenmemeli; kaydedilmeli ve izlenebilir olmalı.
- Arka plan işi başarısız olsa bile kullanıcı kelime öğrenmeye devam edebilmeli.
