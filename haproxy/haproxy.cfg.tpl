global
    log stdout format raw local0
    maxconn 4096

defaults
    log     global
    mode    http
    option  httplog
    option  dontlognull
    timeout connect 5000ms
    timeout client  50000ms
    timeout server  50000ms

frontend service1_frontend
    bind *:8443 ssl crt /usr/local/etc/haproxy/certs/service1.pem
    mode http
    default_backend service1_backend

    http-response set-header Access-Control-Allow-Origin "*"
    http-response set-header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS"
    http-response set-header Access-Control-Allow-Headers "*"

backend service1_backend
    mode http
    balance roundrobin
    option httpchk
    http-check send meth GET uri /actuator/health ver HTTP/1.1 hdr Host localhost
    timeout connect 5000
{{ range service "service1-jetty" }}
    server srv-{{ .Port }} {{ .Address }}:{{ .Port }} check ssl verify none inter 15s
{{ else }}
    server placeholder 127.0.0.1:1 backup
{{ end }}

listen stats
    bind *:8404
    mode http
    stats enable
    stats uri /stats
    stats refresh 10s
    stats admin if TRUE
