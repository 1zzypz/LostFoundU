package lostfound.model;

import java.util.List;

public interface Reportable {

    void generateReport(List<Item> items, List<ClaimRequest> claims);
}
