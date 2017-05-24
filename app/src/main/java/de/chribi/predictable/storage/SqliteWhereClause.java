package de.chribi.predictable.storage;


class SqliteWhereClause {
    private String clause;
    private String[] args;
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
