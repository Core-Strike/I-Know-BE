# iKnow — Spring Boot Backend API 명세서

> 실시간 표정 기반 학습 이해도 감지 시스템의 Spring Boot 백엔드입니다.

---

## 기술 스택

| 항목 | 내용 |
|------|------|
| 언어 | Java 21 |
| 프레임워크 | Spring Boot 4.0.5 |
| 데이터베이스 | MySQL 8 |
| ORM | Spring Data JPA |
| 실시간 통신 | WebSocket + STOMP (SockJS) |
| 빌드 | Gradle |

---

## 전체 시스템에서의 역할

```
[교육생 브라우저]
  연속 3회 confused 감지 (30초)
  → POST /api/confused-events          ← 이 서비스로 전송

[Spring Boot]                          ← 이 서비스
  ① confused 이벤트 수신
  → Alert DB 저장 (studentName 포함)
  → 해당 시점 강의 토픽 매칭 (LectureTopic)
  → WebSocket으로 강사에게 즉시 푸시

  ② 강사 STT 텍스트 수신
  → POST /api/lecture-chunk
  → LectureTopic DB 저장

  ③ 이벤트 직후 2분 STT 원문 수신
  → POST /api/lecture-summary
  → Alert에 lectureText 저장

  ④ 강사 PASS 버튼
  → DELETE /api/alerts/:alertId

[강사 브라우저]
  WebSocket 구독 /topic/alert/{sessionId}
  → 알림 수신 → 대시보드 표시
  → 마이크 녹음 → STT 변환 → POST /api/lecture-chunk
  → 알림 2분 후 STT 원문 → POST /api/lecture-summary
  → STT 원문 → POST FastAPI /summarize (AI 요약, Spring 미관여)
```

---

## 데이터 모델

### Session

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | PK |
| `sessionId` | String | **6자리 숫자** (100000~999999, 중복 시 재생성) |
| `classId` | String | 반 식별자 |
| `thresholdPct` | Integer | 혼란 감지 임계값 % (기본 50, 대시보드 참고용) |
| `curriculum` | String | 커리큘럼 텍스트 (대시보드 참고용) |
| `startedAt` | LocalDateTime | 세션 시작 시각 (자동) |
| `endedAt` | LocalDateTime | 세션 종료 시각 |
| `status` | Enum | `ACTIVE` / `ENDED` |

### Alert

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | PK |
| `sessionId` | String | 연관 세션 ID |
| `studentId` | String | 교육생 식별자 |
| `studentName` | String | 교육생 이름 |
| `capturedAt` | LocalDateTime | confused 발생 시각 |
| `confusedScore` | Double | 혼란도 점수 (0.0 ~ 1.0) |
| `reason` | String | GPT 판단 이유 |
| `unclearTopic` | String | 매칭된 강의 토픽 (LectureTopic 자동 조회) |
| `lectureText` | String (TEXT) | 이벤트 직후 2분 녹음 STT 원문 (Spring 저장) |
| `lectureSummary` | String (TEXT) | GPT 요약문 — FastAPI `/summarize` 결과, 프론트가 직접 관리 |
| `createdAt` | LocalDateTime | 저장 시각 (자동) |

> `lectureSummary`는 Spring이 직접 생성하지 않습니다. 프론트엔드가 FastAPI `/summarize`를 호출하여 요약을 받고, 필요 시 별도로 저장합니다.

### LectureTopic

| 필드 | 타입 | 설명 |
|------|------|------|
| `id` | Long | PK |
| `sessionId` | String | 연관 세션 ID |
| `classId` | String | 반 식별자 |
| `topicText` | String (TEXT) | 강사 음성 STT 변환 텍스트 |
| `capturedAt` | LocalDateTime | 녹음 시각 |
| `createdAt` | LocalDateTime | 저장 시각 (자동) |

---

## REST API 명세

### 세션

---

#### `POST /api/sessions`
강사가 수업 시작 시 세션을 생성합니다.

