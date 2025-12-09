pipeline {
    agent any
    
    tools {
        maven 'Maven 3.9.11'
        jdk 'JDK 25'
    }
    
    environment {
        SPRING_PROFILES_ACTIVE = 'test'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Clonando repositorio...'
                checkout scm
            }
        }
        
        stage('Maven Build') {
            steps {
                echo 'Node.js install, npm install, Tailwind build, compile, test y package'
                sh 'mvn clean package'
            }
        }
        
        stage('Run Tests') {
            steps {
                echo 'Tests ya ejecutados en el stage anterior'
                echo 'Publicando resultados...'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Docker Build') {
            steps {
                echo 'Construyendo la imagen Docker'
                script {
                    def githubUser = "jorgelopezvalle"
                    def repoName = "petlink-plataforma-adopcion-tfg"
                    
                    def image = "ghcr.io/${githubUser}/${repoName}:${env.BUILD_NUMBER}"
                    
                    sh "docker build -t ${image} ."
                    
                    env.IMAGE_NAME = image
                }
            }
        }

        stage('GitHub Push (GHCR)') {
            steps {
                echo 'Subiendo la imagen a GitHub Container Registry'
                withCredentials([usernamePassword(credentialsId: 'github-credentials', passwordVariable: 'GHCR_TOKEN', usernameVariable: 'GHCR_USER')]) {
                    script {
                        sh "echo \$GHCR_TOKEN | docker login ghcr.io -u \$GHCR_USER --password-stdin"

                        sh "docker push ${env.IMAGE_NAME}"

                        if (env.BRANCH_NAME == 'main') {
                            def latestImage = env.IMAGE_NAME.tokenize(':')[0] + ":latest"
                            sh "docker tag ${env.IMAGE_NAME} ${latestImage}"
                            sh "docker push ${latestImage}"
                            echo "Imagen también etiquetada como ${latestImage}"
                        }
                        sh "docker logout ghcr.io"
                    }
                }
            }
        }

        stage('Deploy to Production') {
            steps {
                echo "Desplegando ${env.IMAGE_NAME} en producción"

                withCredentials([
                    usernamePassword(credentialsId: 'github-credentials', passwordVariable: 'GHCR_TOKEN', usernameVariable: 'GHCR_USER'),
                    string(credentialsId: 'petlink-db-url', variable: 'DB_URL'),
                    usernamePassword(credentialsId: 'petlink-db-credentials', passwordVariable: 'DB_PASSWORD', usernameVariable: 'DB_USERNAME'),
                    string(credentialsId: 'petlink-session-timeout', variable: 'SESSION_TIMEOUT'),
                    string(credentialsId: 'petlink-minio-endpoint', variable: 'MINIO_ENDPOINT'),
                    string(credentialsId: 'petlink-minio-public-url', variable: 'MINIO_PUBLIC_URL'),
                    usernamePassword(credentialsId: 'petlink-minio-credentials', passwordVariable: 'MINIO_SECRET_KEY', usernameVariable: 'MINIO_ACCESS_KEY'),
                    string(credentialsId: 'petlink-minio-bucket', variable: 'MINIO_BUCKET'),
                    string(credentialsId: 'petlink-mail-host', variable: 'MAIL_HOST'),
                    string(credentialsId: 'petlink-mail-port', variable: 'MAIL_PORT'),
                    usernamePassword(credentialsId: 'petlink-mail-credentials', passwordVariable: 'MAIL_PASSWORD', usernameVariable: 'MAIL_USERNAME')
                ]) {
                    script {
                        def containerName = "petlink-prod"
                        def port = "8082"

                        echo "Deteniendo y eliminando contenedor anterior si existe"
                        sh "docker stop ${containerName} || true"
                        sh "docker rm ${containerName} || true"

                        echo "Autenticando con GHCR"
                        sh "echo \$GHCR_TOKEN | docker login ghcr.io -u \$GHCR_USER --password-stdin"

                        echo "Descargando la última imagen desde GHCR"
                        sh "docker pull ${env.IMAGE_NAME}"

                        echo "Iniciando nuevo contenedor con variables de entorno"
                        sh """
                            docker run -d \
                            --name ${containerName} \
                            -p ${port}:8080 \
                            -e DB_URL="\${DB_URL}" \
                            -e DB_USERNAME="\${DB_USERNAME}" \
                            -e DB_PASSWORD="\${DB_PASSWORD}" \
                            -e SESSION_TIMEOUT="\${SESSION_TIMEOUT}" \
                            -e MINIO_ENDPOINT="\${MINIO_ENDPOINT}" \
                            -e MINIO_PUBLIC_URL="\${MINIO_PUBLIC_URL}" \
                            -e MINIO_ACCESS_KEY="\${MINIO_ACCESS_KEY}" \
                            -e MINIO_SECRET_KEY="\${MINIO_SECRET_KEY}" \
                            -e MINIO_BUCKET="\${MINIO_BUCKET}" \
                            -e MAIL_HOST="\${MAIL_HOST}" \
                            -e MAIL_PORT="\${MAIL_PORT}" \
                            -e MAIL_USERNAME="\${MAIL_USERNAME}" \
                            -e MAIL_PASSWORD="\${MAIL_PASSWORD}" \
                            --restart unless-stopped \
                            ${env.IMAGE_NAME}
                        """

                        echo "Contenedor ${containerName} desplegado en puerto ${port}"

                        sh "docker logout ghcr.io"

                        sh "docker image prune -f"
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline ejecutado exitosamente'
        }
        failure {
            echo 'Pipeline fallido'
        }
        always {
            echo 'Limpiando workspace...'
            deleteDir()
        }
    }
}