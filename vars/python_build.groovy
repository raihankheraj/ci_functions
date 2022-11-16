def call(service) {
    pipeline {
        agent any
        stages {
            stage('build') {
                steps {
                    sh "pip install -r ${service}/requirements.txt"
                }
            }
        }
    }
}
