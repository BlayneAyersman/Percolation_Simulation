import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

// Performs a Monte Carlo simulation of a percolation system to approximate threshold value (p*)
public class PercolationStats {
    // Declare private class members
    private static final double CONFIDENCE_95 = 1.96;                   // Z-value for 95% confidence level
    private final double[] thresholds;                                  // Array of threshold values from each trial
    private double mean = 0;                                            // Mean of threshold values
    private double stddev = 0;                                          // Standard deviation of threshold values
    private double confidenceHi;                                        // High end of confidence interval
    private double confidenceLo = 0;                                    // Low end of confidence interval

    // Perform independent trials on an n x n grid
    // Accepts grid row/column length and number of simulations to perform as integer arguments
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0)                                      // Throw error if arguments are out of range
            throw new IllegalArgumentException("Arguments must be greater than zero!");

        // Initialize variables
        int gridSize = n * n;                                           // Grid size
        int numBlocked;                                                 // Number of blocked sites
        thresholds = new double[trials];                                // Array of threshold values
        Percolation percolation;                                        // Object used to model percolation system
        Site site;                                                      // Temporary object to store the location (row, column) of an individual site in the percolation model
        Site[] blockedSites;                                            // Array of blocked site locations


        // Perform trials
        for (int t = 0; t < trials; t++) {                              // For each trial,
            percolation = new Percolation(n);                           // Initialize a new n-by-n percolation model
            blockedSites = new Site[gridSize];                          // Initialize a new list of blocked sites in the model
            numBlocked = gridSize;

            // Fill blockedSites array with all row/column locations in the percolation grid
            for (int row = 0; row < n; row++)
                for (int col = 0; col < n; col++)
                    blockedSites[row * n + col] = new Site(row + 1, col + 1);

            while (!percolation.percolates()) {                         // Until the system percolates
                int siteIdx = StdRandom.uniform(numBlocked);            // Generate random blockedSites index
                site = blockedSites[siteIdx];                           // Set current site to site at index
                percolation.open(site.getRow(), site.getCol());         // Open site
                numBlocked--;                                           // Decrement number of blocked sites

                // Swap opened site with last blocked site in blockedSites range
                blockedSites[siteIdx] = blockedSites[numBlocked];
                blockedSites[numBlocked] = site;

            }                                                           // Once the system percolates,
            thresholds[t] = (double) percolation.numberOfOpenSites()
                    / (double) gridSize;                                // Calculate and set threshold value at current index
        }                                                               // Increment t
        mean = StdStats.mean(thresholds);
        stddev = StdStats.stddev(thresholds);
        confidenceHi = mean + (CONFIDENCE_95 * stddev / Math.sqrt(trials));
        confidenceLo = mean - (CONFIDENCE_95 * stddev / Math.sqrt(trials));
    }

    // Site object holds row and column integer values corresponding to the location of a grid element
    private static class Site {
        private final int row;
        private final int col;

        Site(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    // Return the sample mean of percolation thresholds
    public double mean() {
        return mean;
    }

    // Return the sample standard deviation of percolation thresholds
    public double stddev() {
        return stddev;
    }

    // Return the low endpoint of the 95% confidence interval
    public double confidenceLo() {
        return confidenceLo;
    }

    // Return the high endpoint of the 95% confidence interval
    public double confidenceHi() {
        return confidenceHi;
    }

    // Test client
    public static void main(String[] args) {
        // Read grid size and number of trial simulations to perform from common input
        int n = StdIn.readInt();                                        // Read grid size from common input
        int trials = StdIn.readInt();                                   // Read number of trials from common input
        PercolationStats pStats = new PercolationStats(n, trials);      // Initialize new PercolationStats object to analyze simulation results

        // Output statistics
        StdOut.println("mean                    = " + pStats.mean());
        StdOut.println("stddev                  = " + pStats.stddev());
        StdOut.println("95% confidence interval = [" + pStats.confidenceLo() + ", " + pStats.confidenceHi() + "]");
    }
}

