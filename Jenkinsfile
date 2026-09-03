pipeline {

    agent any

    options {
        skipDefaultCheckout(true)
        timestamps()
    }

    environment {
        AWS_REGION     = 'us-west-2'
        AWS_ACCOUNT_ID = '250224372179'

        ECR_REGISTRY   = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        FRONTEND_REPO  = 'dev-frontend'
        BACKEND_REPO   = 'dev-backend'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Jenkins Environment') {
            steps {
                sh '''
                    set -eux

                    echo "Checking AWS CLI..."
                    aws --version

                    echo "Checking Docker..."
                    docker --version

                    echo "Checking Docker access..."
                    docker info >/dev/null

                    echo "Jenkins AWS identity:"
                    aws sts get-caller-identity
                '''
            }
        }

        stage('Generate Image Tag') {
            steps {
                script {
                    def commit = sh(
                        script: 'git rev-parse --short=7 HEAD',
                        returnStdout: true
                    ).trim()

                    env.IMAGE_TAG = "${BUILD_NUMBER}-${commit}"

                    echo "Docker image tag: ${env.IMAGE_TAG}"
                }
            }
        }

        stage('Backend Tests') {
            steps {
                dir('backend') {
                    sh '''
                        set -eux
                        ./mvnw test
                    '''
                }
            }
        }

        stage('Backend Package') {
            steps {
                dir('backend') {
                    sh '''
                        set -eux
                        ./mvnw package -DskipTests
                    '''
                }
            }
        }

        stage('Frontend Install') {
            steps {
                dir('frontend') {
                    sh '''
                        set -eux
                        npm ci
                    '''
                }
            }
        }

        stage('Frontend Build') {
            steps {
                dir('frontend') {
                    sh '''
                        set -eux
                        npm run build
                    '''
                }
            }
        }

        stage('ECR Login') {
            steps {
                sh '''
                    set -eux

                    echo "Logging into Amazon ECR..."

                    aws ecr get-login-password \
                        --region "$AWS_REGION" | \
                    docker login \
                        --username AWS \
                        --password-stdin "$ECR_REGISTRY"
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                    set -eux

                    echo "Building frontend image..."
                    docker build \
                        -t "$ECR_REGISTRY/$FRONTEND_REPO:$IMAGE_TAG" \
                        ./frontend

                    echo "Building backend image..."
                    docker build \
                        -t "$ECR_REGISTRY/$BACKEND_REPO:$IMAGE_TAG" \
                        ./backend

                    echo "Docker images built successfully."

                    docker images | grep "$IMAGE_TAG"
                '''
            }
        }

        stage('Push Docker Images') {
            steps {
                sh '''
                    set -eux

                    echo "Pushing frontend image..."
                    docker push \
                        "$ECR_REGISTRY/$FRONTEND_REPO:$IMAGE_TAG"

                    echo "Pushing backend image..."
                    docker push \
                        "$ECR_REGISTRY/$BACKEND_REPO:$IMAGE_TAG"

                    echo "Both images pushed successfully."
                '''
            }
        }

        stage('Verify ECR Images') {
            steps {
                sh '''
                    set -eux

                    echo "Verifying frontend image in ECR..."

                    aws ecr describe-images \
                        --repository-name "$FRONTEND_REPO" \
                        --region "$AWS_REGION" \
                        --image-ids imageTag="$IMAGE_TAG"

                    echo "Verifying backend image in ECR..."

                    aws ecr describe-images \
                        --repository-name "$BACKEND_REPO" \
                        --region "$AWS_REGION" \
                        --image-ids imageTag="$IMAGE_TAG"

                    echo "ECR image verification successful."
                '''
            }
        }
    }

    post {

        success {
            echo "============================================"
            echo "CI/CD pipeline completed successfully."
            echo "Image Tag: ${env.IMAGE_TAG}"
            echo "Frontend: ${env.ECR_REGISTRY}/${env.FRONTEND_REPO}:${env.IMAGE_TAG}"
            echo "Backend : ${env.ECR_REGISTRY}/${env.BACKEND_REPO}:${env.IMAGE_TAG}"
            echo "============================================"
        }

        failure {
            echo "============================================"
            echo "Pipeline failed."
            echo "Check the failed stage and its logs."
            echo "============================================"
        }

        always {
            echo "Jenkins pipeline execution completed."
        }
    }
}
