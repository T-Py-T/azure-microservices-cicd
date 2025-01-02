pipeline {
    agent any

    environment {
        MAJOR_VERSION = '1'
        DOCKERHUB_REPO = 'tnt850910'
        BRANCH = 'adservice'
        GIT_REPO_URL = 'https://github.com/T-Py-T/eks-jenkins-microservices-cicd'
        VERSION_TAG = "${MAJOR_VERSION}.${BUILD_NUMBER}"
        DOCKER_IMAGE = "${DOCKERHUB_REPO}/${BRANCH}:${VERSION_TAG}"
    }
    tools {
        gradle 'gradle8'
        jdk 'jdk19'
    }
    stages {
        // Clean the workspace
        stage('Clean Repo') {steps {deleteDir()}}
        stage('Pull Repo') { steps { withCredentials(
            [usernamePassword(credentialsId: 'git-cred', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
            git branch: "${env.BRANCH}", url: "https://${GIT_USERNAME}:${GIT_PASSWORD}@${env.GIT_REPO_URL.replace('https://', '')}"
        }}}
        stage('Gradle Compile') { 
            steps {  
                sh "chmod +x ./gradlew"
                sh "./gradlew compileJava" 
            }}
        // stage('Format Code') {steps {sh "./gradlew googleJavaFormat"}} // FORMATTING NOT WORKING (GOOGLE FORMAT FAILS)
        stage('Gradle Build') {steps {sh "./gradlew build"}}
        // stage('Gradle Test') {steps {sh "./gradlew test"}} // There are no tests in the java branch currently 
        stage('Trivy FS Scan') {steps {sh "trivy fs --severity HIGH,CRITICAL --format table -o fs.html . | tee fs.html"}}       
        stage('Build & Tag Docker Image') { 
            steps {script { 
                withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                    sh "echo DOCKER_IMAGE: ${env.DOCKER_IMAGE} "
                    sh "docker build -t ${env.DOCKER_IMAGE} ."
                }}}}
        stage('Docker Image Scan') { 
            steps {script {
                sh "trivy image --severity HIGH,CRITICAL --format table -o trivy-image-report.html ${env.DOCKER_IMAGE} | tee trivy-image-report.html" 
                }}}       
        stage('Push Docker Image') {
            steps { 
                script {
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker push ${env.DOCKER_IMAGE}"
                }}}}

        // stage('Scan Docker Image') {
        //     steps {
        //         script {
        //             def trivyOutput = sh(script: "trivy image $APP_NAME:latest", returnStdout: true).trim()
        //             println trivyOutput // Display Trivy scan results
        //             if (trivyOutput.contains("Total: 0")) { echo "No vulnerabilities found in the Docker image."}
        //             else { echo "Vulnerabilities found in the Docker image." }
        //         }}}
        
        stage('Clean Workspace') {steps {deleteDir()}}
        stage('Pull Infra-Steps Repo') { 
            steps { 
                withCredentials([usernamePassword(credentialsId: 'git-cred', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                    git branch: 'Infra-Steps', url: "https://${GIT_USERNAME}:${GIT_PASSWORD}@${env.GIT_REPO_URL.replace('https://', '')}"
                }}}
        stage('Update and Commit Deployment YAML') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'git-cred', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                    script {
                        sh """
                            sed -i 's|image: ${env.DOCKERHUB_REPO}/adservice:.*|image: ${env.DOCKER_IMAGE} |' deployment-service.yml
                            git add deployment-service.yml
                            git commit -m "Update Docker image to ${env.DOCKER_IMAGE}" || echo "No changes to commit"
                            git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${env.GIT_REPO_URL.replace('https://', '')} Infra-Steps
                        """
                    }}}}

        stage('Create Pull Request') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'git-cred', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                    script {
                        sh """
                            export GITHUB_TOKEN=${GIT_PASSWORD}
                            gh auth login --with-token
                            gh pr create --title "Update Docker image to ${env.DOCKERHUB_REPO}/adservice:${env.VERSION_TAG}" --body "This PR updates the Docker image to ${env.DOCKER_IMAGE}" --base main --head Infra-Steps
                        """
                    }}}}
    }
}
