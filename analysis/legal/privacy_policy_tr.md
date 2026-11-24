# Worday — Gizlilik Politikası (Taslak)

**Son güncelleme:** 24 Kasım 2026  
**Yürürlük:** Uygulama mağazasında yayımlanmadan önce veri sorumlusu bilgileri, iletişim kanalı, gerçek SDK yapılandırması ve saklama süreleri tamamlanmalıdır.

## 1. Veri sorumlusu ve iletişim

Worday hizmetinin veri sorumlusu: **[Şirket/kişi unvanı ve açık adres eklenecek]**.  
Gizlilik başvuruları: **[KVKK/GDPR başvuru e-posta adresi eklenecek]**.

Bu alanlar tamamlanmadan bu metin yayıma hazır değildir.

## 2. İşlenebilecek veriler

Uygulamanın etkin özelliklerine ve kullanıcının tercihine bağlı olarak aşağıdaki kategoriler işlenebilir:

- **Uygulama verileri:** öğrenme seviyesi, günlük hedef, kaydedilen kelimeler, tekrar aralıkları, quiz sonuçları ve çalışma oturumları. Mevcut Android sürümünde bu verilerin önemli bölümü Room ve DataStore aracılığıyla cihazda saklanır.
- **Hesap verileri:** Firebase Authentication etkinleştirilirse kimlik doğrulama sağlayıcısının ürettiği kullanıcı tanımlayıcısı ve hesap bilgileri. Mevcut kaynak incelemesinde tam bir hesap/senkronizasyon akışı doğrulanmamıştır.
- **AI isteği:** kullanıcı bir kelime için örnek cümle istediğinde ilgili kelime ve istek parametreleri Gemini hizmetine iletilebilir. Üretim mimarisinde Gemini anahtarı sunucuda tutulmalı; aktarımın backend üzerinden yapıldığı ve hangi kayıtların tutulduğu açıklanmalıdır.
- **Tanılama/analitik:** Firebase Analytics veya Crashlytics etkinleştirilirse uygulama etkileşimi, cihaz/uygulama sürümü ve çökme tanılama verileri işlenebilir.
- **Reklam verileri:** AdMob etkinleştirilirse reklam gösterimi, reklam tanımlayıcıları ve ilgili cihaz/etkileşim verileri Google tarafından işlenebilir. Kişiselleştirilmiş reklam ve consent seçenekleri gerçek yapılandırmaya göre açıklanmalıdır.

Yukarıdaki üçüncü taraf SDK'ların kod deposunda bağımlılık olarak bulunması, tek başına veri toplandığını kanıtlamaz. Yayın sürümündeki gerçek SDK başlatma ve ağ trafiği denetlenerek bu bölüm kesinleştirilmelidir.

## 3. İşleme amaçları ve hukuki sebepler

Veriler; kelime öğrenme işlevlerini sunmak, ilerlemeyi cihazda saklamak, kullanıcının talep ettiği AI örneklerini üretmek, hizmet güvenliğini sağlamak, hata ayıklamak ve (etkinse) reklam/analitik sunmak amacıyla işlenebilir.

KVKK ve GDPR kapsamındaki hukuki sebep; işleme amacına göre sözleşmenin kurulması/ifası, hukuki yükümlülük, meşru menfaat veya gerekli olduğu hallerde açık rıza olabilir. Her amaç için hukuki sebep veri sorumlusu tarafından ayrıca belirlenmeli; açık rıza hizmetin zorunlu olmayan özelliklerine bağlanmamalıdır.

## 4. Üçüncü taraf hizmetler ve uluslararası aktarım

- **Firebase / Google:** kimlik doğrulama, analitik veya çökme raporlama özellikleri gerçekten etkinleştirilmişse ilgili veriler Google altyapısına aktarılabilir.
- **Gemini API / Google:** kullanıcı tarafından istenen örnek cümle talebi ve kelime girdisi işlenmek üzere Google'a gönderilebilir. Google'ın geçerli API veri işleme koşulları ve saklama ayarları incelenmelidir.
- **AdMob / Google:** reklam özelliği etkinleştirilirse reklam sunumu ve ölçümü için gerekli veriler işlenebilir.

Veriler Türkiye/AEA dışındaki ülkelere aktarılabilir. KVKK m.9 ve GDPR Bölüm V kapsamındaki aktarım mekanizması, alıcılar, amaçlar ve güvenceler gerçek veri akışına göre belirlenip kullanıcıya açıklanmalıdır.

## 5. Saklama ve güvenlik

Yerel öğrenme verileri uygulama cihazında, kullanıcı uygulama verisini silene veya uygulamayı kaldırana kadar kalabilir; işletim sistemi yedekleme ayarları bu sonucu etkileyebilir. Sunucu/SDK verileri için saklama süreleri ilgili hizmet yapılandırması ve sağlayıcı sözleşmelerine göre belirlenmelidir. **[Kesin saklama süreleri ve silme prosedürü yayın öncesi eklenecek.]**

Makul teknik ve idari tedbirler; TLS, erişim kontrolü, en az ayrıcalık, secret yönetimi, log maskeleme, güvenlik güncellemeleri ve yedekleme kontrollerini içermelidir. İnternete dağıtılan Android paketinde API anahtarı gizli kabul edilmemelidir.

## 6. Kullanıcı hakları

KVKK ve uygulanabildiği ölçüde GDPR kapsamında kullanıcılar; verilerine erişme, düzeltme, silme/yok etme, işlemeyi kısıtlama veya itiraz etme, taşınabilirlik ve rızayı geri çekme haklarına sahip olabilir. Başvurular yukarıdaki iletişim kanalına yapılabilir. Başvuru usulü ve yanıt süreleri ilgili mevzuata göre yürütülür.

## 7. Çocuklar

Uygulamanın hedef kitlesi ve çocuklara yönelik olup olmadığı mağaza beyanında doğru belirtilmelidir. Çocuklardan kişisel veri toplanması söz konusuysa yaşa uygun bilgilendirme, ebeveyn izni ve ilgili mevzuat gereklilikleri ayrıca değerlendirilmelidir.

## 8. Değişiklikler

Politika güncellendiğinde yeni sürüm ve yürürlük tarihi yayımlanır. Önemli değişikliklerde kullanıcıya uygun kanaldan bildirim yapılır.

## 9. Yayın öncesi tamamlanması gerekenler

1. Veri sorumlusu unvanı, adresi ve başvuru e-postası.
2. Firebase, Gemini ve AdMob'un release build'deki gerçek etkinliği ve veri alanları.
3. Hukuki sebep, saklama süreleri, alıcı/işleyen listesi ve uluslararası aktarım mekanizması.
4. Consent yönetimi, hesap/veri silme süreci ve çocuk kullanıcı değerlendirmesi.
5. KVKK/GDPR uzmanı hukuk danışmanı incelemesi.

*Bu belge hukuki danışmanlık değildir; doğrulanmamış SDK davranışları hakkında kesin veri toplama iddiasında bulunmaz.*
