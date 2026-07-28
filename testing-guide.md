# Testing Guide — JaCoCo + Unit Test

## Yêu cầu

- Java 17+
- VS Code (đã có Live Server extension)
- **Không cần cài Maven** — project đã có Maven Wrapper (`mvnw`)

## Chạy test + coverage

Dùng **Maven Wrapper** (không cần cài Maven global):

```powershell
# Windows (PowerShell / CMD):
mvnw.cmd clean verify

# Linux / Mac:
./mvnw clean verify
```

> Nếu đã cài Maven global thì dùng `mvn clean verify` như bình thường.

Sau khi chạy xong, report sẽ ở:

```
target/site/jacoco/index.html
```

## Xem report

Dùng **Live Server** trong VS Code:

1. Chuột phải vào file `target/site/jacoco/index.html`
2. Chọn **"Open with Live Server"**

→ Browser mở ra, xem được coverage từng package, class, method.

## Chỉ chạy test (không generate report)

```powershell
# Windows:
mvnw.cmd test

# Linux/Mac:
./mvnw test
```

## Chạy 1 file test cụ thể

```powershell
# Windows:
mvnw.cmd test -Dtest="NotificationServiceImplUnitTest"

# Linux/Mac:
./mvnw test -Dtest="NotificationServiceImplUnitTest"
```

## Chạy 1 method test cụ thể

```powershell
# Windows:
mvnw.cmd test -Dtest="NotificationServiceImplUnitTest#shouldSaveAndPushSse"

# Linux/Mac:
./mvnw test -Dtest="NotificationServiceImplUnitTest#shouldSaveAndPushSse"
```

## Viết test mới

### Quy ước

| Mục          | Quy ước                                                                                            |
| ------------ | -------------------------------------------------------------------------------------------------- |
| Thư mục test | `src/test/java/...` (mirror thư mục `src/main/java/`)                                              |
| Tên class    | `{TênService}UnitTest.java` hoặc `{TênService}Test.java`                                           |
| Framework    | JUnit 5 (`@Test`, `@BeforeEach`) + Mockito (`@Mock`, `@InjectMocks`) + AssertJ (`assertThat(...)`) |
| Annotation   | `@ExtendWith(MockitoExtension.class)` trên class                                                   |

## Cấu trúc test hiện tại

```
src/test/java/.../
├── ModulithArchitectureTest.java         (trống — chưa implement)
├── common/internal/service/
│   └── AreaServiceUnitTest.java          (1 test)
├── nhahocduong/service/
│   ├── impl/
│   │   ├── DiseaseServiceImplUnitTest.java      (1 test)
│   │   ├── ExamCampaignServiceImplUnitTest.java (13 tests)
│   │   ├── ExamServiceImplUnitTest.java         (8 tests)
│   │   ├── MedicalEnumServiceImplUnitTest.java  (10 tests)
│   │   └── NotificationServiceImplUnitTest.java (12 tests)
│   └── SseNotificationServiceTest.java  (12 tests)
└── user/internal/service/
    └── UserServiceUnitTest.java          (7 tests)

Tổng: 64 tests, 9 file
```

## Coverage hiện tại

| Chỉ số         | Giá trị                        |
| -------------- | ------------------------------ |
| Instruction    | ~7%                            |
| Branch         | ~1%                            |
| Lines          | ~4,010 covered / ~1,815 missed |
| Classes tested | 7/26+ service implementations  |

Coverage thấp vì mới test được ~7 service. Tập trung viết test cho các service quan trọng trước:

- Auth (AuthenticationService, JwtService, OtpService)
- Core business (PatientService, TreatmentRecordService, SchoolReportService)
- Notification (đã có test)
