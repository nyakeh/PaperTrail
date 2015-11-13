package uk.co.nyakeh.papertrail.database;

public class BookDbSchema {
    public static final class BookTable {
        public static final String NAME = "books";

        public static final class Cols {
            public static final String ID = "id";
            public static final String TITLE = "title";
            public static final String AUTHOR = "author";
            public static final String PROGRESS = "progress";
            public static final String LENGTH = "length";
            public static final String STARTED = "started";
            public static final String FINISHED = "finished";
            public static final String IMAGE_URL = "image_url";
            public static final String CATEGORY = "category";
            public static final String STATUS = "status";
            public static final String ISBN = "isbn";
            public static final String DESCRIPTION = "description";
        }
    }
}