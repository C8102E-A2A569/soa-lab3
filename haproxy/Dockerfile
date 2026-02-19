# Бинарник consul-template
ARG CT_VERSION=0.37.0

FROM hashicorp/consul-template:${CT_VERSION} AS consul-template-builder

FROM haproxy:2.8-alpine
COPY --from=consul-template-builder /bin/consul-template /usr/local/bin/consul-template

COPY haproxy.cfg.tpl /tpl/haproxy.cfg.tpl
COPY entrypoint.sh reload-haproxy.sh /tmp/

RUN sed 's/\r$//' /tmp/entrypoint.sh > /tmp/entrypoint_lf.sh && \
    sed 's/\r$//' /tmp/reload-haproxy.sh > /tmp/reload_lf.sh

EXPOSE 8443 8404

ENTRYPOINT ["/bin/sh", "/tmp/entrypoint_lf.sh"]
