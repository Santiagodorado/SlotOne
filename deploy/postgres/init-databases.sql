-- Scripts en /docker-entrypoint-initdb.d se ejecutan al crear el volumen por primera vez.
CREATE DATABASE slotone_usuarios OWNER slotone;
CREATE DATABASE slotone_negocios OWNER slotone;
CREATE DATABASE slotone_agenda OWNER slotone;
