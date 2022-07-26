package org.beanmaker.v2.cli;

import org.beanmaker.v2.codegen.DatabaseServer;
import org.beanmaker.v2.codegen.MySQLDatabaseServer;

enum DatabaseType {
    MYSQL;

    DatabaseServer getServerInstance(String serverFQDN, int serverPort, String username, char[] password) {
        return new MySQLDatabaseServer(serverFQDN, serverPort, username, new String(password));
    }

}
