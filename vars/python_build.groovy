def call(service) {
    pipeline {
        agent any
        stages {
            stage('build') {
                steps {
                    sh "pip install -r ${service}/requirements.txt"
                }
            }
            stage('Python Lint') {
                steps {
                    sh "pylint-fail-under --fail_under 5.0 ${service}/app.py"
                }
            }

        }
    }
}
