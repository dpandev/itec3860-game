#!/usr/bin/env bash
set -euo pipefail
git config core.hooksPath .githooks
chmod +x .githooks/pre-commit || true
echo "core.hooksPath set to .githooks"
