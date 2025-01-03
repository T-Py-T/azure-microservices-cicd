pipeline {
    agent any

    environment {
        MAJOR_VERSION = '1'
        DOCKERHUB_REPO = 'tnt850910'
        BRANCH = 'cartservice'
        GIT_REPO_URL = 'https://github.com/T-Py-T/eks-jenkins-microservices-cicd'
        VERSION_TAG = "${MAJOR_VERSION}.${BUILD_NUMBER}"
        DOCKER_IMAGE = "${DOCKERHUB_REPO}/${BRANCH}:${VERSION_TAG}"
    }
    tools {
        // Add .NET SDK tool if available in Jenkins
        // dotnet 'dotnet-sdk-3.1' // Example tool name, ensure it matches your Jenkins configuration
    }
    stages {
        // Clean the workspace
        stage('Clean Repo') {steps {deleteDir()}}
        stage('Pull Repo') { steps { withCredentials(
            [usernamePassword(credentialsId: 'git-cred', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
            git branch: "${env.BRANCH}", url: "https://${GIT_USERNAME}:${GIT_PASSWORD}@${env.GIT_REPO_URL.replace('https://', '')}"
        }}}
        stage('Trivy FS Scan') {
            steps {
                script {
                    def trivyOutput = sh(script: "trivy fs --severity HIGH,CRITICAL --format table .", returnStdout: true).trim()
                    println trivyOutput // Display Trivy scan results
                    if (trivyOutput.contains("Total: 0")) { echo "No vulnerabilities found in the Docker image."}
                    else { echo "Vulnerabilities found in the Docker image." }
                }}}
        // stage('.NET Restore') {steps {sh "dotnet restore"}}
        // stage('.NET Build') {steps {sh "dotnet build --configuration Release"}}
        // stage('.NET Test') {steps {sh "dotnet test --configuration Release"}}
        stage('Build & Tag Docker Image') {
            steps {
                script {
                    dir('src') {
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "echo DOCKER_IMAGE: ${env.DOCKER_IMAGE} "
                        sh "docker build -t ${env.DOCKER_IMAGE} ."
                    }}}}}
        stage('Trivy Image Scan') {
            steps {
                script {
                    def trivyOutput = sh(script: "trivy image --severity HIGH,CRITICAL --format table ${env.DOCKER_IMAGE}", returnStdout: true).trim()
                    println trivyOutput // Display Trivy scan results
                    if (trivyOutput.contains("Total: 0")) { echo "No vulnerabilities found in the Docker image."}
                    else { echo "Vulnerabilities found in the Docker image." }
                }}}
        stage('Push Docker Image') {
            steps { 
                script {
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                        sh "docker push ${env.DOCKER_IMAGE}"
                }}}}
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
    }
}
