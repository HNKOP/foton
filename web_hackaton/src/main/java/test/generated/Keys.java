/*
 * This file is generated by jOOQ.
 */
package test.generated;


import javax.annotation.Generated;

import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;

import test.generated.tables.Messages;
import test.generated.tables.Users;
import test.generated.tables.records.MessagesRecord;
import test.generated.tables.records.UsersRecord;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>atom</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.11"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<MessagesRecord, Integer> IDENTITY_MESSAGES = Identities0.IDENTITY_MESSAGES;
    public static final Identity<UsersRecord, Integer> IDENTITY_USERS = Identities0.IDENTITY_USERS;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<MessagesRecord> KEY_MESSAGES_PRIMARY = UniqueKeys0.KEY_MESSAGES_PRIMARY;
    public static final UniqueKey<MessagesRecord> KEY_MESSAGES_ID_UNIQUE = UniqueKeys0.KEY_MESSAGES_ID_UNIQUE;
    public static final UniqueKey<UsersRecord> KEY_USERS_PRIMARY = UniqueKeys0.KEY_USERS_PRIMARY;
    public static final UniqueKey<UsersRecord> KEY_USERS_ID_UNIQUE = UniqueKeys0.KEY_USERS_ID_UNIQUE;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<MessagesRecord, Integer> IDENTITY_MESSAGES = Internal.createIdentity(Messages.MESSAGES, Messages.MESSAGES.ID);
        public static Identity<UsersRecord, Integer> IDENTITY_USERS = Internal.createIdentity(Users.USERS, Users.USERS.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<MessagesRecord> KEY_MESSAGES_PRIMARY = Internal.createUniqueKey(Messages.MESSAGES, "KEY_messages_PRIMARY", Messages.MESSAGES.ID);
        public static final UniqueKey<MessagesRecord> KEY_MESSAGES_ID_UNIQUE = Internal.createUniqueKey(Messages.MESSAGES, "KEY_messages_id_UNIQUE", Messages.MESSAGES.ID);
        public static final UniqueKey<UsersRecord> KEY_USERS_PRIMARY = Internal.createUniqueKey(Users.USERS, "KEY_users_PRIMARY", Users.USERS.ID);
        public static final UniqueKey<UsersRecord> KEY_USERS_ID_UNIQUE = Internal.createUniqueKey(Users.USERS, "KEY_users_id_UNIQUE", Users.USERS.ID);
    }
}
