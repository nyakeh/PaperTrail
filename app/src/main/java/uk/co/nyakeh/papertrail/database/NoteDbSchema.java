package uk.co.nyakeh.papertrail.database;

public class NoteDbSchema {
    public static final class NoteTable {
        public static final String NAME = "notes";

        public static final class Cols {
            public static final String ID = "id";
            public static final String BOOK_ID = "book_id";
            public static final String TITLE = "title";
            public static final String CONTENT = "content";
            public static final String CREATED = "created";
            public static final String UPDATED = "updated";
        }
    }
}