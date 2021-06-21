
### Como executar:

Com o Docker e o docker-compose instalado na sua máquina, acessar pelo terminal a pasta `shedlock` e digitar os seguintes comandos :

#### Windows :

```
.\gradlew build -x test
docker-compose up -d 
```

#### Linux :

```
chmod +x ./gradlew
./gradlew build -x test
docker-compose up -d 
```