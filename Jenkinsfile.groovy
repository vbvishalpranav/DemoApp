pipeline {
    agent any
    stages {
        stage ('Clone') {
            steps {
			dir(path: "/home/ec2-user/pranav"){
            //    git branch: 'master', url: "https://github.com/jfrog/project-examples.git"
			    checkout scm
				}
            }
        }
		stage ('Demo App') {
            steps {
			dir(path: "/home/ec2-user/pranav"){
                
				withMaven(maven: 'Maven_Home', publisherStrategy: 'EXPLICIT')
                   sh 'mvn -f /home/ec2-user/pranav/pom.xml install'
                
				}
            }
			
        }

        stage ('Artifactory configuration') {
            steps {
                rtServer (
                    id: "ARTIFACTORY_SERVER",
                    url: SERVER_URL,
                    credentialsId: CREDENTIALS
                )

                rtMavenDeployer (
                    id: "MAVEN_DEPLOYER",
                    serverId: "ARTIFACTORY_SERVER",
                    releaseRepo: ARTIFACTORY_LOCAL_RELEASE_REPO,
                    snapshotRepo: ARTIFACTORY_LOCAL_SNAPSHOT_REPO
                )

                rtMavenResolver (
                    id: "MAVEN_RESOLVER",
                    serverId: "ARTIFACTORY_SERVER",
                    releaseRepo: ARTIFACTORY_VIRTUAL_RELEASE_REPO,
                    snapshotRepo: ARTIFACTORY_VIRTUAL_SNAPSHOT_REPO
                )
            }
        }



        stage ('Publish build info') {
            steps {
                rtPublishBuildInfo (
                    serverId: "ARTIFACTORY_SERVER"
                )
            }
        }
    }
} 