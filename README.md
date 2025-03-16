# 🚀 FaturaPay: Fatura ve Abonelik Yönetimi


FaturaPay, kullanıcıların faturalarını yönetmelerini ve ödemelerini takip etmelerini sağlayan modern bir platformdur. Ayrıca abonelik bazlı faturaları destekler ve otomatik hatırlatma sistemleri sunar.

---
## 📸 Ekran Görüntüleri

| 🔑 Giriş Ekranı | 📊 Dashboard | 📋 Faturalar | 💳 Abonelikler |
|---------------|-------------|-------------|--------------|
| <img src="https://github.com/user-attachments/assets/35b35e68-6b03-4499-a232-23cbb7522384" width="350"> | <img src="https://github.com/user-attachments/assets/056100b5-2156-484b-b0d3-2c6abac8171b" width="350"> | <img src="https://github.com/user-attachments/assets/83fbc183-297d-41a1-b536-f6e56d8520fa" width="350"> | <img src="https://github.com/user-attachments/assets/e4d1a1ae-347b-40ab-a46b-d73bd733c58b" width="350"> |

---
## 📌 Özellikler

✅ **JWT ile Kimlik Doğrulama** 🔐  
✅ **Fatura Yönetimi** 🧾  
✅ **Abonelik Yönetimi** 🔄  
✅ **Kafka ile Bildirim Sistemi** 📢  
✅ **Docker & Kafka Entegrasyonu** 🐳  
✅ **PostgreSQL & Spring Boot Backend** ☕  
✅ **Kotlin & Jetpack Compose ile Mobil Uygulama** 📱  

---

## 🛠 Kurulum

### 1️⃣ Gereksinimler
Projeyi çalıştırmadan önce aşağıdaki bağımlılıkların kurulu olduğundan emin olun:

- **JDK 17** ☕
- **Maven** 🏗️
- **Docker & Docker Compose** 🐳
- **PostgreSQL** 🗄️
- **Kafka & Zookeeper** 📨

### 2️⃣ Backend Kurulumu (Spring Boot & PostgreSQL)

#### 1️⃣ Bağımlılıkları Yükle
```bash
mvn clean install
```

#### 2️⃣ PostgreSQL Bağlantısı (`application.properties`)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/faturapay
spring.datasource.username=postgres
spring.datasource.password=12345
spring.jpa.hibernate.ddl-auto=update
```

#### 3️⃣ Uygulamayı Başlat
```bash
mvn spring-boot:run
```

---

## 🐳 Kafka & Docker Kullanımı

### 1️⃣ Docker Compose ile Kafka ve Zookeeper'ı Başlat
```bash
docker-compose up -d
```

### 2️⃣ Kafka Mesaj Gönderme ve Dinleme

📤 **Kafka'ya mesaj gönder:**
```bash
docker exec -it kafka_custom kafka-console-producer --topic test-topic --bootstrap-server kafka_custom:9093
```

📥 **Kafka'dan mesaj al:**
```bash
docker exec -it kafka_custom kafka-console-consumer --topic test-topic --from-beginning --bootstrap-server kafka_custom:9093
```

---

## 📡 API Endpointleri

| **Özellik** | **Metod** | **Endpoint** |
|-------------|----------|-------------|
| Kullanıcı Kaydı | `POST` | `/api/auth/register` |
| Kullanıcı Girişi | `POST` | `/api/auth/login` |
| Fatura Listesi | `GET` | `/invoices` |
| Fatura Ekle | `POST` | `/invoices` |
| Fatura Güncelle | `PUT` | `/invoices/{id}` |
| Fatura Sil | `DELETE` | `/invoices/{id}` |
| Abonelik Listesi | `GET` | `/subscriptions` |
| Abonelik Ekle | `POST` | `/subscriptions` |
| Abonelik Güncelle | `PUT` | `/subscriptions/{id}` |
| Abonelik Sil | `DELETE` | `/subscriptions/{id}` |

---

## 📢 Kafka Bildirim Sistemi
Kafka ile kullanıcıya **fatura hatırlatmaları ve abonelik yenileme bildirimleri** gönderilir.

- **Consumer Group:** `notification-group`
- **Topic:** `payment-reminder`

📢 **Bildirim göndermek için:**
```bash
docker exec -it kafka_custom kafka-console-producer --topic payment-reminder --bootstrap-server kafka_custom:9093
```

✅ **Bildirimleri dinlemek için:**
```bash
docker exec -it kafka_custom kafka-console-consumer --topic payment-reminder --from-beginning --bootstrap-server kafka_custom:9093
```

---

## 📌 Projeyi Docker ile Çalıştırma

Projeyi **Docker üzerinden çalıştırmak** için şu adımları takip edin:

```bash
docker-compose up -d
```

🔍 **Çalışan konteynerleri kontrol etmek için:**
```bash
docker ps
```

🛑 **Konteynerleri durdurmak için:**
```bash
docker-compose down
```

---

## 👨‍💻 Katkıda Bulunma

Projeye katkıda bulunmak için:
1️⃣ **Fork** edin 🍴  
2️⃣ **Branch oluşturun** (`feature-xyz` gibi) 🌱  
3️⃣ **Değişiklik yapıp commit atın** 📝  
4️⃣ **Pull Request gönderin** 🚀  

💡 **Her türlü öneri ve geri bildirim için iletişime geçebilirsiniz!**

---

## 📌 Lisans
📜 MIT Lisansı © 2025 **FaturaPay**

🚀 **FaturaPay ile finansal yönetiminizi kolaylaştırın!** 💸🎯

