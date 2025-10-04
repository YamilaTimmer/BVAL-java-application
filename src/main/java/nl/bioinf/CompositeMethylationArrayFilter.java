package nl.bioinf;


import java.util.ArrayList;
import java.util.List;

public class CompositeMethylationArrayFilter implements MethylationArrayFilter {
    private List<MethylationArrayFilter> filters = new ArrayList<>();

    public void addFilter(MethylationArrayFilter filter) {
        filters.add(filter);
    }

    // Goes through all checks for all arguments
    @Override
    public boolean pass(MethylationArray methylationArray) {
        for (MethylationArrayFilter filter : filters) {
            if (! filter.pass(methylationArray)) {
                return false;
            }
        }
        return true;
    }

}
