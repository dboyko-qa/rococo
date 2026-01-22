[English version](README.en.md)
# Проект Rococo

## О проекте

Этот проект — production-like backend-система, разработанная с нуля для готового frontend-приложения.  
Он демонстрирует полный цикл работы с backend-частью сервиса: от проектирования и реализации до комплексного 
автоматизированного тестирования.

Проект фокусируется на качестве и надёжности. В нём реализовано многоуровневое тестовое покрытие, которое позволяет 
проверять систему на разных уровнях и уверенно выявлять ошибки на ранних этапах.

Репозиторий будет полезен как пример backend-проекта с продуманной архитектурой и полноценной стратегией тестирования, 
приближенной к реальным продуктовым задачам.

## Схема проекта Rococo

<img src="rococo_scheme_dark.png" width="600">

## Architecture

Backend построен по **микросервисной архитектуре** и состоит из независимых сервисов с чётко разделёнными зонами ответственности.  
В системе предусмотрены две точки входа, каждая из которых обслуживает отдельный поток запросов.

Все сервисы реализованы на **Java 21** с использованием **Spring Boot** и работают с базой данных **PostgreSQL**.

---

### Gateway

**Gateway** является основной точкой входа для frontend-приложения.  
Frontend взаимодействует с gateway по **REST**, а gateway маршрутизирует запросы во внутренние сервисы и выступает в роли оркестратора.

Связь между gateway и внутренними микросервисами реализована по протоколу **gRPC**.  
Все внутренние сервисы доступны только через gateway и не имеют прямого внешнего доступа.

---

### Auth Service

**Auth service** — отдельный микросервис, отвечающий за аутентификацию и авторизацию пользователей.  
Он не участвует в общем потоке запросов, проходящих через gateway, и используется как самостоятельная точка входа.

При регистрации пользователя Auth service передаёт данные в **Userdata service** асинхронно через брокер сообщений **Kafka**.

---

### Service Communication

Внутренние сервисы не взаимодействуют напрямую с внешними клиентами.  
**Auth service** обменивается данными с **Userdata service** через **Kafka**, что снижает связанность сервисов и повышает устойчивость системы.

---

### Architectural Focus

Архитектура проекта ориентирована на простоту поддержки, масштабируемость, тестируемость и изоляцию ключевых функциональных зон.


## Testing

В проекте используются **юнит-, интеграционные и end-to-end тесты**.

---

### Юнит- и интеграционные тесты

Юнит- и интеграционные тесты находятся непосредственно в микросервисах.

**Юнит-тесты** используют **JUnit 5** и **Mockito** и проверяют бизнес-логику сервисов в изоляции от внешних зависимостей.

**Интеграционные тесты** используют **WireMock** для стабилизации внешних интеграций и **in-memory базу данных H2**.  
Они проверяют взаимодействие компонентов внутри сервиса и корректность работы с данными.

---

### End-to-end тесты

End-to-end тесты вынесены в отдельный подпроект и поддерживают параллельный запуск с изоляцией потоков и данных.

- **API-тесты** реализованы с использованием **RestAssured** и проверяют ключевые бизнес-сценарии и права доступа.
- **UI-тесты** написаны с использованием **Selenide** и выполняют проверки через браузер **Chrome**.

Для всех e2e тестов применяются механизмы изоляции данных и сессий, чтобы тесты могли запускаться параллельно без конфликтов.

---

### Подготовка тестовых данных

Подготовка тестовых данных и управление состоянием тестов реализованы через кастомные **JUnit 5 экстеншены**.  
В проекте используются экстеншены для:

- выбора пользователя,
- автоматического логина через API,
- генерации тестовых данных и передачи их в тесты через параметры.

---

### Запуск и отчёты

Тесты запускаются локально. Планируется запуск тестов в **Docker-контейнерах** и интеграция с **GitHub Actions** для автоматического выполнения в CI.

Для анализа результатов тестирования и удобной навигации по сценариям используются отчёты **Allure**.



# Настройка проекта
#### 1. Установить docker (Если не установлен)
#### 2. Спуллить контейнер postgres:15.1, zookeeper и kafka версии 7.3.2

```posh
docker pull postgres:15.1
docker pull confluentinc/cp-zookeeper:7.3.2
docker pull confluentinc/cp-kafka:7.3.2
```

После `pull` вы увидите спуленный image командой `docker images`

```posh
MacBook-Pro ~ % docker images            
REPOSITORY                 TAG              IMAGE ID       CREATED         SIZE
postgres                   15.1             9f3ec01f884d   10 days ago     379MB
confluentinc/cp-kafka      7.3.2            db97697f6e28   12 months ago   457MB
confluentinc/cp-zookeeper  7.3.2            6fe5551964f5   7 years ago     451MB

```

#### 3. Создать volume для сохранения данных из БД в docker на вашем компьютере

```posh
docker volume create pgdata
```

#### 4. Запустить БД, zookeeper и kafka 3-мя последовательными командами:

Запустив скрипт (Для Windows необходимо использовать bash terminal: gitbash, cygwin или wsl)

