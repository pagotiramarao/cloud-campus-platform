pipeline {

    agent any

    stages {

        stage('checkout') {
             
            steps {
                checkout scm
            }

        stage('backend tests') {
            steps {
                dir('backend'){
                    sh './mvnw test'
                }
            }
        }
        
        stage('backend package') {
            steps {
                dir('backend') {
                    sh './mvnw package -DskipTests'
                }
            }
        }

        stage('frontend install') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                }
            }
        }

        stage('frontend package') {
            steps {
                dir('frontend') {
                    sh 'npm run build'
                }
            }
        }

        
    }

    post {
        success {
            echo 'CI pipeline completed successfully.'
        }

        failure {
            echo 'CI pipeline failed. Check the stage logs above.'
        }

        always {
            echo 'CI pipeline execution completed.'
        }


    }   

}
