pipeline {
    agent any

    tools {
        maven 'Maven3'
        jdk   'JDK17'
    }

    environment {
        DOCKER_USER = 'asgarali12311'
        IMAGE_NAME  = "${DOCKER_USER}/food-ordering-system"
        IMAGE_TAG   = "${BUILD_NUMBER}"
        DOCKER_BIN  = '"C:\\Users\\Asgar Ali Sayyed\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker"'
        DOCKER      = '"C:\\Users\\Asgar Ali Sayyed\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe"'
        PATH        = "C:\\Users\\Asgar Ali Sayyed\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker;${env.PATH}"
    }

    stages {

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/it25asgaralisayyed/food-ordering-system'
            }
        }

        stage('Build with Maven') {
            steps {
                bat 'mvn clean package -DskipTests'
            }
        }

        stage('Selenium Tests') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Check Docker') {
            steps {
                bat 'whoami'
                bat '%DOCKER% --version'
                bat '%DOCKER% ps'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat '%DOCKER% build -t %IMAGE_NAME%:%IMAGE_TAG% -t %IMAGE_NAME%:latest .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    bat '''
                        echo %DH_PASS%| %DOCKER% login -u %DH_USER% --password-stdin
                        %DOCKER% push %IMAGE_NAME%:%IMAGE_TAG%
                        %DOCKER% push %IMAGE_NAME%:latest
                        %DOCKER% logout
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                bat '''
                    %DOCKER% rm -f food-app >nul 2>&1 || ver >nul
                    %DOCKER% run -d --name food-app -p 9090:8080 %IMAGE_NAME%:%IMAGE_TAG%
                    ping -n 16 127.0.0.1 >nul
                    curl -f http://localhost:9090/ >nul
                    %DOCKER% rm -f food-app
                '''
            }
        }
    }

    post {
        success { echo 'Pipeline completed successfully. Image pushed to Docker Hub.' }
        failure { echo 'Pipeline failed. Check the stage logs above.' }
    }
}