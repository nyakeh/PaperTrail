package uk.co.nyakeh.papertrail;

public class Statistics {
    public int TotalPagesRead = 0;
    public int AveragePageCount = 0;
    public float AverageRating = 0.0f;
    public String MostReadCategory = "";

    public Statistics(int totalPagesRead, int averagePageCount, float averageRating, String mostReadCategory) {
        TotalPagesRead = totalPagesRead;
        AveragePageCount = averagePageCount;
        AverageRating = averageRating;
        MostReadCategory = mostReadCategory;
    }
}