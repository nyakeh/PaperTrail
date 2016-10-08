package uk.co.nyakeh.papertrail.database;

public class ReadingListOrderDbSchema {
    public static final class ReadingListOrderTable {
        public static final String NAME = "reading_list_order";

        public static final class Cols {
            public static final String ID = "id";
            public static final String BOOK_ID = "book_id";
            public static final String POSITION = "position";
        }
    }
}