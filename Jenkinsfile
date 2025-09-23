pipeline {
    agent any

    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false'
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        DOCKER_REGISTRY = 'hosbee001' // Docker Hub 사용자명
        // DOCKER_REGISTRY = 'localhost:5000' // 로컬 테스트용
        IMAGE_TAG = "${BUILD_NUMBER}"
        GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    }

    tools {
        gradle 'Gradle-8.5' // Jenkins에 설정된 Gradle 버전
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.BUILD_VERSION = "${env.BUILD_NUMBER}-${env.GIT_COMMIT_SHORT}"
                }
            }
        }

        stage('Clean & Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test Report'
                    ])
                }
            }
        }

        stage('Build Docker Images') {
            parallel {
                stage('Build Admin API') {
                    steps {
                        script {
                            def image = docker.build("${DOCKER_REGISTRY}/hosbee-admin-api:${BUILD_VERSION}", "-f hosbee-admin-api/Dockerfile .")
                            docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-registry-credentials') {
                                image.push()
                                image.push('latest')
                            }
                        }
                    }
                }
                stage('Build Admin UI') {
                    steps {
                        script {
                            def image = docker.build("${DOCKER_REGISTRY}/hosbee-admin-ui:${BUILD_VERSION}", "-f hosbee-admin-ui/Dockerfile .")
                            docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-registry-credentials') {
                                image.push()
                                image.push('latest')
                            }
                        }
                    }
                }
                stage('Build User API') {
                    steps {
                        script {
                            def image = docker.build("${DOCKER_REGISTRY}/hosbee-user-api:${BUILD_VERSION}", "-f hosbee-user-api/Dockerfile .")
                            docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-registry-credentials') {
                                image.push()
                                image.push('latest')
                            }
                        }
                    }
                }
                stage('Build Web UI') {
                    steps {
                        script {
                            def image = docker.build("${DOCKER_REGISTRY}/hosbee-web-ui:${BUILD_VERSION}", "-f hosbee-web-ui/Dockerfile .")
                            docker.withRegistry("https://${DOCKER_REGISTRY}", 'docker-registry-credentials') {
                                image.push()
                                image.push('latest')
                            }
                        }
                    }
                }
            }
        }

        stage('Deploy to Development') {
            when {
                branch 'develop'
            }
            steps {
                script {
                    // Docker Compose를 이용한 개발 환경 배포
                    sh """
                        export BUILD_VERSION=${BUILD_VERSION}
                        export DOCKER_REGISTRY=${DOCKER_REGISTRY}
                        docker-compose -f docker-compose.dev.yml down
                        docker-compose -f docker-compose.dev.yml up -d
                    """
                }
            }
        }

        stage('Deploy to Production') {
            when {
                branch 'main'
            }
            steps {
                input message: '운영환경에 배포하시겠습니까?', ok: 'Deploy'
                script {
                    // Kubernetes 배포 또는 Docker Swarm 배포
                    sh """
                        export BUILD_VERSION=${BUILD_VERSION}
                        export DOCKER_REGISTRY=${DOCKER_REGISTRY}
                        docker-compose -f docker-compose.prod.yml down
                        docker-compose -f docker-compose.prod.yml up -d
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
            // 슬랙 알림 등
        }
        failure {
            echo 'Pipeline failed!'
            // 실패 알림
        }
    }
}