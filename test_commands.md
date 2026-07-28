 # Test_01:Security & Auth (JwtService, AuthenticationService, OtpService)
 **Token creation/validation, login workflows, OTP rate limiting, and expiration handling.**

…\nhahocduongbe > .\mvnw.cmd test "-Dtest=vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthenticationServiceTest,vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.OtpServiceTest,vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.JwtServiceTest" "-Dsurefire.useFile=false"

 # Test_02:Core Dental Domain (ExamServiceImpl, PatientServiceImpl)**
 **Dental record linking, odontogram updates with @Retryable concurrency handling, data scoping by organization, and Excel imports.**

 .\mvnw.cmd test "-Dtest=vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.PatientServiceImplTest,vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImplTest" "-Dsurefire.useFile=false"

 # Test_03: Administration (OrganizationServiceImpl, AcademicYearServiceImpl)
 **Organization CRUD with duplicate-class validation, delete prevention, deletable-class checks, academic year transitions (promote/graduate), rollback, and system_log audit trails.**

 .\mvnw.cmd test "-Dtest=vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.OrganizationServiceImplTest,vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.AcademicYearServiceImplTest" "-Dsurefire.useFile=false"

 # Test_04: Utility and Infrastructure (ExcelUtil, EmailService)
 .\mvnw.cmd test "-Dtest=vn.viettel.bvrhm.nhahocduong.api.common.internal.utils.ExcelUtilTest,vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.EmailServiceTest" "-Dsurefire.useFile=false"

 # Test_05: User Administration & Security (UserServiceImpl)
 **User registration workflow, duplicate-username detection, admin approval (TC-ADMIN-01), account lock/unlock toggle (TC-ADMIN-02), and password reset verification.**

 .\mvnw.cmd test "-Dtest=vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserServiceTest" "-Dsurefire.useFile=false"

 # Test_06: Campaign & Schedule Management (ExamCampaignServiceImpl, ExamScheduleServiceImpl)
 **Campaign CRUD, schedule creation & dentist assignment (TC-CAMP-01), student exam completion tracking (TC-CAMP-02), and dentist in-app notification broadcasting (TC-CAMP-03).**

 .\mvnw.cmd test "-Dtest=vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamCampaignServiceImplTest,vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamScheduleServiceImplTest" "-Dsurefire.useFile=false"

 # All Tests (12 Test Classes / 204 Tests)
 **Run the entire suite of 12 isolated unit test classes across Security, Dental Domain, Administration, Utility, User Admin, and Campaign Management.**

 .\mvnw.cmd test "-Dtest=vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.AuthenticationServiceTest,vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.OtpServiceTest,vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.JwtServiceTest,vn.viettel.bvrhm.nhahocduong.api.user.internal.service.UserServiceTest,vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.PatientServiceImplTest,vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamServiceImplTest,vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.OrganizationServiceImplTest,vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.AcademicYearServiceImplTest,vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamCampaignServiceImplTest,vn.viettel.bvrhm.nhahocduong.api.nhahocduong.internal.service.impl.ExamScheduleServiceImplTest,vn.viettel.bvrhm.nhahocduong.api.common.internal.utils.ExcelUtilTest,vn.viettel.bvrhm.nhahocduong.api.auth.internal.service.EmailServiceTest" "-Dsurefire.useFile=false"