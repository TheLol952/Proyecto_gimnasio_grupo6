#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

if [[ -f "$PROJECT_DIR/.env" ]]; then
  set -a
  # shellcheck disable=SC1091
  source "$PROJECT_DIR/.env"
  set +a
fi

DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-5432}"
DB_NAME="${DB_NAME:-gimnasio}"
DB_USER="${DB_USER:-gimnasio_app}"

required_variables=(
  DB_PASSWORD
  DEMO_ADMIN_USER DEMO_ADMIN_PASSWORD
  DEMO_RECEPTION_USER DEMO_RECEPTION_PASSWORD
  DEMO_TRAINER_USER DEMO_TRAINER_PASSWORD
)
for variable in "${required_variables[@]}"; do
  [[ -n "${!variable:-}" ]] || {
    echo "Error: falta $variable en .env o en el ambiente." >&2
    exit 1
  }
done

command -v psql >/dev/null 2>&1 || {
  echo "Error: psql no esta instalado o no se encuentra en PATH." >&2
  exit 1
}

export PGPASSWORD="$DB_PASSWORD"

psql -v ON_ERROR_STOP=1 -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
  -f "$PROJECT_DIR/database/schema.sql"
psql -v ON_ERROR_STOP=1 \
  -v "DEMO_ADMIN_USER=$DEMO_ADMIN_USER" \
  -v "DEMO_ADMIN_PASSWORD=$DEMO_ADMIN_PASSWORD" \
  -v "DEMO_RECEPTION_USER=$DEMO_RECEPTION_USER" \
  -v "DEMO_RECEPTION_PASSWORD=$DEMO_RECEPTION_PASSWORD" \
  -v "DEMO_TRAINER_USER=$DEMO_TRAINER_USER" \
  -v "DEMO_TRAINER_PASSWORD=$DEMO_TRAINER_PASSWORD" \
  -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
  -f "$PROJECT_DIR/database/data.sql"

echo "Base de datos preparada correctamente."
