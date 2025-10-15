# BVAL: Beta Value Analyzer

## Authors:
- [Ramon Reilman](https://github.com/RamonReilman)
- [Yamila Timmer](https://github.com/YamilaTimmer)

## Description
This repository contains a Java commandline tool for analyzing- and comparing of beta values as found in methylation data. With BVAL we hope to allow users to get more insight into methylation of various input samples- and regions. 

### Tool features
1. Generating a summary to get an overview of the input beta values file
2. Generating a filtered output file, where the user can filter on samples (columns), genomic positions (chromosomes or genes) and on specific beta value ranges using a cutoff
3. Generating a report where two or more samples are compared using one of various statistical methods: [Student's _t_-test](https://en.wikipedia.org/wiki/Student%27s_t-test), [Spearman's rank correlation coefficient](https://en.wikipedia.org/wiki/Spearman%27s_rank_correlation_coefficient), [Wilcoxon signed-rank test](https://en.wikipedia.org/wiki/Wilcoxon_signed-rank_test)

## System requirements and installation
### System requirements
- **OS**: Windows, macOS or Linux

This tool was created using:
- Java ([v.24.0.1](https://www.oracle.com/java/technologies/javase/jdk24-archive-downloads.html))
- Gradle ([v.8.14](https://gradle.org/releases/))

### Installation (for users)
Download the newest release of BVAL ([v0.0.1](https://github.com/YamilaTimmer/methylation-java-app/releases/tag/v0.0.1)). This release includes the application and all its dependencies.

After downloading, run the app using:
```bash
java -jar bval-app-0.0.1.jar 
```

The example above runs the app with no arguments and will print the help function, showing information on the different commands of BVAL, read more about how to use the different commands in the chapter '[Use cases of BVAL](#use-cases-of-bval)'.

### Clone the repository (for developers)
For further developing and/or testing we recommend cloning the repository, this can be done using:
```bash
git clone https://github.com/YamilaTimmer/methylation-java-app
```

### Running the tool
#### Information about required data
This app uses methylation data as input, specifically beta values (see more information [here](#biological-background)), in [.csv](https://en.wikipedia.org/wiki/Comma-separated_values) format. There are **some columns that are required**, below these are briefly described:
- `gene`: a column containing the gene for each row
- `chr`: a column containing the chromosome for each row
- `sample`: one or more columns, containing the beta value per sample per row

Also see the [example data](https://github.com/YamilaTimmer/BVAL-java-application/blob/main/data/exampledata.csv) to get an idea of what the input data could look like.

> [!NOTE]  
> The filtering usecase as described [here](#generating-filtered-data), uses the mandatory columns to filter, however any other columns containing other data are also permitted, they cannot be filtered on but they will be present in the final output.

> [!IMPORTANT]  
> There is no strict column order, as the app relies on checking on column names instead. Make sure to name the gene column `gene` and the chromosome column `chr`! Samples columns can have any name, but the index of the first sample column has to be specified, this index uses normal counting, so the first column of the file is index 1, etc. **All other samples have to be after the first sample column and no other columns should be behind or within the sample columns.**

#### Use cases of BVAL
The tool contains three distinct use cases, below some examples are shown on how to use these.
##### Generating a summary
The `summary` subcommand generates a small overview with some statistics of the input file, including how many samples/genes the input file contains, what the avg. beta-value is and the amount of NA-values.

```bash
summary -f <file-path> <sample-index>
```

Generating a summary can be done by passing `summary` and `-f`, followed by a file path. As well as an argument containing the index for the first sample column (`-si`). The input file should contain input methylation data containing beta values (in [.csv](https://en.wikipedia.org/wiki/Comma-separated_values) format), read more about the expected input file [here](#information-about-required-data). Below an example can be found using the [example data](https://github.com/YamilaTimmer/methylation-java-app/blob/main/data/exampledata.csv) from this repo:

```bash
summary -f data/exampledata.csv -si 7
```

Which prints the following output to the terminal:
```
Generating summary...
---------------------
Number of samples: 3
Number of genes: 10
Avg beta value: 0.5
Amount of NA values: 1
```

##### Generating filtered data
The `filter` subcommand allows the user to make a filtered subset based on the input file. The user can filter on samples, chromosomes, (or) genes and on beta-values using a cutoff.

```bash
filter -f <file-path> -sl <sample-index> -s <samples> [ -chr <chromosomes> OR -g <genes> ] -c <cutoff-value> -ct <cutoff-type> -o <output-file>
```

Generating a filtered output file from the input can be done by passing `filter` and `-f`, followed by a file path containing input methylation data containing beta values (in [.csv](https://en.wikipedia.org/wiki/Comma-separated_values) format). 

**Below, all possible user arguments are described for the filter use case:**

**Mandatory arguments**:
* `-f/--file`: file path with input, containing beta values
* `-si/--sample-index`: should be the index of the first sample column
  
**Optional arguments**:
* `-s/--sample`: one or more samples, specified as the corresponding column names from the input file
* `-chr/--chromosome`: one or more chromosomes (mutually exclusive with --gene)
* `-g/--gene`: one or more gene names (mutually exclusive with --chromosome)
* `-c/--cutoff`: a value ≥ 0 and ≤ 1.0 that serves as a cutoff value for filtering on beta values 
* `-ct/--cutofftype`: how to filter using the cutoff (upper/lower), by default it filters out any values below the cutoff (upper), but using lower will filter out any values higher than the cutoff
* `-o/--output`: allows user to give path to where the output file should be saved

Below an example of the command, with arguments is shown. This example filters the input data in a way that only Sample1 and Sample 2 (the first two columns) are kept, with only rows containing chromosome 17. Only beta values below 0.5 are kept.
```bash
BVAL filter -f data/exampledata.csv -si 7 -s Sample1 Sample2 -chr 17 -c 0.5 -ct lower
```

The filtered output is written to the user-specified path, if no path is given it is automatically generated as output.txt in the same directory as the tool
##### Comparing methylation across samples/regions
The `compare` subcommand allows the user to perform statistical analyses on a (sub)set of samples and thus compare methylation across samples.

```bash
compare -f <file-path> -s <samples> -m <comparison-method>
```

**Below, all possible user arguments are described for the compare use case**:

**Mandatory arguments**:
* `-f/--file`: file path with input, containing beta values
* `-si/--sampleindex`: should be the index of the first sample column
  
**Optional arguments**:
* `-s/--sample`: one or more samples to compare to each other, specified as the corresponding column names from the input file. If no sample is specified, all samples in the file will be compared to eachother.
* `-m/--methods`: statistical method(s) on which the samples should be compared, possible methods are:
	- [Student's _t_-test](https://en.wikipedia.org/wiki/Student%27s_t-test) [t-test], 
	- [Spearman's rank correlation coefficient](https://en.wikipedia.org/wiki/Spearman%27s_rank_correlation_coefficient) [spearman], 
	- [Wilcoxon signed-rank test](https://en.wikipedia.org/wiki/Wilcoxon_signed-rank_test) [wilcoxon-test])
  If no methods are specified, all methods are ran.
* `-o/--output`: allows user to give path to where the output file should be saved

#### Information on verbosity
As user you can specify how much output you would like to receive in the terminal, this can be set using `--verbose`, followed by either `0`, `1` or `2`. 
- `0` [WARNING]: default setting, only shows errors and warnings
- `1` [INFO]: outputs errors/warning plus additional information
- `2` [DEBUG]: outputs all of above plus extra information that is useful for debugging purposes

#### Additional help
For each subcommand you can get a help menu using:

Get an overview of the possible options with:
```bash
[summary/filter/compare] [-h/--help]
```

This will show a menu with all possible options and their usage.

To check the current version of the application use:

```bash
BVAL  [-V/--version]
```

## Biological background
More and more research is being conducted in the field of epigenetics, to investigate possible causes and/or treatment options for diseases such as cancer. It is believed that methylation of DNA plays a major role in the development of diseases. This is a process in which methyl groups are added to cytosine bases by DNA methyltransferases. The methylated form of cytosine, 5mC, represents only about 1% of all bases in the body and is mainly found in “CpG sites”, which are regions where a cytosine is followed by a guanine base. CpG sites are often heavily methylated, except in “CpG islands,” which are regions of about 1,000 base pairs with a higher CpG density than the rest of the genome, yet these islands are often unmethylated. Around 70% of all gene promoters are located within a CpG island (Moore et al., 2012).

Methylation of DNA prevents transcription factors from binding to the transcription start site, resulting in a decrease of gene expression or even silencing of the gene. A well-known example of this is hypermethylation of promoter regions of tumor suppressor genes, which disables the body’s natural defense mechanism against tumor formation (Kaluscha et al., 2022). However, hypomethylation also appears to be linked to the development of diseases, although this relationship seems less clear than that of hypermethylation (Van Tongelen et al., 2017).

A commonly used method for analyzing methylation within the epigenome is the “Illumina Methylation Array.” This method measures two signals for each target location in the genome: an unmethylated signal and a methylated signal (Illumina, n.d.). After further processing, beta values can be determined — these are values ranging from 0 to 1, which are interpreted as the methylation percentage of the target region, on a scale from 0 to 100%. The beta value is calculated by dividing the methylated signal by the sum of the methylated and unmethylated signals. α is a small value added to prevent negative values resulting from corrections or technical errors. A value of 0 indicates that all copies of the target region were completely unmethylated, while a value of 1 indicates that all copies were fully methylated (Du et al., 2010). Below the full formula for calculating beta values is shown.

$$
\text{Beta}_i = \frac{\max(y_{i,\text{methy}}, 0)}{\max(y_{i,\text{unmethy}}, 0) + \max(y_{i,\text{methy}}, 0) + \alpha}
$$


## Support
In case of any bugs or needed support, please open an issue [here](https://github.com/YamilaTimmer/methylation-java-app/issues).

## License
This project is licensed under GNU General Public License v3.0. See the [LICENSE file](https://github.com/YamilaTimmer/methylation-java-app/blob/main/LICENSE) for details.

## Sources

- Du, P., Zhang, X., Huang, C., Jafari, N., Kibbe, W. A., Hou, L., & Lin, S. M. (2010). Comparison of Beta-value and M-value methods for quantifying methylation levels by microarray analysis. BMC Bioinformatics, 11(1). Https://doi.org/10.1186/1471-2105-11-587 
- Kaluscha, S., Domcke, S., Wirbelauer, C., Stadler, M. B., Durdu, S., Burger, L., & Schübeler, D. (2022). Evidence that direct inhibition of transcription factor binding is the prevailing mode of gene and repeat repression by DNA methylation. Nature Genetics, 54(12), 1895–1906. https://doi.org/10.1038/s41588-022-01241-6 
- Moore, L. D., Le, T., & Fan, G. (2012). DNA Methylation and Its Basic Function. Neuropsychopharmacology, 38(1), 23–38. https://doi.org/10.1038/npp.2012.112 
- Illumina. (z.d.). Introduction to Methylation Array Analysis. https://www.illumina.com/techniques/microarrays/methylation-arrays.html
- Van Tongelen, A., Loriot, A., & De Smet, C. (2017). Oncogenic roles of DNA hypomethylation through the activation of cancer-germline genes. Cancer Letters, 396, 130–137. [https://doi.org/10.1016/j.canlet.2017.03.029](https://doi.org/10.1016/j.canlet.2017.03.029) 
