mvn clean package -DskipTests=true -B -e -U
proxychains4 docker build -f Dockerfile -t pinduoduo .
docker run -d -p 9999:9999 pinduoduo
#docker tag devops harbor.yj2025.com/library/devops
#docker push harbor.yj2025.com/library/devops
