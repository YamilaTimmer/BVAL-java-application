package nl.bioinf.processing;


import nl.bioinf.model.MethylationArray;

import java.util.ArrayList;
import java.util.List;

public class CompositeUserArgumentsCheck implements UserArgumentsCheck {
    private List<UserArgumentsCheck> filters = new ArrayList<>();

    public void addFilter(UserArgumentsCheck filter) {
        filters.add(filter);
    }

    // Goes through all checks for all arguments
    @Override
    public boolean pass(MethylationArray methylationArray) {
        for (UserArgumentsCheck filter : filters) {
            if (! filter.pass(methylationArray)) {
                return false;
            }
        }
        return true;
    }

}
