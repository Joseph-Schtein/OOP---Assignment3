package gameClient;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

import java.util.ArrayList;
import java.util.List;

public class FloydWarshallSolver {

  private int n;
  private boolean solved;
  private double[][] dp;
  private Integer[][] next;

  private static final int REACHES_NEGATIVE_CYCLE = -1;

  /**
   * As input, this class takes an adjacency matrix with edge weights between nodes, where
   * POSITIVE_INFINITY is used to indicate that two nodes are not connected.
   *
   * <p>NOTE: Usually the diagonal of the adjacency matrix is all zeros (i.e. matrix[i][i] = 0 for
   * all i) since there is typically no cost to go from a node to itself, but this may depend on
   * your graph and the problem you are trying to solve.
   */
  public FloydWarshallSolver(double[][] matrix) {
    n = matrix.length;
    dp = new double[n][n];
    next = new Integer[n][n];

    // Copy input matrix and setup 'next' matrix for path reconstruction.
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (matrix[i][j] != POSITIVE_INFINITY) next[i][j] = j;
        dp[i][j] = matrix[i][j];
      }
    }
  }

  /**
   * Runs Floyd-Warshall to compute the shortest distance between every pair of nodes.
   *
   * @return The solved All Pairs Shortest Path (APSP) matrix.
   */
  public double[][] getApspMatrix() {
    solve();
    return dp;
  }

  // Executes the Floyd-Warshall algorithm.
  public void solve() {
    if (solved) return;

    // Compute all pairs shortest paths.
    for (int k = 0; k < n; k++) {
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          if (dp[i][k] + dp[k][j] < dp[i][j]) {
            dp[i][j] = dp[i][k] + dp[k][j];
            next[i][j] = next[i][k];
          }
        }
      }
    }
    solved = true;
  }

  /**
   * Reconstructs the shortest path (of nodes) from 'start' to 'end' inclusive.
   *
   * @return An array of nodes indexes of the shortest path from 'start' to 'end'. If 'start' and
   *     'end' are not connected return an empty array. If the shortest path from 'start' to 'end'
   *     are reachable by a negative cycle return -1.
   */
  public List<Integer> reconstructShortestPath(int start, int end) {
    solve();
    List<Integer> path = new ArrayList<>();
    if (dp[start][end] == POSITIVE_INFINITY) return path;
    int at = start;
    for (; at != end; at = next[at][end]) {
      path.add(at);
    }
    // Return null since there are an infinite number of shortest paths.
    path.add(end);
    return path;
  }

  /* Example usage. */

  // Creates a graph with n nodes. The adjacency matrix is constructed
  // such that the value of going from a node to itself is 0.
  public static double[][] createGraph(int n) {
    double[][] matrix = new double[n][n];
    for (int i = 0; i < n; i++) {
      java.util.Arrays.fill(matrix[i], POSITIVE_INFINITY);
      matrix[i][i] = 0;
    }
    return matrix;
  }
} 
