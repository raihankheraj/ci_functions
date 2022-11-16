def call(dockerRepoName, imageName) {
    pipeline {
        agent any
        stages {
            stage('build') {
                steps {
                    sh 'pip install -r requirements.txt'
                }
            }
            stage('Python Lint') {
                steps {
                    sh 'pylint-fail-under --fail_under 5.0 *.py'
                }
            }
            stage('Test and Coverage Test') {
                steps {
                    script {
                        // Remove existing xml report files (if any)
                        def test_reports_exist = fileExists 'test-reports'
                        if (test_reports_exist) {                        
                            sh 'rm -rf test-reports'
                        }
                        def api_test_reports_exist = fileExists 'api-test-reports'
                        if (api_test_reports_exist) {                        
                            sh 'rm -rf api-test-reports'
                        }

                        // Run the tests
                        def files = findFiles(glob: '**/test*.py')
                        for (file in files) {
                        sh "coverage run --omit */site-packages/*,*/dist-packages/* ${file.path}"
                        }
                        sh 'coverage report'
                    }
                }
                post {
                    always {
                        script {
                                def test_reports_exist = fileExists 'test-reports'
                                if (test_reports_exist) {                        
                                    junit 'test-reports/*.xml'
                                }
                                def api_test_reports_exist = fileExists 'api-test-reports'
                                if (api_test_reports_exist) {                        
                                    junit 'api-test-reports/*.xml'
                                }
                        }
                        //junit 'test-reports/*.xml'
                        //junit 'api-test-reports/*.xml'
                    }
                }
            }
            stage('Package') {
                when {
                    expression { env.GIT_BRANCH == 'origin/main' }
                }
                steps {
                    withCredentials([string(credentialsId: 'DockerHub', variable: 'TOKEN')]) {
                        sh "docker login -u 'raihankheraj' -p '$TOKEN' docker.io"
                        sh "docker build -t ${dockerRepoName}:latest --tag raihankheraj/${dockerRepoName}:${imageName} ."
                        sh "docker push raihankheraj/${dockerRepoName}:${imageName}"
                    }
                }
            }

            stage('Zip Artifacts') {
                steps {
                    sh 'zip app.zip *.py'
                    archiveArtifacts artifacts: 'app.zip'
                }
            }
        }
    }

}

