def call() {
    pipeline {
        agent any
        stages {
            stage('build') {
                steps {
                    sh 'pip install -r Storage-Kafka/requirements.txt'
                }
            }
        }
    }
}
