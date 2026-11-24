# ADR-004: WorkManager ile Güvenilir Arka Plan İşleri

- Durum: Kabul edildi (mevcut yaklaşım)
- Tarih: 24.11.2026

## Bağlam
Seri kontrolü ve gelecekte offline değişikliklerin sunucuya aktarılması uygulama kapalıyken de çalışabilmelidir. Android işletim sistemi kesin zamanlı çalışmayı garanti etmez.

## Karar
Kalıcı, ertelenebilir ve yeniden denenebilir işler WorkManager ile yürütülür. İşler unique work adıyla planlanır ve idempotent tasarlanır.

## Gerekçe
- Uygulama yeniden başlatıldığında işlerin korunması.
- Kısıtlar ve retry/backoff desteği.
- Android sürümleri arasında standart iş planlama.

## Sonuçlar
Olumlu: lifecycle dayanıklılığı ve retry yönetimi.
Olumsuz: tam zamanlı tetikleme garantisi yoktur; iş mantığı çalıştığı andaki veriye göre güvenli olmalıdır.

## Uygulama kuralları
- Seri hesabı sabit 24 saat varsayımına bağlanmamalı; takvim günü ile hesaplanmalıdır.
- Senkronizasyon işi event ID ile idempotent olmalıdır.
- Geçici ağ hataları retry; kalıcı hatalar fail ve gözlemlenebilir olmalıdır.
- İşler kullanıcı öğrenme akışının ön koşulu olmamalıdır.
