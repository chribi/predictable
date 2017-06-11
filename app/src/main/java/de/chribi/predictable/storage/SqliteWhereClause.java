package de.chribi.predictable.storage;


class SqliteWhereClause {
    private final String clause;
    private final String[] args;
    SqliteWhereClause(String clause, String[] args)
    {
        this.clause = clause;
        this.args = args;
    }

    String getClause() {
        return clause;
    }

    String[] getArgs() {
        return args;
    }
}
