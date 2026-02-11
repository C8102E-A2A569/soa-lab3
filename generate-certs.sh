#!/bin/bash

# Создаем директорию для сертификатов
mkdir -p haproxy/certs

# Генерируем приватный ключ
openssl genrsa -out haproxy/certs/service1.key 2048

# Генерируем самоподписанный сертификат
openssl req -new -x509 -key haproxy/certs/service1.key -out haproxy/certs/service1.crt -days 365 \
  -subj "//C=RU\ST=SPb\L=SaintPetersburg\O=ITMO\CN=localhost"

# Объединяем ключ и сертификат в один PEM файл для HAProxy
cat haproxy/certs/service1.crt haproxy/certs/service1.key > haproxy/certs/service1.pem

echo "SSL certificates generated successfully!"