```posh
User-MacBook-Pro  rococo % bash localenv.sh
```

Или выполнив последовательно команды, для *nix:

```posh
docker run --name rococo-all -p 5432:5432 -e POSTGRES_PASSWORD=secret -v pgdata:/var/lib/postgresql/data -v ./postgres/script:/docker-entrypoint-initdb.d -e CREATE_DATABASES=rococo-auth,rococo-artist,rococo-geo,rococo-museum,rococo-painting,rococo-userdata -e TZ=GMT+3 -e PGTZ=GMT+3 -d postgres:15.1 --max_prepared_transactions=100

docker run --name=zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 -p 2181:2181 -d confluentinc/cp-zookeeper:7.3.2

docker run --name=kafka -e KAFKA_BROKER_ID=1 \
-e KAFKA_ZOOKEEPER_CONNECT=$(docker inspect zookeeper --format='{{ .NetworkSettings.IPAddress }}'):2181 \
-e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
-e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 \
-e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
-p 9092:9092 -d confluentinc/cp-kafka:7.3.2
```

Для Windows (Необходимо использовать bash terminal: gitbash, cygwin или wsl):

```posh
docker run --name rococo-all -p 5432:5432 -e POSTGRES_PASSWORD=secret -e CREATE_DATABASES=rococo-auth,rococo-artist,rococo-geo,rococo-museum,rococo-painting,rococo-userdata -e TZ=GMT+3 -e PGTZ=GMT+3 -v pgdata:/var/lib/postgresql/data -v ./postgres/script:/docker-entrypoint-initdb.d -d postgres:15.1 --max_prepared_transactions=100

docker run --name=zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 -p 2181:2181 -d confluentinc/cp-zookeeper:7.3.2

docker run --name=kafka -e KAFKA_BROKER_ID=1 -e KAFKA_ZOOKEEPER_CONNECT=$(docker inspect zookeeper --format="{{ .NetworkSettings.IPAddress }}"):2181 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 -e KAFKA_TRANSACTION_STATE_LOG_MIN_ISR=1 -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 -p 9092:9092 -d confluentinc/cp-kafka:7.3.2
```


Если вы используете Windows и контейнер с БД не стартует с ошибкой в логе:

```
server started
/usr/local/bin/docker-entrypoint.sh: running /docker-entrypoint-initdb.d/init-database.sh
/usr/local/bin/docker-entrypoint.sh: /docker-entrypoint-initdb.d/init-database.sh: /bin/bash^M: bad interpreter: No such file or directory
```

То необходимо выполнить следующие команды в каталоге /postgres/script :

```
sed -i -e 's/\r$//' init-database.sh
chmod +x init-database.sh
```

необходимо проверить, было ли сообщение об автоматическом создании баз данныхз в логе контейнера с Postgres (rococo-all):
```posh
docker logs -f rococo-all
... 
Multiple database creation requested: rococo-auth,rococo-artist,rococo-geo,rococo-museum,rococo-painting,rococo-userdata"
...
```
Если сообщения нет, то необходимо создать базы данных вручную (при этом, мы создаем только пустые БД, без таблиц):
- Установить одну из программ для визуальной работы с Postgres. Например, PgAdmin, DBeaver или Datagrip.
- Подключиться к БД postgres (host: localhost, port: 5432, user: postgres, pass: secret, database name: postgres) из PgAdmin и создать пустые БД микросервисов
```sql
   create database "rococo-auth" with owner postgres;
   create database "rococo-artist" with owner postgres;
   create database "rococo-geo" with owner postgres;
   create database "rococo-museum" with owner postgres;
   create database "rococo-painting" with owner postgres;
   create database "rococo-userdata" with owner postgres;
```

#### 5. Установить Java версии 21. 
#### 6. Установить пакетый менеджер для сборки front-end npm


# Запуск Rococo локальное в IDE:

#### 1. Запусти фронт Rococо:

```posh
User-MacBook-Pro rococo % cd rococo-client
User-MacBook-Pro rococo-client % npm i
User-MacBook-Pro rococo-client % npm run dev
```

  Фронт стартанет на порту 3000: http://127.0.0.1:3000/

#### 2. Прописать run конфигурацию для всех сервисов rococo-* - Active profiles local

Для этого зайти в меню Run -> Edit Configurations -> выбрать main класс -> указать Active profiles: local

#### 4 Запустить сервис Rococo-auth c помощью gradle или командой Run в IDE:

- Запустить сервис auth

```posh
User-MacBook-Pro rococo % cd rococo-auth
User-MacBook-Pro rococo-auth % gradle bootRun --args='--spring.profiles.active=local'
```

Или просто перейдя к main-классу приложения RococoAuthApplication выбрать run в IDEA (предварительно удостовериться что
выполнен предыдущий пункт)

#### 5  Запустить в любой последовательности другие сервисы: rococo-artist,rococo-geo,rococo-museum,rococo-painting,rococo-userdata

Фронтенд Rococo при запуске локально будет работать для вас по адресу http://127.0.0.1:3000/,


