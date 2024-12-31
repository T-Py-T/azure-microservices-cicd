pipeline {
    agent any

    tools {
        gradle 'gradle8' // Ensure 'gradle6' is the name of the Gradle installation in Jenkins
        jdk 'jdk17'      // Ensure 'jdk17' is the name of the JDK installation in Jenkins
    }

    stages {
        stage('Pull Repo') { 
            steps { 
                git branch: 'adservice', credentialsId: 'git-cred', url: 'https://github.com/T-Py-T/eks-jenkins-microservices-cicd' 
            } 
        }
        
        stage('Compile') { 
            steps {  
                dir('path/to/your/gradle/project') { // Change to the directory containing the build.gradle file
                    sh "chmod +x ./gradlew"
                    sh "./gradlew compileJava" 
                }
            } 
        }
        
        stage('Build') { 
            steps {  
                dir('path/to/your/gradle/project') { // Change to the directory containing the build.gradle file
                    sh "./gradlew build" 
                }
            } 
        }
        
        stage('Test') { 
            steps { 
                dir('path/to/your/gradle/project') { // Change to the directory containing the build.gradle file
                    sh "./gradlew test" 
                }
            } 
        }
        
        stage('Trivy FS Scan') { 
            steps { 
                sh "trivy fs --format table -o fs.html ." 
            } 
        }       
        
        stage('Build & Tag Docker Image') { 
            steps { 
                script { 
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker build -t tnt850910/adservice:latest ."
                    } 
                } 
            } 
        }

        stage('Docker Image Scan') { 
            steps { 
                sh "trivy image --format table -o trivy-image-report.html tnt850910/adservice:latest" 
            } 
        }
        
        stage('Push Docker Image') {
            steps { 
                script { 
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker push tnt850910/adservice:latest" 
                    } 
                } 
            } 
        }
    }
}
