pipeline {
    agent any

    options {
        timestamps()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Checkstyle') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew checkstyleAll --no-daemon'
            }
            post {
                always {
                    archiveArtifacts artifacts: 'app/build/reports/checkstyle/*',
                                     allowEmptyArchive: true
                }
            }
        }
    }
}