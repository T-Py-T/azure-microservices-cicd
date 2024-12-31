pipeline {
    agent any

    stages {
        stage('Pull Repo') { steps { git branch: 'main', credentialsId: 'git-cred', url: '{repo}' } }
        
        stage('Compile') { steps {  sh  "mvn compile" } }
        
        stage('Test') { steps { sh "mvn test" } }
        
        stage('Trivy FS Scan') { steps { sh "trivy fs --format table -o fs.html ."} }       
        
        stage('Build') { steps { sh "mvn package" } }
        
        stage('Build & Tag Docker Image') { 
            steps { script { withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                sh "docker build -t tnt850910/adservice:latest ."
            } } } }

        stage('Docker Image Scan') { 
            steps { 
                sh "trivy image --format table -o trivy-image-report.html tnt850910/adservice:latest " 
            } }
        
        stage('Push Docker Image') {
            steps {script { withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                            sh "docker push tnt850910/adservice:latest" } } } }
    }
}
