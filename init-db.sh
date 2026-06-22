#!/bin/bash
set -e

echo "Starting database restore..."

pg_restore --no-owner --no-privileges -d nhahocduong -U nhahocduong /nhahocduong/nhahocduong.dump

echo "Database restore complete!"