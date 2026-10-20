<div align="center">

<img src="./src/assets/cashflow-mate-banner.png" alt="CashFlow Mate — 소상공인 현금흐름 관리 서비스" width="100%" />

# CashFlow Mate — Backend ⚙️

### 매출·정산·대사·지출 데이터 관리 및 현금흐름 예측 API

[🌐 배포 서비스](https://cash-flow-mate-frontend.vercel.app/) · [🖥️ Frontend Repository](https://github.com/gain-0525/CashFlow-Mate-Frontend)

</div>

---

## 📌 프로젝트 소개

**CashFlow Mate**는 소상공인이 매출과 실제 정산 내역을 비교하고, 지출 및 향후 현금흐름을 함께 파악할 수 있도록 돕는 웹 서비스입니다.

이 저장소는 **Spring Boot 기반 백엔드**로, 프론트엔드에 필요한 데이터 조회·관리 API와 MySQL 연동을 담당합니다.

## ✨ 주요 도메인 및 기능

| 영역 | 역할 |
| --- | --- |
| **매출 (Sales)** | 매출 데이터 관리 |
| **정산 (Settlements)** | 정산 데이터 관리 |
| **대사 (Reconciliation)** | 매출·정산 정보 비교 및 차이 확인 |
| **지출 (Expenses)** | 지출 데이터 관리 |
| **현금흐름 예측 (Forecast)** | 향후 30일의 현금흐름 예측 결과 제공 |
| **CSV 업로드** | 업로드 작업 및 오류 정보 관리 |
| **사업장 설정** | 서비스 운영에 필요한 사업장 설정 정보 관리 |

> 대사 판정 규칙 및 예측 계산 방식의 상세 내용은 실제 구현 코드와 함께 설명을 보완할 수 있습니다.

## 🛠️ 기술 스택

| 구분 | 기술 |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot |
| Build | Gradle |
| Persistence | Spring Data JPA |
| Database | MySQL |
| Infrastructure | AWS EC2 (Ubuntu), Nginx |
| Deployment | systemd, HTTPS (Let's Encrypt) |

## 🏗️ 시스템 아키텍처

```text
React + TypeScript (Vercel)
             │
             │ HTTPS API 요청
             ▼
      Nginx (EC2 :443)
             │
             │ Reverse Proxy
             ▼
   Spring Boot (localhost:8080)
             │
             │ JPA
             ▼
        MySQL (EC2)
```

- 프론트엔드는 Vercel에서 제공됩니다.
- Nginx가 HTTPS 요청을 내부 Spring Boot 서버로 전달합니다.
- Spring Boot는 MySQL과 연동하여 데이터를 저장하고 조회합니다.
- 백엔드 프로세스는 EC2에서 `systemd` 서비스로 실행합니다.

## 🗄️ 데이터베이스 구성

| 테이블 | 설명 |
| --- | --- |
| `business_settings` | 사업장 설정 정보 |
| `sales` | 매출 정보 |
| `settlements` | 정산 정보 |
| `expenses` | 지출 정보 |
| `upload_batches` | CSV 업로드 작업 정보 |
| `upload_errors` | CSV 업로드 오류 정보 |

## 🔌 API

프론트엔드와 REST API로 통신합니다. 확인된 API 예시는 아래와 같습니다.

| Method | Endpoint | 설명 |
| --- | --- | --- |
| `GET` | `/api/settlements` | 정산 내역 조회 |

> 전체 엔드포인트 및 요청·응답 스키마는 프로젝트의 Controller 코드와 API 명세를 기준으로 추가해 주세요. 운영 환경에서 API 문서를 공개할 경우 접근 제어를 검토해야 합니다.

## 🚀 로컬 실행

### 1. 저장소 클론

```bash
git clone https://github.com/gain-0525/CashFlow-Mate.git
cd CashFlow-Mate
```

### 2. 실행 환경 준비

- JDK 21
- MySQL
- 데이터베이스 `cashflow_mate` 및 필요한 테이블 스키마

### 3. 데이터베이스 연결 설정

`src/main/resources/application.yml`의 데이터베이스 설정 예시:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cashflow_mate
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
```

환경변수 `DB_USERNAME`, `DB_PASSWORD`를 설정합니다. **실제 비밀번호는 GitHub에 커밋하지 마세요.**

> 현재 배포 과정에서는 로컬 MySQL의 스키마와 데이터를 SQL 덤프로 EC2에 이전했습니다. 빈 DB만 생성하면 테이블이 자동 생성되지 않을 수 있으므로, 실행 전 스키마 준비가 필요합니다.

### 4. 서버 실행

macOS / Linux:

```bash
./gradlew bootRun
```

Windows:

```powershell
.\gradlew.bat bootRun
```

배포용 JAR 빌드:

```bash
./gradlew clean bootJar
```

## ☁️ 배포 및 트러블슈팅

**AWS EC2 + Nginx + MySQL** 환경에 배포했습니다.

- **CORS 문제:** Vercel 프론트엔드 도메인을 허용 출처에 추가하여 교차 출처 요청 문제 해결
- **HTTP 500 오류:** EC2 MySQL에 `settlements` 테이블이 없다는 서버 로그를 확인하고 스키마 및 데이터 이전으로 해결
- **HTTPS 구성:** DuckDNS 도메인, Nginx 리버스 프록시 및 Let's Encrypt 인증서 적용
- **프로세스 관리:** `systemd`로 Spring Boot 서비스 실행 및 재시작 관리

## 🔐 보안 및 향후 개선

> 현재 MVP는 사용자 인증 및 사용자별 데이터 접근 제어가 구현되지 않은 상태로 확인되었습니다. **실제 금융·개인정보를 저장하지 말고 시연용 데이터만 사용해야 합니다.** API를 공개 운영하기 전 인증·인가를 구현해야 합니다.

- Spring Security 기반 인증 및 접근 권한 구현
- DB 비밀번호 및 배포 환경변수 안전한 관리
- 자동 배포(CI/CD), 백업 및 모니터링 구축
- 데이터베이스 마이그레이션 체계 도입

## 🔗 관련 링크

- **Frontend:** https://github.com/gain-0525/CashFlow-Mate-Frontend
- **Backend:** https://github.com/gain-0525/CashFlow-Mate
- **서비스:** https://cash-flow-mate-frontend.vercel.app/

---

<div align="center">
  <sub>CashFlow Mate · 소상공인의 현금흐름을 더 쉽고, 더 정확하게</sub>
</div>
