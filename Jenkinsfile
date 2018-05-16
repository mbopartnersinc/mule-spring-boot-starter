#!groovy

node {
    stage 'Build and unit test'

    git branch: 'develop', credentialsId: '4e03df13-cd12-4e0e-a658-c28aa55934c5', url: 'git@github.com:mbopartnersinc/rest-adapter.git'

    mvn '-B -DskipITs clean source:jar deploy'

    step $class: 'hudson.tasks.junit.JUnitResultArchiver', testResults: 'target/surefire-reports/*.xml'

    stage 'Quality Analysis'
    mvn '-B sonar:sonar -Dsonar.host.url="http://sonar.mbopartners.com" -Dsonar.java.libraries=target/*.jar'

    stage 'Integration Tests'
    mvn '-B failsafe:integration-test'
}

def mvn(args) {
    sh "${tool 'Maven'}/bin/mvn ${args}"
}
