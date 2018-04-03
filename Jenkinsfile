pipeline {
  agent any
  triggers {
        cron('H/2 * * * *')
    }
  stages {
    stage('build') {
      steps {
        sh 'mvn clean verify'
      }
    }
  }
  stage('test') {
      steps {
        sh 'mvn test'
      }
    }
}
