# Worday — İş Gereksinimleri Dokümanı (BRD)

**Sürüm:** 1.0 · **Tarih:** 24.11.2026 · **Durum:** Koddan geriye doğru çıkarılmış taslak

## 1. Amaç ve kapsam

Worday, Türkçe konuşan kullanıcıların İngilizce kelime dağarcığını CEFR A1–C2 seviyelerinde, günlük öğrenme oturumları, aralıklı tekrar (SM-2), quiz, kişisel kelime defteri ve isteğe bağlı yapay zekâ örnek cümleleriyle geliştirmesini sağlayan Android uygulamasıdır.

İlk sürüm Android odaklıdır. Temel öğrenme işlevleri internet bağlantısı olmadan çalışmalıdır. Hesap ve çoklu cihaz senkronizasyonu, bu kod tabanında mevcut olmayan ancak backend yol haritasında ele alınan sonraki kapsamdır.

## 2. Paydaşlar ve kullanıcı profilleri

- Son kullanıcı: İngilizce öğrenen, Türkçe arayüz kullanan kişi.
- Ürün sahibi: Worday ürün ekibi.
- Teknik ekip: Android ve backend geliştiricileri.
- Harici hizmet sağlayıcılar: Google/Firebase, Gemini API sağlayıcısı, AdMob (etkinleştirildiği durumda).

## 3. Ürün hedefleri

1. Kullanıcıya seviyesine uygun kelimeleri düzenli sunmak.
2. Kelimelerin unutulmasını azaltmak için SM-2 tabanlı tekrar planlamak.
3. Günlük hedef ve quiz ile öğrenme alışkanlığı oluşturmak.
4. Kullanıcının zorlandığı kelimeleri daha sık tekrar ettirmek.
5. Öğrenme ilerlemesini ölçülebilir göstergelerle sunmak.
6. AI örnek cümlelerini isteğe bağlı bir destek olarak sunmak.
7. İnternet kesintisinde temel öğrenme deneyimini korumak.

## 4. Kapsam içi işlevler

### 4.1 İlk kullanım ve seviye
- Kullanıcı onboarding akışını başlatabilmeli veya seviye testini atlayabilmelidir.
- Seviye testi sonuçları A1, A2, B1, B2, C1 veya C2 olarak sınıflandırılmalıdır.
- Kullanıcı günlük kelime hedefini seçebilmelidir.
- Seviye ve hedef yerel olarak saklanmalıdır.

### 4.2 Günlük öğrenme
- Kullanıcı seçilen seviyeye ait günlük kelime setini görmelidir.
- Kelime kartında İngilizce kelime, Türkçe karşılık, tanım, IPA ve örnek cümleler sunulmalıdır.
- Kullanıcı kartı bildiğini/bilmediğini belirtebilmelidir.
- Kullanıcı kelime telaffuzunu cihaz TTS motoruyla dinleyebilmelidir.
- Kullanıcı kelimeyi kişisel kelime defterine ekleyip çıkarabilmelidir.
- Günlük hedef tamamlanmadan sonraki günün kelimeleri kilitli kalmalıdır (mevcut UI davranışından çıkarılmış kural).

### 4.3 Aralıklı tekrar (SM-2)
- Quiz/tekrar cevabı kalite puanına dönüştürülmelidir.
- Kalite 0–2 başarısız, 3–5 başarılı kabul edilmelidir.
- Başarısız cevap tekrar sayısını sıfırlamalı ve sonraki tekrar aralığını bir güne çekmelidir.
- Başarılı cevap tekrar sayısını artırmalı; ilk aralıklar 1 ve 6 gün olmalı, sonraki aralık ease factor ile hesaplanmalıdır.
- Ease factor 1.3 altına düşmemelidir.
- Üç başarılı tekrar sonrası kelime öğrenilmiş olarak işaretlenmelidir.
- Due kelimeler, sonraki tekrar zamanı geldiğinde tekrar listesine alınmalıdır.
- Algoritmanın tarih ve sınır durumları testlerle güvence altına alınmalıdır.

### 4.4 Quiz
- Günlük kelimelerden soru oluşturulmalıdır.
- Her soru için doğru Türkçe karşılık ve mümkünse üç farklı çeldirici gösterilmelidir.
- Kullanıcı bir soruya yalnızca bir kez cevap verebilmelidir.
- Cevap sonucu görünür olmalı, ardından sonraki soruya geçilmelidir.
- Başarı eşiği mevcut kodda %60'tır; ürün sahibi tarafından onaylanmalıdır.
- Başarılı quiz günlük hedefi tamamlamalı ve seri hesabını güncellemelidir.
- Aynı gün ikinci kez günlük quiz başlatma davranışı açıkça tanımlanmalıdır.

