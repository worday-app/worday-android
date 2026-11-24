# Worday — Ürün Gereksinimleri

**Sürüm:** 1.1  
**Tarih:** 24.11.2026  
**Durum:** Mevcut uygulama incelenerek hazırlanmış taslak. Ürün kararları ayrıca onaylanmalıdır.

## 1. Worday ne yapıyor?

Worday, Türkçe arayüzle İngilizce kelime öğrenmeye yardımcı olan bir Android uygulamasıdır. Kullanıcılar seviyelerine uygun kelimeleri çalışabilir, quiz çözebilir, zorlandıkları kelimeleri tekrar edebilir ve ilerlemelerini takip edebilir.

Temel öğrenme özellikleri internet bağlantısı olmadan da çalışmalıdır. AI ile örnek cümle oluşturma gibi çevrimiçi özellikler bağlantı gerektirir.

## 2. Hedef kullanıcı

İngilizce kelime dağarcığını düzenli çalışmayla geliştirmek isteyen kullanıcılar. Uygulama A1–C2 seviyelerini hedefler.

## 3. Ürünün temel hedefleri

- Kullanıcıya seviyesine uygun kelimeler sunmak.
- Tekrar zamanını SM-2 algoritmasıyla planlamak.
- Günlük hedef ve quiz ile düzenli çalışma alışkanlığı oluşturmak.
- Kaydedilen kelimeleri tek bir yerde toplamak.
- Çalışma geçmişini anlaşılır şekilde göstermek.
- AI örnek cümlelerini isteğe bağlı destek olarak sunmak.
- İnternet olmadığında temel öğrenme akışını korumak.

## 4. İşlevsel gereksinimler

### 4.1 İlk kullanım
- Kullanıcı onboarding akışını tamamlayabilmeli veya seviye testini atlayabilmelidir.
- Seviye A1, A2, B1, B2, C1 veya C2 olarak belirlenebilmelidir.
- Kullanıcı günlük kelime hedefini seçebilmelidir.
- Seviye ve hedef cihazda saklanmalıdır.

### 4.2 Kelime öğrenme
- Günlük kelimeler kullanıcının seviyesine göre gösterilmelidir.
- Kelime ekranında İngilizce kelime, Türkçe karşılık, açıklama, IPA ve örnekler bulunmalıdır.
- Kullanıcı kelimeyi bildiğini veya bilmediğini belirtebilmelidir.
- Telaffuz cihazın metin okuma özelliğiyle dinlenebilmelidir.
- Kelime kişisel kelime defterine eklenip çıkarılabilmelidir.
- Günlük hedef ve sonraki günün kelimelerine erişim kuralı net olmalıdır.

### 4.3 Tekrar sistemi
- Quiz cevabı SM-2 algoritmasına uygun kalite puanına dönüştürülmelidir.
- Kalite 0–2 başarısız, 3–5 başarılı kabul edilir.
- Başarısız cevapta tekrar sayısı sıfırlanır ve sonraki aralık bir gün olur.
- Başarılı cevaplarda ilk iki aralık 1 ve 6 gündür; sonraki aralık ease factor ile hesaplanır.
- Ease factor 1.3 değerinin altına düşmez.
- Üç başarılı tekrar sonrası kelime öğrenilmiş sayılır.

Bu kurallar mevcut algoritmadan çıkarılmıştır. Yanlış cevap geçmişinin nasıl korunacağı ve öğrenilmiş kelimenin tekrar yanlış cevaplanınca ne olacağı ürün kararı olarak netleştirilmelidir.

### 4.4 Quiz
- Sorular günlük kelimelerden hazırlanmalıdır.
- Her soruda doğru cevap ve mümkünse üç çeldirici bulunmalıdır.
- Bir soru yalnızca bir kez cevaplanmalıdır.
- Cevaptan sonra kullanıcıya sonuç gösterilmelidir.
- Mevcut kodda geçme eşiği %60'tır. Yayın öncesi onaylanmalıdır.
- Quiz sonucu, günlük oturum ve seri bilgisi tutarlı güncellenmelidir.
- Aynı gün tekrar quiz başlatma davranışı tanımlanmalıdır.

### 4.5 Kelime defteri
- Kaydedilen kelimeler listelenmelidir.
- İngilizce ve Türkçe alanlarda arama yapılabilmelidir.
- Tümü, öğreniliyor ve öğrenildi filtreleri bulunmalıdır.
- Kullanıcı kelimeyi listeden çıkarabilmelidir.

### 4.6 İstatistikler
- Güncel ve en uzun çalışma serisi gösterilmelidir.
- Öğrenilen kelime sayısı ve quiz doğruluğu gösterilmelidir.
- Haftalık grafik son yedi takvim gününü doğru yansıtmalıdır.
- Her metriğin nasıl hesaplandığı açıkça tanımlanmalıdır.

### 4.7 AI örnek cümleleri
- Kullanıcı seçtiği kelime için örnek cümle isteyebilmelidir.
- AI isteği başarısız olsa bile kelime öğrenme ve quiz çalışmaya devam etmelidir.
- Kota ve bağlantı hataları anlaşılır biçimde gösterilmelidir.
- Gemini anahtarı Android uygulamasında tutulmamalı; istek backend üzerinden yapılmalıdır.

## 5. Şu anki koddan görülen varsayılanlar

| Konu | Mevcut durum | Netleştirilecek konu |
|---|---|---|
| Günlük hedef | 10 kelime | Kullanıcı değiştirebiliyor mu, sınırlar ne? |
| Başlangıç seviyesi | A1 | Seviye testi atlanınca ne olur? |
| Quiz geçme puanı | %60 | Ürün onayı |
| SM-2 doğru cevap | Kalite 4 | Kullanıcı cevabı kaliteye nasıl çevrilir? |
| SM-2 yanlış cevap | Kalite 1 | Yanlış cevap geçmişi nasıl etkiler? |
| Öğrenilmiş kelime | 3 başarılı tekrar | Ürün tanımı |
| AI kotası | Kodda sabit 2; tüketim akışı tamamlanmamış | Ücretsiz kota ve sıfırlanma süresi |
| Hesap/senkronizasyon | Henüz yok | Backend kapsamı |

## 6. Yayın için temel beklentiler

- Uygulamanın temel öğrenme akışı uçak modunda çalışmalıdır.
- Kelime ve ilerleme verileri uygulama kapatılıp açıldığında korunmalıdır.
- Güncelleme sırasında kullanıcı verisi silinmemelidir.
- Ağ veya AI hatası uygulamanın çökmesine neden olmamalıdır.
- Kullanıcı verileri hesap özelliği eklendiğinde hesap bazında ayrılmalıdır.
- Gizlilik metni ve mağaza beyanları gerçek veri akışını anlatmalıdır.

## 7. İlk sürüm dışında kalanlar

- iOS uygulaması.
- Sosyal özellikler ve arkadaş sıralaması.
- Öğretmen veya kurum paneli.
- Web istemcisi.
- Ücretli AI paketleri ve abonelik (iş modeli kararı verilene kadar).
- Uzaktan kelime kataloğu yönetimi.

## 8. Notlar

Bu doküman mevcut koddan çıkarılan gereksinimleri ve henüz karara bağlanmamış ürün konularını birlikte içerir. Tablodaki açık kararlar onaylanmadan kesin ürün kuralı sayılmamalıdır.
