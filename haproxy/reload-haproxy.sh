#!/bin/sh
CFG="${HAPROXY_CFG:-/tmp/haproxy.cfg}"
if [ -f /var/run/haproxy.pid ]; then
  haproxy -f "$CFG" -sf $(cat /var/run/haproxy.pid) 2>/dev/null || true
fi
