CREATE TABLE IF NOT EXISTS user
(
    uuid       BINARY(16) NOT NULL,
    name       TINYTEXT   NOT NULL,
    discord_id BIGINT     NULL,

    CONSTRAINT user_uuid_pk PRIMARY KEY (uuid)
);

CREATE TABLE IF NOT EXISTS user_meta
(
    user_uuid   BINARY(16)                           NOT NULL,
    language    TINYINT  DEFAULT 1                   NOT NULL,
    online_time BIGINT   DEFAULT 0                   NOT NULL,
    last_seen   DATETIME DEFAULT current_timestamp() NOT NULL,
    create_date DATETIME DEFAULT current_timestamp() NOT NULL,

    CONSTRAINT user_meta_uuid_fk UNIQUE (user_uuid),
    CONSTRAINT user_meta_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_currency
(
    user_uuid BINARY(16)       NOT NULL,
    amethysts BIGINT DEFAULT 0 NOT NULL,
    shards    BIGINT DEFAULT 0 NOT NULL,

    CONSTRAINT user_currency_uuid_fk UNIQUE (user_uuid),
    CONSTRAINT user_currency_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS currency_transactions
(
    id          BIGINT AUTO_INCREMENT                NOT NULL,
    user_uuid   BINARY(16)                           NULL,
    target_uuid BINARY(16)                           NULL,
    amethysts   BIGINT   DEFAULT 0                   NOT NULL,
    shards      BIGINT   DEFAULT 0                   NOT NULL,
    create_date DATETIME DEFAULT current_timestamp() NOT NULL,

    CONSTRAINT currency_transactions_id_pk PRIMARY KEY (id),
    CONSTRAINT currency_transactions_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT currency_transactions_target_uuid_fk FOREIGN KEY (target_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_setting
(
    user_uuid BINARY(16)                                          NOT NULL,
    msg       SET ('ALL', 'CLANMEMBERS', 'FRIENDS') DEFAULT 'ALL' NOT NULL,
    friend    SET ('ALL', 'CLANMEMBERS')            DEFAULT 'ALL' NOT NULL,
    party     SET ('ALL', 'CLANMEMBERS', 'FRIENDS') DEFAULT 'ALL' NOT NULL,
    clan      SET ('ALL', 'FRIENDS')                DEFAULT 'ALL' NOT NULL,

    CONSTRAINT user_setting_uuid_fk UNIQUE (user_uuid),
    CONSTRAINT user_setting_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS report
(
    id            INT AUTO_INCREMENT                   NOT NULL,
    user_uuid     BINARY(16)                           NOT NULL,
    executed_uuid BINARY(16)                           NOT NULL,
    claimed_uuid  BINARY(16)                           NULL,
    reason_id     TINYINT                              NOT NULL,
    claimed_date  DATETIME                             NULL,
    create_date   DATETIME DEFAULT current_timestamp() NOT NULL,

    CONSTRAINT report_id_pk PRIMARY KEY (id),
    CONSTRAINT report_reported_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT report_executed_user_uuid_fk FOREIGN KEY (executed_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT report_claimed_user_uuid_fk FOREIGN KEY (claimed_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS warn
(
    id            INT AUTO_INCREMENT                   NOT NULL,
    user_uuid     BINARY(16)                           NOT NULL,
    executed_uuid BINARY(16)                           NOT NULL,
    canceled_uuid BINARY(16)                           NULL,
    reason_id     TINYINT                              NOT NULL,
    canceled_date DATETIME                             NULL,
    create_date   DATETIME DEFAULT current_timestamp() NOT NULL,

    CONSTRAINT warn_id_pk PRIMARY KEY (id),
    CONSTRAINT warn_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT warn_executed_user_uuid_fk FOREIGN KEY (executed_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT warn_canceled_user_uuid_fk FOREIGN KEY (canceled_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS kick
(
    id            INT AUTO_INCREMENT                   NOT NULL,
    user_uuid     BINARY(16)                           NOT NULL,
    executed_uuid BINARY(16)                           NOT NULL,
    canceled_uuid BINARY(16)                           NULL,
    reason_id     TINYINT                              NOT NULL,
    canceled_date DATETIME                             NULL,
    create_date   DATETIME DEFAULT current_timestamp() NOT NULL,

    CONSTRAINT kick_id_pk PRIMARY KEY (id),
    CONSTRAINT kick_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT kick_executed_user_uuid_fk FOREIGN KEY (executed_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT kick_canceled_user_uuid_fk FOREIGN KEY (canceled_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mute
(
    id            INT AUTO_INCREMENT                   NOT NULL,
    user_uuid     BINARY(16)                           NOT NULL,
    executed_uuid BINARY(16)                           NOT NULL,
    canceled_uuid BINARY(16)                           NULL,
    reason_id     TINYINT                              NOT NULL,
    until_date    DATETIME                             NOT NULL,
    canceled_date DATETIME                             NULL,
    create_date   DATETIME DEFAULT current_timestamp() NOT NULL,

    CONSTRAINT mute_id_pk PRIMARY KEY (id),
    CONSTRAINT mute_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT mute_executed_user_uuid_fk FOREIGN KEY (executed_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT mute_canceled_user_uuid_fk FOREIGN KEY (canceled_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ban
(
    id            INT AUTO_INCREMENT                   NOT NULL,
    user_uuid     BINARY(16)                           NOT NULL,
    executed_uuid BINARY(16)                           NOT NULL,
    canceled_uuid BINARY(16)                           NULL,
    reason_id     TINYINT                              NOT NULL,
    until_date    DATETIME                             NOT NULL,
    canceled_date DATETIME                             NULL,
    create_date   DATETIME DEFAULT current_timestamp() NOT NULL,

    CONSTRAINT ban_id_pk PRIMARY KEY (id),
    CONSTRAINT ban_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT ban_canceled_user_uuid_fk FOREIGN KEY (executed_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT ban_executed_user_uuid_fk FOREIGN KEY (canceled_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan
(
    id        INT AUTO_INCREMENT,
    user_uuid BINARY(16) NOT NULL,

    CONSTRAINT clan_id_pk PRIMARY KEY (id),
    CONSTRAINT clan_user_uuid_pk UNIQUE (user_uuid),
    CONSTRAINT clan_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_meta
(
    clan_id INT                       NOT NULL,
    name    TINYTEXT                  NOT NULL,
    tag     TINYTEXT                  NOT NULL,
    banner  TEXT DEFAULT 'Banner-NBT' NOT NULL,

    CONSTRAINT clan_meta_pk UNIQUE (clan_id),
    CONSTRAINT clan_meta_clan_id_fk FOREIGN KEY (clan_id) REFERENCES clan (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_setting
(
    clan_id  INT                  NOT NULL,
    publicly TINYINT(1) DEFAULT 0 NOT NULL,
    request  TINYINT(1) DEFAULT 0 NOT NULL,

    CONSTRAINT clan_setting_pk UNIQUE (clan_id),
    CONSTRAINT clan_setting_clan_id_fk FOREIGN KEY (clan_id) REFERENCES clan (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_member
(
    clan_id     INT                                                     NOT NULL,
    user_uuid   BINARY(16)                                              NOT NULL,
    `rank`      SET ('MEMBER', 'MODERATOR') DEFAULT 'MEMBER'            NOT NULL,
    create_date DATETIME                    DEFAULT current_timestamp() NOT NULL,

    CONSTRAINT clan_member_pk UNIQUE (user_uuid),
    CONSTRAINT clan_member_clan_id_fk FOREIGN KEY (clan_id) REFERENCES clan (id) ON DELETE CASCADE,
    CONSTRAINT clan_member_user_id_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_invite
(
    clan_id        INT                                  NOT NULL,
    user_uuid      BINARY(16)                           NOT NULL,
    clan_user_uuid BINARY(16)                           NOT NULL,
    create_date    DATETIME DEFAULT current_timestamp() NOT NULL,

    CONSTRAINT clan_invite_clan_id_fk FOREIGN KEY (clan_id) REFERENCES clan (id) ON DELETE CASCADE,
    CONSTRAINT clan_invite_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT clan_invite_clan_user_uuid_fk FOREIGN KEY (clan_user_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_request
(
    clan_id        INT        NOT NULL,
    user_uuid      BINARY(16) NOT NULL,
    clan_user_uuid BINARY(16) NOT NULL,
    create_date    DATETIME   NOT NULL,
    CONSTRAINT clan_request_clan_id_fk FOREIGN KEY (clan_id) REFERENCES clan (id),
    CONSTRAINT clan_request_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid),
    CONSTRAINT clan_request_clan_user_uuid_fk FOREIGN KEY (clan_user_uuid) REFERENCES user (uuid)
);

CREATE TABLE IF NOT EXISTS friend
(
    user_uuid   BINARY(16)                           NOT NULL,
    friend_uuid BINARY(16)                           NOT NULL,
    create_date DATETIME DEFAULT current_timestamp() NOT NULL,
    CONSTRAINT friend_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT friend_friend_uuid_fk FOREIGN KEY (friend_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friend_request
(
    user_uuid      BINARY(16)                           NOT NULL,
    requested_uuid BINARY(16)                           NOT NULL,
    create_date    DATETIME DEFAULT current_timestamp() NOT NULL,
    CONSTRAINT friend_request_user_uuid_fk FOREIGN KEY (user_uuid) REFERENCES user (uuid) ON DELETE CASCADE,
    CONSTRAINT friend_request_requested_uuid_fk FOREIGN KEY (requested_uuid) REFERENCES user (uuid) ON DELETE CASCADE
);