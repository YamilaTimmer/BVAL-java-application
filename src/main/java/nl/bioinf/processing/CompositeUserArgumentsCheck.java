package nl.bioinf.processing;

import java.util.ArrayList;
import java.util.List;

public class CompositeUserArgumentsCheck implements UserArgumentsCheck{
    private final List<UserArgumentsCheck> filters = new ArrayList<>();

    public void addFilter(UserArgumentsCheck filter) {
        filters.add(filter);
    }

    // Goes through all checks for all arguments
    public boolean pass() {
        for (UserArgumentsCheck filter : filters) {
            if (! filter.pass()) {
                return false;
            }
        }
        return true;
    }
}
