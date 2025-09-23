# Hosbee Jenkins 배포 가이드

## 1. Jenkins 사전 준비

### 필수 플러그인 설치
Jenkins 관리 → 플러그인 관리에서 다음 플러그인들을 설치:

```
- Pipeline (기본 설치됨)
- Docker Pipeline
- Git
- Gradle
- HTML Publisher
- Test Results Analyzer
- Blue Ocean (선택사항)
```

### 필요한 도구 설정
Jenkins 관리 → Global Tool Configuration에서:

1. **Java (JDK 17)**
   - Name: `Java-17`
   - JAVA_HOME: `/usr/lib/jvm/java-17-openjdk`

2. **Gradle**
   - Name: `Gradle-7.x`
   - Version: Gradle 7.x 또는 최신 버전

3. **Docker**
   - Docker가 Jenkins 서버에 설치되어 있어야 함

### Credentials 설정
Jenkins 관리 → Manage Credentials에서:

1. **Docker Registry Credentials**
   - ID: `docker-registry-credentials`
   - Type: Username with password
   - Username: Docker Registry 사용자명
   - Password: Docker Registry 패스워드

2. **Database Credentials (운영환경)**
   - ID: `db-credentials`
   - Type: Username with password

## 2. Pipeline Job 생성

### Step 1: 새 Pipeline Job 생성
1. Jenkins 대시보드 → "New Item"
2. Item name: `hosbee-pipeline`
3. Type: Pipeline 선택

### Step 2: Pipeline 설정
1. **General 설정**
   - GitHub project URL 입력 (있는 경우)
   - Build Triggers 설정 (예: GitHub webhook, SCM polling)

2. **Pipeline 설정**
   - Definition: `Pipeline script from SCM`
   - SCM: Git
   - Repository URL: 프로젝트 Git 저장소 URL
   - Branch: `*/main` 또는 `*/develop`
   - Script Path: `Jenkinsfile`

## 3. 환경 변수 설정

### Jenkins 시스템 환경 변수
Jenkins 관리 → Configure System → Global properties:

```bash
DOCKER_REGISTRY=your-registry.com
JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### Pipeline 환경 변수
각 환경별로 `.env` 파일 생성:

```bash
# .env.dev
DB_ROOT_PASSWORD=dev_root_password
DB_USER=hosbee_dev
DB_PASSWORD=dev_password
DOCKER_REGISTRY=dev-registry.com

# .env.prod
DB_ROOT_PASSWORD=prod_root_password
DB_USER=hosbee_prod
DB_PASSWORD=prod_password
DOCKER_REGISTRY=prod-registry.com
```

## 4. 배포 프로세스

### 개발 환경 배포 (develop 브랜치)
1. 코드 push → develop 브랜치
2. Jenkins 자동 빌드 시작
3. 테스트 실행
4. Docker 이미지 빌드 및 Push
5. 개발 서버에 자동 배포

### 운영 환경 배포 (main 브랜치)
1. 코드 push → main 브랜치
2. Jenkins 빌드 시작
3. 테스트 실행
4. Docker 이미지 빌드 및 Push
5. **수동 승인 대기**
6. 승인 후 운영 서버 배포

## 5. 서버 설정

### Docker 및 Docker Compose 설치
```bash
# Docker 설치 (Ubuntu)
sudo apt update
sudo apt install docker.io docker-compose
sudo usermod -aG docker jenkins

# 환경 변수 파일 복사
cp .env.example .env
# .env 파일 편집하여 실제 값으로 변경
```

### 포트 설정
각 서비스별 포트:
- hosbee-web-ui: 80 (HTTP), 443 (HTTPS)
- hosbee-admin-ui: 9030
- hosbee-admin-api: 9031
- hosbee-user-api: 9092
- MySQL: 3306

### 방화벽 설정
```bash
# 필요한 포트 오픈
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 9030/tcp
sudo ufw allow 9031/tcp
sudo ufw allow 9092/tcp
```

## 6. 모니터링 및 로그

### Health Check
각 서비스는 `/actuator/health` 엔드포인트를 통해 상태 확인 가능

### 로그 확인
```bash
# 전체 서비스 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f hosbee-admin-api
```

### Jenkins 빌드 결과
- Test Report: Jenkins 대시보드에서 확인
- Build History: 각 빌드별 성공/실패 상태
- Console Output: 빌드 과정 상세 로그

## 7. 트러블슈팅

### 일반적인 문제들

1. **Gradle 빌드 실패**
   ```bash
   # Gradle wrapper 권한 설정
   chmod +x gradlew
   ```

2. **Docker 이미지 빌드 실패**
   - Docker Registry 인증 확인
   - Dockerfile 경로 확인

3. **서비스 시작 실패**
   - 포트 충돌 확인
   - 환경 변수 설정 확인
   - 데이터베이스 연결 확인

4. **메모리 부족**
   - JVM 옵션 조정: `-Xms512m -Xmx1g`
   - Docker 컨테이너 메모리 제한 설정

### 유용한 명령어

```bash
# 서비스 재시작
docker-compose restart [service-name]

# 특정 서비스 스케일링
docker-compose up -d --scale hosbee-user-api=2

# 시스템 리소스 확인
docker stats

# 컨테이너 로그 실시간 모니터링
docker logs -f [container-name]
```

## 8. 보안 고려사항

1. **환경 변수 암호화**
   - Jenkins Credentials Store 사용
   - 민감한 정보는 파일에 직접 저장 금지

2. **네트워크 보안**
   - 내부 서비스 간 통신은 Docker 네트워크 사용
   - 외부 노출 포트 최소화

3. **SSL/TLS 설정**
   - HTTPS 인증서 설정
   - 데이터베이스 SSL 연결

4. **정기 업데이트**
   - 베이스 이미지 정기 업데이트
   - 보안 패치 적용

이 가이드를 따라 설정하면 hosbee 프로젝트를 Jenkins를 통해 안정적으로 배포할 수 있습니다.