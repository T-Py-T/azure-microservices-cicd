pipeline {
    agent any

    environment {
        MAJOR_VERSION = '1' // Define the major version here or in the Jenkins job configuration
        DOCKERHUB_REPO = 'tnt850910' // Define the DockerHub repository here
        GIT_REPO_URL = 'https://github.com/T-Py-T/eks-jenkins-microservices-cicd' // Define the Git repository URL here
    }

    tools {
        gradle 'gradle8' // Ensure 'gradle8' is the name of the Gradle installation in Jenkins
        jdk 'jdk19'      // Ensure 'jdk19' is the name of the JDK installation in Jenkins
    }

    stages {
        stage('Clean Repo') {
            steps {
                deleteDir() // Clean the workspace
            }
        }

        stage('Pull Repo') { 
            steps { 
                git branch: 'adservice', credentialsId: 'git-cred', url: "${env.GIT_REPO_URL}" 
            } 
        }
        
        stage('Gradle Compile') { 
            steps {  
                sh "chmod +x ./gradlew"
                sh "./gradlew compileJava" 
            } 
        }

        // stage('Format Code') {
        //     steps {
        //         sh "./gradlew googleJavaFormat"
        //     }
        // }
        
        stage('Gradle Build') { 
            steps {  
                sh "./gradlew build" 
            } 
        }

        // There are no tests in the java branch currently        
        // stage('Gradle Test') { 
        //     steps { 
        //         sh "./gradlew test" 
        //     } 
        // }
        
        stage('Trivy FS Scan') { 
            steps { 
                sh "trivy fs --format table -o fs.html ." 
            } 
        }       
        
        stage('Build & Tag Docker Image') { 
            steps { 
                script { 
                    def versionTag = "${env.MAJOR_VERSION}.${env.BUILD_NUMBER}"
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker build -t ${env.DOCKERHUB_REPO}/adservice:${versionTag} ."
                    } 
                } 
            } 
        }

        stage('Docker Image Scan') { 
            steps { 
                script {
                    def versionTag = "${env.MAJOR_VERSION}.${env.BUILD_NUMBER}"
                    sh "trivy image --format table -o trivy-image-report.html ${env.DOCKERHUB_REPO}/adservice:${versionTag}" 
                }
            } 
        }
        
        stage('Push Docker Image') {
            steps { 
                script { 
                    def versionTag = "${env.MAJOR_VERSION}.${env.BUILD_NUMBER}"
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker push ${env.DOCKERHUB_REPO}/adservice:${versionTag}" 
                    } 
                } 
            } 
        }

        stage('Clean Workspace') {
            steps {
                deleteDir() // Clean the workspace
            }
        }

        stage('Pull Infra-Steps Repo') { 
            steps { 
                git branch: 'Infra-Steps', credentialsId: 'git-cred', url: "${env.GIT_REPO_URL}" 
            } 
        }

        stage('Update and Commit Deployment YAML') {
            steps {
                script {
                    def versionTag = "${env.MAJOR_VERSION}.${env.BUILD_NUMBER}"
                    sh """
                        sed -i 's|image: ${env.DOCKERHUB_REPO}/adservice:.*|image: ${env.DOCKERHUB_REPO}/adservice:${versionTag}|' deployment-service.yml
                        git config user.email "jenkins@example.com"
                        git config user.name "Jenkins"
                        git add deployment-service.yml
                        git commit -m "Update Docker image to ${env.DOCKERHUB_REPO}/adservice:${versionTag}" || echo "No changes to commit"
                        git push origin Infra-Steps
                    """
                }
            }
        }
    }
}