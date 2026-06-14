#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME="${MONGO_CONTAINER_NAME:-mongo-demo-db}"
MONGO_IMAGE="${MONGO_IMAGE:-mongo:7.0}"
HOST_PORT="${MONGO_PORT:-27017}"
DB_NAME="${MONGO_DB_NAME:-mongo_demo}"
COLLECTION_NAME="${MONGO_COLLECTION_NAME:-products}"
DEFAULT_SEED_COUNT="${DEFAULT_SEED_COUNT:-1500}"

usage() {
  cat <<'EOF'
Usage:
  ./scripts/mongo-data.sh up
  ./scripts/mongo-data.sh seed [count]
  ./scripts/mongo-data.sh up-seed [count]
  ./scripts/mongo-data.sh reset
  ./scripts/mongo-data.sh down
  ./scripts/mongo-data.sh status

Notes:
  - Default seed count is 1500 records.
  - You can override settings with env vars:
      MONGO_CONTAINER_NAME, MONGO_IMAGE, MONGO_PORT,
      MONGO_DB_NAME, MONGO_COLLECTION_NAME, DEFAULT_SEED_COUNT
EOF
}

log() {
  printf '[mongo-data] %s\n' "$1"
}

fail() {
  printf '[mongo-data] ERROR: %s\n' "$1" >&2
  exit 1
}

require_docker() {
  command -v docker >/dev/null 2>&1 || fail "docker is required but not installed"
}

container_exists() {
  docker ps -a --format '{{.Names}}' | grep -Fxq "$CONTAINER_NAME"
}

container_running() {
  docker ps --format '{{.Names}}' | grep -Fxq "$CONTAINER_NAME"
}

wait_for_mongo() {
  local retries=45
  local i
  for i in $(seq 1 "$retries"); do
    if docker exec "$CONTAINER_NAME" mongosh --quiet --eval "db.adminCommand({ ping: 1 }).ok" >/dev/null 2>&1; then
      return 0
    fi
    sleep 1
  done
  fail "MongoDB container did not become ready in time"
}

ensure_container_up() {
  if container_running; then
    log "Container '$CONTAINER_NAME' is already running"
    return
  fi

  if container_exists; then
    log "Starting existing container '$CONTAINER_NAME'"
    docker start "$CONTAINER_NAME" >/dev/null
  else
    log "Creating and starting container '$CONTAINER_NAME' on port $HOST_PORT"
    docker run -d \
      --name "$CONTAINER_NAME" \
      -p "$HOST_PORT:27017" \
      -e MONGO_INITDB_DATABASE="$DB_NAME" \
      "$MONGO_IMAGE" >/dev/null
  fi

  wait_for_mongo
  log "MongoDB is ready"
}

validate_count() {
  local value="$1"
  [[ "$value" =~ ^[0-9]+$ ]] || fail "seed count must be a positive integer"
  (( value > 0 )) || fail "seed count must be greater than 0"

  if (( value < 1000 || value > 2000 )); then
    log "Warning: requested count $value is outside the suggested 1000-2000 range"
  fi
}

seed_products() {
  local count="${1:-$DEFAULT_SEED_COUNT}"
  validate_count "$count"

  ensure_container_up

  log "Seeding $count products into '$DB_NAME.$COLLECTION_NAME'"
  docker exec "$CONTAINER_NAME" mongosh --quiet --eval "
const dbName = '$DB_NAME';
const collectionName = '$COLLECTION_NAME';
const count = $count;
const categories = ['Electronics', 'Books', 'Home', 'Sports', 'Fashion', 'Toys'];
const database = db.getSiblingDB(dbName);
const collection = database.getCollection(collectionName);

const docs = [];
for (let i = 0; i < count; i++) {
  const category = categories[Math.floor(Math.random() * categories.length)];
  docs.push({
    name: 'Product-' + Date.now().toString(36) + '-' + i,
    category: category,
    availableStock: Math.floor(Math.random() * 5000),
    unitsSold: Math.floor(Math.random() * 2000)
  });
}

collection.insertMany(docs, { ordered: false });
printjson({ inserted: count, database: dbName, collection: collectionName });
"
}

reset_products() {
  ensure_container_up
  log "Dropping collection '$DB_NAME.$COLLECTION_NAME'"
  docker exec "$CONTAINER_NAME" mongosh --quiet --eval "
const database = db.getSiblingDB('$DB_NAME');
database.getCollection('$COLLECTION_NAME').drop();
printjson({ dropped: '$DB_NAME.$COLLECTION_NAME' });
"
}

show_status() {
  if container_running; then
    log "Container '$CONTAINER_NAME' is running"
    docker ps --filter "name=^${CONTAINER_NAME}$" --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'
  elif container_exists; then
    log "Container '$CONTAINER_NAME' exists but is stopped"
    docker ps -a --filter "name=^${CONTAINER_NAME}$" --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}'
  else
    log "Container '$CONTAINER_NAME' does not exist"
  fi
}

down_container() {
  if container_exists; then
    log "Stopping and removing container '$CONTAINER_NAME'"
    docker rm -f "$CONTAINER_NAME" >/dev/null
  else
    log "No container named '$CONTAINER_NAME' to remove"
  fi
}

main() {
  local command="${1:-up-seed}"
  local count="${2:-$DEFAULT_SEED_COUNT}"

  case "$command" in
    up)
      require_docker
      ensure_container_up
      ;;
    seed)
      require_docker
      seed_products "$count"
      ;;
    up-seed)
      require_docker
      seed_products "$count"
      ;;
    reset)
      require_docker
      reset_products
      ;;
    down)
      require_docker
      down_container
      ;;
    status)
      require_docker
      show_status
      ;;
    -h|--help|help)
      usage
      ;;
    *)
      usage
      fail "unknown command: $command"
      ;;
  esac
}

main "$@"

