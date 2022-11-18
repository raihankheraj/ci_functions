def call(service, dockerRepoName, imageName) {
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

            stage('Package') {
                when {
                    expression { env.GIT_BRANCH == 'origin/main' }
                }
                steps {
                    withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                        sh "docker login -u 'raihankheraj' -p '$TOKEN' docker.io"
                        dir("${service}") {
                            sh "docker build -t ${dockerRepoName}:latest --tag raihankheraj/${dockerRepoName}:${imageName} ."
                        }
                        sh "docker push raihankheraj/${dockerRepoName}:${imageName}"
                    }
                }
            }


        }
    }
}
