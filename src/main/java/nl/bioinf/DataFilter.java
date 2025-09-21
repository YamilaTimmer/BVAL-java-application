package nl.bioinf;

// Not implemented/finished
public class DataFilter {

    //Allow user to filter on: sample, e.g. sample 2 equals column 6, so only look at column 6 for all xaxis
    //Allow user to pass positional argument, e.g. chr2 or gene TP53, only showing results for those

    public enum PositionalArgument {
        CHR,
        GENE
        //FPOS,
        //TPOS;
    }


    public static PositionalArgument filterPos(String filterArguments) {

        // Convert to uppercase
        String filterArgumentsUpper = filterArguments.toUpperCase();

        PositionalArgument pa = null;

        if (filterArguments.contains("CHR") && filterArguments.contains("GENE")){
            System.err.println("Please only enter one positional filter, either Chr or Gene");
        }
        else if(filterArguments.contains("CHR")){
            pa = PositionalArgument.CHR;
        }else if(filterArguments.contains("GENE")){
            pa = PositionalArgument.GENE;
        }

        return pa;
    }


}
