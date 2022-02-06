def call(){
  
	pipeline {
	    agent any
	    environment {
	        NEXUS_USER         = credentials('NEXUS-USER')
	        NEXUS_PASSWORD     = credentials('NEXUS-PASS')
	    }
	    parameters {
	        choice(
	            name:'compileTool',
	            choices: ['Maven', 'Gradle'],
	            description: 'Seleccione herramienta de compilacion'
	        )
	        text(
	        	description: 'Enviar los stages separados por ";"... Vacío si necesita todos los stages', name: 'stages'
	        )
	    }
	    stages {
	        stage("Pipeline"){
	            steps {
	                script{
	                	sh "env"
	                	env.TAREA = ""
	                  switch(params.compileTool)
	                    {
	                        case 'Maven':
	                            maven.call()
	                        break;
	                        case 'Gradle':
	                            gradle.call()
	                        break;
	                    }
	                }
	            }
	            post{
	                success{
	                    slackSend (color: 'good', message: "[Claudio Correa] [${JOB_NAME}] [${BUILD_TAG}] Ejecucion Exitosa", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'TOKEN-SLACK')
	                }
	                failure{
	                    slackSend (color: 'danger', message: "[Claudio Correa] [${env.JOB_NAME}] [${BUILD_TAG}] Ejecucion fallida en stage [${env.TAREA}]", teamDomain: 'dipdevopsusac-tr94431', tokenCredentialId: 'TOKEN-SLACK')
	                }
	            }
	        }
	    }
	}

}

return this;