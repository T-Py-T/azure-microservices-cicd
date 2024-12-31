pipeline {
    agent any

    environment {
        MAJOR_VERSION = '1' // Define the major version here or in the Jenkins job configuration
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
                git branch: 'adservice', credentialsId: 'git-cred', url: 'https://github.com/T-Py-T/eks-jenkins-microservices-cicd' 
            } 
        }
        
        stage('Compile') { 
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
        
        stage('Build') { 
            steps {  
                sh "./gradlew build" 
            } 
        }

        // There are no tests in the java branch currently        
        // stage('Test') { 
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
                        sh "docker build -t tnt850910/adservice:${versionTag} ."
                    } 
                } 
            } 
        }

        stage('Docker Image Scan') { 
            steps { 
                script {
                    def versionTag = "${env.MAJOR_VERSION}.${env.BUILD_NUMBER}"
                    sh "trivy image --format table -o trivy-image-report.html tnt850910/adservice:${versionTag}" 
                }
            } 
        }
        
        stage('Push Docker Image') {
            steps { 
                script { 
                    def versionTag = "${env.MAJOR_VERSION}.${env.BUILD_NUMBER}"
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker push tnt850910/adservice:${versionTag}" 
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
                git branch: 'infra-steps', credentialsId: 'git-cred', url: 'https://github.com/T-Py-T/eks-jenkins-microservices-cicd' 
            } 
        }

        stage('Update Deployment YAML') {
            steps {
                script {
                    def versionTag = "${env.MAJOR_VERSION}.${env.BUILD_NUMBER}"
                    sh """
                        sed -i 's|image: tnt850910/adservice:.*|image: tnt850910/adservice:${versionTag}|' deployment.yaml
                    """
                }
            }
        }

        stage('Commit Changes') {
            steps {
                script {
                    def versionTag = "${env.MAJOR_VERSION}.${env.BUILD_NUMBER}"
                    sh """
                        git config user.email "you@example.com"
                        git config user.name "Your Name"
                        git add deployment.yaml
                        git commit -m "Update Docker image to tnt850910/adservice:${versionTag}"
                        git push origin infra-steps
                    """
                }
            }
        }
    }
}