### 4.5 Kelime defteri
- Kaydedilen kelimeler listelenmelidir.
- İngilizce ve Türkçe alanlarda arama yapılmalıdır.
- Tümü, öğreniliyor ve öğrenildi filtreleri sunulmalıdır.
- Kaydedilen kelime listeden çıkarılabilmelidir.

### 4.6 İstatistikler
- Güncel ve en uzun öğrenme serisi gösterilmelidir.
- Toplam öğrenilen kelime ve doğruluk oranı gösterilmelidir.
- Haftalık çalışma verisi takvim günleriyle tutarlı sunulmalıdır.
- İstatistikler yalnızca başarılı quizleri değil, ürünün onaylayacağı tüm çalışma olaylarını doğru tanımlamalıdır.

### 4.7 AI örnek cümleleri
- Kullanıcı bir kelime için doğal İngilizce örnek cümle talep edebilmelidir.
- AI isteği başarısız olursa temel öğrenme akışı etkilenmemelidir.
- Kota, hız sınırı ve sağlayıcı hataları kullanıcıya anlaşılır biçimde sunulmalıdır.
- Gemini erişimi sunucu tarafında tutulmalıdır; istemciye gizli anahtar dağıtılmamalıdır.
- AI özelliği internet bağlantısı gerektirir; kelime öğrenmenin geri kalanı çevrimdışı sürmelidir.

## 5. İş kuralları ve açık kararlar

| Kural | Mevcut koddan çıkarım | Ürün kararı gerekli mi? |
|---|---|---|
| Günlük hedef varsayılanı | 10 kelime | Hayır |
| Seviye varsayılanı | A1 | Onboarding davranışı doğrulanmalı |
| Quiz geçme eşiği | %60 | Evet, ürün onayı |
| SM-2 kalite | Doğru=4, yanlış=1 kısa yolu | Evet, 0–5 kullanıcı deneyimi |
| Öğrenilmiş kelime | 3 başarılı tekrar | Evet, öğrenme tanımı |
| Günlük seri | Başarılı quiz sonrası artış | Evet, öğrenme oturumu kuralı |
| AI ücretsiz kota | Şu an sabit 2 / tüketim boş | Evet, plan ve kota |
| Kullanıcı hesabı | Mevcut uygulamada yok | Backend kapsam kararı |
| Veri senkronizasyonu | Mevcut değil | Backend kapsam kararı |

## 6. İşlevsel olmayan beklentiler

- Temel kelime öğrenme ve quiz internet olmadan çalışmalıdır.
- Kullanıcı verisi cihazda kalıcı saklanmalıdır.
- Ağ hataları uygulamanın çökmesine neden olmamalıdır.
- Kullanıcı verileri hesap bazında ayrıştırılmalıdır (hesap özelliği devreye alındığında).
- Uygulama verileri ve üçüncü taraf SDK davranışı şeffaf biçimde açıklanmalıdır.
- Erişilebilirlik, yerelleştirme, performans ve veri yedekleme politikaları yayın öncesi tamamlanmalıdır.

## 7. Kabul ölçütleri

- İlk kurulumda kelime kataloğu güvenilir biçimde hazırlanır.
- Uygulama uçak modunda açılır; günlük kelimeler, quiz ve kelime defteri çalışır.
- Quiz cevabı SM-2 alanlarını beklenen şekilde günceller.
- Aynı cevap çift işlenmez.
- Günlük seri aynı takvim gününde birden fazla artırılmaz.
- İstatistikler gerçek oturum verileriyle tutarlıdır.
- AI hatası yerel öğrenmeyi engellemez.
- Veri kaybı olmadan uygulama sürümü yükseltilebilir.
- Release paketi güvenlik, gizlilik ve Play Console kontrollerini geçer.

## 8. Kapsam dışı / sonraki sürüm

- iOS uygulaması.
- Sosyal ağ ve arkadaş sıralaması.
- Öğretmen/kurum paneli.
- Abonelik ve ücretli AI paketleri (iş modeli kararı alınana kadar).
- Uzak kelime kataloğu yönetimi.
- Web istemcisi.

## 9. Varsayımlar

Bu doküman mevcut kaynak koddan türetilmiştir; ürün sahibi onayı alınmamış eşikler ve davranışlar kesin gereksinim sayılmaz. Gerçek kişisel veri kategorileri, Firebase/AdMob etkinliği, veri saklama süreleri ve hukuki dayanaklar dağıtım yapılandırması üzerinden ayrıca doğrulanmalıdır.
