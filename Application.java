package main;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Name: Kensal J Ramos
 * 
 * Description: Implement the Bellman-Ford and Floyd-Warshall algorithms. Both will read from the same file but print
 * to different files. 
 * 
 * Bellman-Ford: Very similar to Djikstra's algorithm, few tweaks
 * 
 * Floyd-Warshall: Use Dynamic programming implementation. In other words, 2-D array that updates distances as it goes.
 * This removes branches reducing runtime from 3^n (recursive Floyd-Warshall) to n^3.
 * 
 * We will create a graph and have the graph class have both algorithms as methods. So graph.(insert algorithm) (parameters)
 * 
 */

public class Application {
	
	public static void main(String[] args) throws negWeightCycleException {

		
		Scanner scanner;
		Graph graph = new Graph(0, 0);
		int numVert = 0;
		int source = 0;
		int numEdge = 0;
		int i = 0;
		
		// Set up read/write
		try {
			scanner = new Scanner(new File("cop3503-asn3-input.txt"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		while(scanner.hasNextInt() || scanner.hasNext("#")) {
			
			// Don't read comments
			if (scanner.hasNext("#")) 
				scanner.nextLine();
		
			switch (i) {
			case 0:
				numVert = scanner.nextInt();
				System.out.println("Number of vertices: " + numVert);
				i++;
				break;
			case 1:
				source = scanner.nextInt();
				System.out.println("Source vertex: " + source);
				i++;
				break;
			case 2:
				numEdge = scanner.nextInt();
				System.out.println("Number of edges: " + numEdge);
				i++;
				graph.setNumVert(numVert);
				graph.setNumEdge(numEdge);
				break;
			default:
				graph.edges[i - 3].setSrc(scanner.nextInt());
				graph.edges[i - 3].setDest(scanner.nextInt());
				graph.edges[i - 3].setWeight(scanner.nextInt());
				i++;
			}
			
	
			
		}
		
		scanner.close();
		graph.bellFordAlg(graph, source);
		graph.floydWarshAlg(graph, source);
		
	}
	


}

class Edge {
	
	private int src;
	private int dest;
	private int weight;
	
	Edge() {
		setSrc(0);
		setDest(0);
		setWeight(0);
	}

	public int getSrc() {
		return src;
	}

	public void setSrc(int src) {
		this.src = src;
		System.out.println("Source set to " + this.src);
	}

	public int getDest() {
		return dest;
	}

	public void setDest(int dest) {
		this.dest = dest;
		System.out.println("Destination set to " + this.dest);
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
		System.out.println("Weight set to " + this.weight);
	}
	
}

class Graph {
	
	
	int numVert;
	int numEdge;
	Edge edges[];
	
	Graph(int numVert, int numEdge) {
		
		setNumVert(numVert);
		setNumEdge(numEdge);
		edges = new Edge[numEdge];
		
		for (int i = 0; i < numEdge; i++) 
			edges[i] = new Edge();
	}
	
	public int getNumVert() {
		return numVert;
	}

	public void setNumVert(int numVert) {
		this.numVert = numVert;
	}

	public int getNumEdge() {
		return numEdge;
	}

	public void setNumEdge(int numEdge) {
		this.numEdge = numEdge;
		edges = new Edge[numEdge];
		
		for (int i = 0; i < numEdge; i++) 
			edges[i] = new Edge();
	}
	

	/*
	 * Bellman-Ford Algorithm in layman terms:
	 * 
	 * (These steps are taken from Professor Gerber's powerpoints at University of Central Florida)
	 * 
	 * 1) Set all distances from given source to all other vertices as infinite and mark every node's parent as undefined
	 * 
	 * 2) Repeat |V| - 1 times
	 * 	For each edge e = (u,v)
	 * 		Let w = e.weight
	 * 		if u.distance + w < v.distance
	 * 			let v.distance = u.distance + w
	 * 			v.parent = u
	 * 
	 * 3) For each edge e = (u,v)
	 * 		Let w = e.weight
	 * 		If u.distance + w < v.distance
	 * 			Throw exception, negative-weight cycle
	 */
	void bellFordAlg(Graph graph, int srcVert) throws negWeightCycleException {
		
		System.out.println("\nStart Bellman-Ford algorithm...\n");
		
		PrintWriter pw;
		int numVert = graph.getNumVert();
		int numEdge = graph.getNumEdge();
		// This array will hold distances. We can keep track since index 0 of graph corresponds to index 0 of dist
		int dist[] = new int[numVert];
		System.out.println("Created dist of size  " + numVert);
		int u;
		int v;
		int weight;
		Edge temp;
		int tempNum = 0;
		int distIndex = 0;

		// Set distances to infinite
		for (int i = 0; i < numVert; i++)
			dist[i] = Integer.MAX_VALUE;
		dist[0] = 0;

		for (int i = 0; i < graph.getNumEdge(); i++) {
			if (graph.edges[i].getSrc() == srcVert) {
				System.out.println("source found");
				temp = graph.edges[0];
				graph.edges[0] = graph.edges[i];
				graph.edges[i] = temp;
				tempNum = i;
				break;
			}
				
		}
		
		
		// Step 2: Repeat |V| - 1 times
		for (int i = 0; i < numVert; i++) {
			
			// For each edge e = (u,v)
			for (int j = 0; j < numEdge; j++) {
				
				u = graph.edges[j].getSrc(); 
				v = graph.edges[j].getDest(); 
				weight = graph.edges[j].getWeight(); 
				
				// If u.distance + w < v.distance
				// Let v.distance = u.distance + w
				// v.parent = u (Handled later)
				if (dist[u - 1] != Integer.MAX_VALUE && dist[u - 1] + weight < dist[v - 1]) {
					dist[v - 1] = dist[u - 1] + weight;
					System.out.println("dist at index " + (v - 1) + " is " + dist[v - 1]);
				}
			
			}
		}
		
		// Step 3: For each edge e = (u,v)
		// Let w = e.weight
		// If u.distance + w < v.distance
		// Throw exception, negative-weight cycle
		for (int i = 0; i < numEdge; i++) {
			
			u = graph.edges[i].getSrc(); 
			v = graph.edges[i].getDest(); 
			weight = graph.edges[i].getWeight(); 
			
			if (dist[u - 1] != Integer.MAX_VALUE && dist[u - 1] + weight < dist[v - 1])
				throw new negWeightCycleException("\nNegative weight cycle!! Exiting Bellman-Ford algorithm\n");
			
		}
		
		// Swap back
		temp = graph.edges[tempNum];
		graph.edges[tempNum] = graph.edges[0];
		graph.edges[0] = temp;
		distIndex = dist[srcVert - 1];
		dist[srcVert - 1] = dist[0];
		dist[0] = distIndex;
		
		// Start printing to our output text file
		try {
			pw = new PrintWriter("cop3503-asn3-output-ramos-kensal-bf.txt");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		// Printing format:
		// 
		pw.println(numVert);
		for (int i = 0; i < numVert; i++) {
			pw.println((i + 1) + " " + srcVert + " " + dist[i]);
		}
		
		
		
		System.out.println("\nEnd Bellman-Ford algorithm...\n");
		pw.close();
	}
	
	
	
	void floydWarshAlg(Graph graph, int srcVert) {
		
		System.out.println("Start Floyd-Warshall algorithm...\n");
		
		PrintWriter pw;
		int numArr[][] = new int[graph.getNumVert()][graph.getNumVert()];
		int next[][] = new int[graph.getNumVert()][graph.getNumVert()];
		
		System.out.println("edges[0] = " + edges[0].getSrc());
		
		// Initialize numArr
		for (int i = 0; i < numVert; i++) {
			
			for (int j = 0; j < numVert; j++) {
				if (i == j) {
					numArr[i][j] = 0;
					next[i][j] = j;
				}
				else
					numArr[i][j] = 9999;
				
				for (int k = 0; k < graph.numEdge; k++) {
					if (edges[k].getSrc() == i + 1 && edges[k].getDest() == j + 1)  {
						numArr[i][j] = edges[k].getWeight();
						next[i][j] = j;
						System.out.println("Added weight: " + edges[k].getWeight());
					}

				}
			}
		}
		
		System.out.println("numArr = { ");
		for (int i = 0; i < numVert; i++) {
			for (int j = 0; j < numVert; j++) {
				
					
				System.out.print(numArr[i][j] + " ");
			}
			System.out.println();
		}
		
		for (int i = 0; i < numVert; i++) {
			for (int j = 0; j < numVert; j++) {
				
				for (int k = 0; k < numVert; k++) {
					
					if (numArr[j][k] > numArr[j][i] + numArr[i][k]) {
						
						System.out.println("Previous numArr[j][k] is: " + numArr[j][k]);
						numArr[j][k] = numArr[j][i] + numArr[i][k];
						next[j][k] = next[j][i];
						System.out.println("new numArr[j][k] is: " + numArr[j][k]);
					}
					
				}
			}
		}

		
		for (int i = 5; i > 0; i--) {
			for (int j = 5; j > 0; j--) {
				numArr[i][j] = numArr[j][i];
			}
		}
		
		for (int j = 0; j < graph.numVert; j++) {
			numArr[j][0] = numArr[0][j];
		}
		
		
		System.out.println("numArr = { ");
		for (int i = 0; i < numVert; i++) {
			
			for (int j = 0; j < numVert; j++) {
				System.out.print(numArr[i][j] + " ");
			}
			System.out.println();
		}
		
		// Start printing to our output text file
		try {
			pw = new PrintWriter("cop3503-asn3-output-ramos-kensal-fw.txt");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		for (int i = 0; i < numVert; i++) {
			
			for (int j = 0; j < numVert; j++) {
				pw.print(numArr[i][j] + " ");
			}
			pw.println();
		}
		
		
		System.out.println("\nEnd Floyd-Warshall algorithm...");
		pw.close();
	}
	
	
}

@SuppressWarnings("serial")
class negWeightCycleException extends Exception {
	
	public negWeightCycleException(String msg) {
		System.out.println(msg);
	}
	
}