**Request Body**
```json
{
  "classId": "class-1",
  "thresholdPct": 50,
  "curriculum": "Spring Boot, JPA, 트랜잭션"
}
```

| 필드 | 필수 | 설명 |
|------|------|------|
| `classId` | Y | 반 식별자 |
| `thresholdPct` | N | 혼란 임계값 %, 기본 50 (대시보드 참고용) |
| `curriculum` | N | 커리큘럼 텍스트 (대시보드 참고용) |

**Response `200 OK`**
```json
{
  "sessionId": "382941",
  "classId": "class-1",
  "thresholdPct": 50,
  "curriculum": "Spring Boot, JPA, 트랜잭션",
  "status": "ACTIVE",
  "startedAt": "2024-01-01T09:00:00",
  "endedAt": null
}
```

---

#### `PATCH /api/sessions/{sessionId}/end`
세션을 종료합니다.

**Response `200 OK`**
```json
{
  "sessionId": "382941",
  "classId": "class-1",
  "thresholdPct": 50,
  "curriculum": "Spring Boot, JPA, 트랜잭션",
  "status": "ENDED",
  "startedAt": "2024-01-01T09:00:00",
  "endedAt": "2024-01-01T10:30:00"
}
```

---

### 혼란 이벤트

---

#### `POST /api/confused-events`
교육생 confused 이벤트를 수신합니다. 프론트엔드에서 연속 3회(30초) 감지 시 호출합니다.

수신 즉시 Alert를 저장하고, 해당 시점 가장 가까운 강의 토픽(LectureTopic)을 매칭한 뒤 강사에게 WebSocket으로 푸시합니다.

**Request Body**
```json
{
  "studentId": "student_42",
  "studentName": "홍길동",
  "sessionId": "382941",
  "capturedAt": "2024-01-01T09:15:00",
  "confusedScore": 0.72,
  "reason": "fear 수치가 높고 눈썹이 찡그려진 상태로 혼란 신호가 명확합니다."
}
```

**Response `200 OK`** — Body 없음

---

#### `GET /api/sessions/{sessionId}/alerts`
세션 알림 이력을 조회합니다. (최신순)

**Response `200 OK`**
```json
[
  {
    "id": 1,
    "sessionId": "382941",
    "studentId": "student_42",
    "studentName": "홍길동",
    "capturedAt": "2024-01-01T09:15:00",
    "confusedScore": 0.72,
    "reason": "fear 수치가 높고 눈썹이 찡그려진 상태로 혼란 신호가 명확합니다.",
    "unclearTopic": "트랜잭션 격리 수준이란 무엇인가",
    "lectureText": "지금 설명드리는 트랜잭션 격리 수준은...",
    "lectureSummary": null,
    "createdAt": "2024-01-01T09:15:01"
  }
]
```

---

#### `GET /api/sessions/{sessionId}/confused-events`
세션의 confused 이벤트 목록을 조회합니다. alerts와 동일한 데이터를 반환합니다.

---

#### `DELETE /api/alerts/{alertId}`
강사가 PASS 버튼 클릭 시 알림을 삭제합니다.

**Response `204 No Content`** — Body 없음

---

### 강의 토픽

---

#### `POST /api/lecture-chunk`
강사 음성의 STT 변환 텍스트를 저장합니다. (주기적 저장, 토픽 매칭용)

**Request Body**
```json
{
  "sessionId": "382941",
  "classId": "class-1",
  "topicText": "트랜잭션 격리 수준이란 무엇인가",
  "capturedAt": "2024-01-01T09:14:30"
}
```

**Response `200 OK`** — Body 없음

---

### 강의 원문

---

#### `POST /api/lecture-summary`
이벤트 직후 2분 녹음의 STT 원문을 전송합니다.

STT 원문을 해당 Alert의 `lectureText`에 저장합니다. AI 요약(`lectureSummary`)은 Spring이 처리하지 않으며, 프론트엔드가 FastAPI `/summarize`를 직접 호출합니다.

**Request Body**
```json
{
  "alertId": 1,
  "sessionId": "382941",
  "audioText": "지금 설명드리는 트랜잭션 격리 수준은 READ COMMITTED, REPEATABLE READ..."
}
```

