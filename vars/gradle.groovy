/*
  forma de invocación de método call:
  def ejecucion = load 'script.groovy'
  ejecucion.call()
*/
def call(){
    stage("Paso 1: Build && Test"){
        sh "kjhjkhjkh"
    }
    stage("Paso 2: Sonar - Análisis Estático"){
        sh "echo 'Análisis Estático!'"
        withSonarQubeEnv('sonarqube') {
            sh 'gradle sonarqube -Dsonar.projectKey=maven-gradle -Dsonar.host.url=http://sonarqube:9000 -Dsonar.login=9d886284c574229ebc1bf5b9051c6dbbd8063e16 -Dsonar.java.binaries=build'
        }
    }
    stage("Paso 3: Curl Springboot Gradle sleep 20"){
        sh "gradle bootRun&"
        sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
    stage("Paso 4: Subir Nexus"){
        nexusPublisher nexusInstanceId: 'nexus',
        nexusRepositoryId: 'devops-usach-nexus',
        packages: [
            [$class: 'MavenPackage',
                mavenAssetList: [
                    [classifier: '',
                    extension: 'jar',
                    filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar'
                ]
            ],
                mavenCoordinate: [
                    artifactId: 'DevOpsUsach2020',
                    groupId: 'com.devopsusach2020',
                    packaging: 'jar',
                    version: '0.1.2'
                ]
            ]
        ]
    }
    stage("Paso 5: Descargar Nexus"){
        sh ' curl -X GET -u $NEXUS_USER:$NEXUS_PASSWORD "http://nexus:8081/repository/devops-usach-nexus/com/devopsusach2020/DevOpsUsach2020/0.1.2/DevOpsUsach2020-0.1.2.jar" -O'
    }
    stage("Paso 6: Levantar Artefacto Jar"){
        sh 'nohup bash java -jar DevOpsUsach2020-0.1.2.jar & >/dev/null'
    }
    stage("Paso 7: Testear Artefacto - Dormir(Esperar 20sg) "){
        sh "sleep 20 && curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
}
return this;