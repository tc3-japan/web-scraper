#!/usr/bin/env bash

# database user / password
USER="root"
PASS="mypassword"

# root user can only read backup file
umask 077

# retention period(7 days)
PERIOD=7

# backup temporary directory
BACKUP_DIR="/root/mysql/backup"

# backup file name (backup file is compressed by gzip)
BACKUP_FILE="mysqldump_`date +%y%m%d`.sql.gz"

# execute mysqldump
mysqldump --opt --all-databases --events --default-character-set=binary -u ${USER} --password=${PASS} | gzip > ${BACKUP_DIR}/${BACKUP_FILE}

# backup file rolation(delete old backup file)
OLD_BACKUP="mysqldump_`date --date "${PERIOD} days ago" +%y%m%d`.sql.gz"
rm -f ${BACKUP_DIR}/${OLD_BACKUP}
