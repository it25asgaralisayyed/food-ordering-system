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
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Selenium Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG -t $IMAGE_NAME:latest .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DH_USER',
                                                  passwordVariable: 'DH_PASS')]) {
                    sh '''
                        echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
                        docker push $IMAGE_NAME:$IMAGE_TAG
                        docker push $IMAGE_NAME:latest
                        docker logout
                    '''
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    docker rm -f food-app || true
                    docker run -d --name food-app -p 9090:8080 $IMAGE_NAME:$IMAGE_TAG
                    sleep 15
                    curl -f http://localhost:9090/ > /dev/null
                    echo "Application is running inside the container"
                    docker rm -f food-app
                '''
            }
        }
    }

    post {
        success { echo 'Pipeline completed successfully. Image pushed to Docker Hub.' }
        failure { echo 'Pipeline failed. Check the stage logs above.' }
    }
}