**Response `200 OK`** — Alert 전체 객체 반환

---

#### `GET /api/alerts/{alertId}/summary`
Alert 단건 조회입니다. `lectureText` 저장 완료 여부 확인에 사용합니다.

**Response `200 OK`** — Alert 전체 객체 반환

---

### 대시보드

---

#### `GET /api/dashboard/classes`
반별 통계를 조회합니다.

**Response `200 OK`**
```json
[
  {
    "classId": "class-1",
    "alertCount": 12,
    "avgConfusedScore": 0.64,
    "topTopics": [
      "트랜잭션 격리 수준이란 무엇인가",
      "JPA 연관관계 매핑",
      "인덱스 동작 원리"
    ],
    "recentAlerts": [ ]
  }
]
```

---

## WebSocket 명세

### 연결

| 항목 | 내용 |
|------|------|
| 엔드포인트 | `ws://{host}/ws` |
| 프로토콜 | SockJS + STOMP |

### 구독 토픽 `/topic/alert/{sessionId}`

`POST /api/confused-events` 수신 즉시 강사에게 푸시됩니다.

**수신 메시지**
```json
{
  "studentId": "student_42",
  "studentName": "홍길동",
  "sessionId": "382941",
  "confusedScore": 0.72,
  "reason": "fear 수치가 높고 눈썹이 찡그려진 상태로 혼란 신호가 명확합니다.",
  "unclearTopic": "트랜잭션 격리 수준이란 무엇인가",
  "capturedAt": "2024-01-01T09:15:00"
}
```

---

## 파일 구조

```
src/main/java/com/iknow/
├── IknowApplication.java
├── config/
│   ├── WebSocketConfig.java          # SockJS + STOMP 설정
│   └── CorsConfig.java               # CORS 전체 허용
├── controller/
│   ├── SessionController.java        # POST /api/sessions, PATCH /:id/end
│   ├── ConfusedEventController.java  # POST /api/confused-events, GET alerts/confused-events
│   ├── AlertController.java          # DELETE /api/alerts/:alertId
│   ├── LectureChunkController.java   # POST /api/lecture-chunk
│   ├── LectureSummaryController.java # POST /api/lecture-summary, GET /api/alerts/:id/summary
│   └── DashboardController.java      # GET /api/dashboard/classes
├── service/
│   ├── SessionService.java           # 6자리 sessionId 생성 (중복 재생성)
│   ├── ConfusedEventService.java     # Alert 저장 + 토픽 매칭 + WebSocket 푸시
│   ├── LectureChunkService.java
│   ├── LectureSummaryService.java    # STT 원문(lectureText) 저장만 담당
│   └── DashboardService.java
├── entity/
│   ├── Session.java                  # thresholdPct, curriculum 추가
│   ├── Alert.java                    # studentName, lectureText, lectureSummary 추가
│   └── LectureTopic.java
├── repository/ (3개)
└── dto/
    ├── request/
    │   ├── CreateSessionRequest.java      # thresholdPct, curriculum 추가
    │   ├── ConfusedEventRequest.java      # studentName 추가
    │   ├── LectureChunkRequest.java
    │   └── LectureSummaryRequest.java
    └── response/
        ├── SessionResponse.java           # thresholdPct, curriculum 추가
        ├── AlertResponse.java             # studentName, lectureText, lectureSummary 추가
        ├── AlertWebSocketPayload.java     # studentName 추가
        └── DashboardClassResponse.java

src/main/resources/
└── application.yml
```

---

## 실행 방법

### 로컬 실행

```bash
./gradlew bootRun
```

애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

### 테스트 실행

```bash
./gradlew test
```

### 참고

- 현재 테스트와 애플리케이션 실행은 `src/main/resources/application.yml`에 설정된 MySQL 연결 정보를 사용합니다.
- Windows 환경에서는 `gradlew.bat bootRun`, `gradlew.bat test`로 실행할 수 있습니다.
