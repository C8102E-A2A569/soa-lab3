#!/bin/sh
set -e
CONSUL_ADDR="${CONSUL_HTTP_ADDR:-consul:8500}"
TPL="${HAPROXY_TPL:-/tpl/haproxy.cfg.tpl}"
CFG="${HAPROXY_CFG:-/tmp/haproxy.cfg}"
WAIT_MAX="${HAPROXY_WAIT_FOR_SERVICES:-90}"
EXPECTED_INSTANCES="${HAPROXY_EXPECTED_INSTANCES:-4}"

# Ждём, пока в Consul не появятся все ожидаемые инстансы (или таймаут)
echo "Waiting for all ${EXPECTED_INSTANCES} service1-jetty instance(s) in Consul (max ${WAIT_MAX}s)..."
i=0
while [ "$i" -lt "$WAIT_MAX" ]; do
  if consul-template -consul-addr="${CONSUL_ADDR}" -once \
    -template="${TPL}:${CFG}:true" 2>/dev/null; then
    count=$(grep -c "server srv-" "${CFG}" 2>/dev/null || echo 0)
    if [ "$count" -ge "$EXPECTED_INSTANCES" ]; then
      echo "All ${count} service1-jetty instance(s) found in Consul."
      break
    fi
    echo "  ... ${count}/${EXPECTED_INSTANCES} instances (waiting 3s)"
  fi
  i=$((i + 3))
  sleep 3
done
# Если по таймауту нет ни одного инстанса — рендерим с placeholder
count=$(grep -c "server srv-" "${CFG}" 2>/dev/null || echo 0)
if [ "$count" -eq 0 ]; then
  consul-template -consul-addr="${CONSUL_ADDR}" -once -template="${TPL}:${CFG}:true"
  echo "No instances yet; will reload when they register."
fi

haproxy -f "${CFG}" -p /var/run/haproxy.pid &

exec consul-template -consul-addr="${CONSUL_ADDR}" \
  -template="${TPL}:${CFG}:sh /tmp/reload_lf.sh"
