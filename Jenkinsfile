pipeline {

    agent any

    environment {
        AWS_REGION = 'us-west-2'
        AWS_ACCOUNT_ID = '938379788459'

        ECR_FRONTEND = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/dev-frontend"
        ECR_BACKEND  = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/dev-backend"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare Jenkins Agent') {
            steps {
                sh '''
                    set -eux

                    echo "Checking AWS CLI..."
                    aws --version

                    echo "Checking Docker..."

                    if ! command -v docker >/dev/null 2>&1; then
                        echo "Docker not found. Installing Docker..."

                        sudo dnf install -y docker
                        sudo systemctl enable docker
                        sudo systemctl start docker

                        echo "Docker installation completed."
                    else
                        echo "Docker already installed."
                    fi

                    sudo systemctl start docker || true

                    sudo docker --version
                    sudo docker info >/dev/null

                    echo "Jenkins agent preparation completed."
                '''
            }
        }

        stage('Verify AWS Identity') {
            steps {
                sh '''
                    set -eux

                    echo "Verifying Jenkins AWS identity..."
                    aws sts get-caller-identity
                '''
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

        stage('Generate Image Tag') {
            steps {
                script {
                    env.IMAGE_TAG = "${env.BUILD_NUMBER}-${env.GIT_COMMIT.take(7)}"
                }

                sh '''
                    echo "Image tag: ${IMAGE_TAG}"
                '''
            }
        }

        stage('ECR Login') {
            steps {
                sh '''
                    set -eux

                    aws ecr get-login-password \
                      --region "${AWS_REGION}" | \
                    sudo docker login \
                      --username AWS \
                      --password-stdin \
                      "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
                '''
            }
        }

        stage('Build Docker Images') {
            steps {
                sh '''
                    set -eux

                    echo "Building backend image..."
                    sudo docker build \
                      -t "${ECR_BACKEND}:${IMAGE_TAG}" \
                      -t "${ECR_BACKEND}:latest" \
                      ./backend

                    echo "Building frontend image..."
                    sudo docker build \
                      -t "${ECR_FRONTEND}:${IMAGE_TAG}" \
                      -t "${ECR_FRONTEND}:latest" \
                      ./frontend

                    echo "Images built successfully."

                    sudo docker images | grep -E 'dev-(frontend|backend)'
                '''
            }
        }

        stage('Push Docker Images') {
            steps {
                sh '''
                    set -eux

                    echo "Pushing backend image..."
                    sudo docker push "${ECR_BACKEND}:${IMAGE_TAG}"

                    echo "Pushing frontend image..."
                    sudo docker push "${ECR_FRONTEND}:${IMAGE_TAG}"

                    echo "Pushing latest tags..."
                    sudo docker push "${ECR_BACKEND}:latest"
                    sudo docker push "${ECR_FRONTEND}:latest"

                    echo "Images pushed successfully."
                '''
            }
        }

        stage('Verify ECR Images') {
            steps {
                sh '''
                    set -eux

                    echo "Backend image:"
                    aws ecr describe-images \
                      --repository-name dev-backend \
                      --region "${AWS_REGION}" \
                      --image-ids imageTag="${IMAGE_TAG}"

                    echo "Frontend image:"
                    aws ecr describe-images \
                      --repository-name dev-frontend \
                      --region "${AWS_REGION}" \
                      --image-ids imageTag="${IMAGE_TAG}"
                '''
            }
        }
    }

    post {

        success {
            echo "CI/CD image build and push completed successfully."
            echo "Image tag: ${IMAGE_TAG}"
        }

        failure {
            echo "Pipeline failed. Check the stage logs above."
        }

        always {
            echo "Jenkins pipeline execution completed."
        }
    }
}
