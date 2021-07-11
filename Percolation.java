/******************************************************************************
 *  Author: Blayne Ayersman
 *  Last Edit Date: 6/27/2021
 *  Dependencies: WeightedQuickUnionUF.java
 *
 *  This class serves as an API through which to model a percolation system.
 *
 ******************************************************************************/

// Models a percolation system with NxN sites by using an optimized union-find data structure.
public class Percolation {
    private final int n;                                        // Number of rows/columns
    private final int nSquared;                                 // Number of sites in grid
    private int numOpen = 0;                                    // Number of open sites
    private boolean[] openStatus;                               // Stores true/false for if corresponding site is open
    private final WeightedQuickUnionUF uf;                      // Union Find structure of percolation system sites with a virtual top and bottom site
    private final WeightedQuickUnionUF uf2;                     // Union Find structure of percolation system sites with a virtual top site, but no virtual bottom site

    // Creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("Argument must be greater than or equal to 1");

        this.n = n;                                             // Initialize number of rows/columns
        this.nSquared = n * n;                                  // Initialize n x n

        openStatus = new boolean[n * n + 2];                    // Initialize array of boolean checks for each site to represent if it's open
        openStatus[nSquared] = true;                            // Open virtual bottom site
        openStatus[nSquared + 1] = true;                        // Open virtual top site

        uf = new WeightedQuickUnionUF(nSquared + 2);            // Fill uf with sites + virtual top and bottom site
        uf2 = new WeightedQuickUnionUF(nSquared + 1);           // No virtual bottom site
    }

    // Opens the site (row, col) if it is not open already (connects the site to any adjacent open sites)
    public void open(int row, int col) {
        if (row < 1 || col < 1 || row > n || col > n)
            throw new IllegalArgumentException("row or column argument is outside of range");

        row--;                                                  // Decrement row input to match our uf array structure
        col--;                                                  // Decrement column input to match our uf array structure
        int site = n * row + col;                               // Calculate index of site given grid row & column

        if (!openStatus[site]) {                                // If site is not already open
            openStatus[site] = true;                            // Open the site
            numOpen++;                                          // Increment number of open sites

            if (site % n != n - 1 && openStatus[site + 1]) {    // If site is not in the right column,
                uf.union(site + 1, site);                       // connect to adjacent site to the right if it's open.
                uf2.union(site + 1, site);
            }

            if (site % n != 0 && openStatus[site - 1]) {        // If site is not in the left column,
                uf.union(site - 1, site);                       // connect to adjacent site to the left if it's open.
                uf2.union(site - 1, site);
            }

            if (site < n) {                                     // If site is in the top row,
                uf.union(nSquared + 1, site);                   // connect to virtual top site.
                uf2.union(nSquared, site);
            } else if (openStatus[site - n]) {                  // Otherwise if adjacent site above is open,
                uf.union(site - n, site);                       // Connect to adjacent site above.
                uf2.union(site - n, site);
            }

            if (site >= n * (n - 1))                            // If site is in the bottom row,
                uf.union(site, nSquared);                       // connect to virtual bottom site.
            else if (openStatus[site + n]) {                    // Otherwise if adjacent site below is open,
                uf.union(site + n, site);                       // connect to adjacent site below.
                uf2.union(site + n, site);
            }
        }
    }

    // Returns true if the site (row, col) is open
    public boolean isOpen(int row, int col) {
        if (row < 1 || col < 1 || row > n || col > n)
            throw new IllegalArgumentException("row or column argument is outside of range");

        row--;                                                  // Decrement row input to match union-find array structure
        col--;                                                  // Decrement column input to match union-find array structure
        int site = n * row + col;                               // Calculate site in question from row & col
        return openStatus[site];                                // Check if bool value stored for this index of uf in openStatus array is true
    }

    // Returns true if the site at (row, col) is connected to the virtual top site
    public boolean isFull(int row, int col) {
        if (row < 1 || col < 1 || row > n || col > n)
            throw new IllegalArgumentException("row or column argument is outside of range");

        row--;
        col--;
        int site = n * row + col;                               // Calculate site in question from row & col
        return uf2.find(site) == uf2.find(nSquared);            // Return whether or not site is connected to vTop component
    }

    // Returns the number of open sites
    public int numberOfOpenSites() {
        return numOpen;                                         // Return count of components in uf
    }

    // Returns true if system percolates
    public boolean percolates() {
        return uf.find(nSquared) == uf.find(nSquared + 1);      // Check if vBottom has the same id as vTop
    }
}
