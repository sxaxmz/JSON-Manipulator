package stackstagingcom.firstwebpage3_com.websiteranking;

public class items {

    private String siteName;
    private String siteId;
    private String visitDate;
    private String visiotrs;

    public items(String siteName, String siteId, String visitDate, String visiotrs) {
        this.siteName = siteName;
        this.siteId = siteId;
        this.visitDate = visitDate;
        this.visiotrs = visiotrs;
    }

    public String getSiteName() {
        return siteName;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public String getVisiotrs() {
        return visiotrs;
    }
}
