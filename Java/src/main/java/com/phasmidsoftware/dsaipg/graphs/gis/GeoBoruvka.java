package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;

/**
 * GeoBoruvka is a generic class for computing Minimum Spanning Trees (MST) specifically tailored for geometric graphs.
 * It extends the base Boruvka class to leverage Borůvka's algorithm for MST generation and provides additional functionality
 * to handle geometric edges and nodes.
 *
 * @param <V> the type of the vertex, which must extend GeoPoint.
 * @param <X> the type of the edge attribute, which must be Comparable and implement Sequenced.
 */
public
class GeoBoruvka<V extends GeoPoint, X extends Comparable<X> & Sequenced> extends Boruvka<V, X> implements GeoMST<V, X> {
    public GeoBoruvka(EdgeGraph<V, X> graph) {
        super(graph);
    }

    /**
     * Method to generate a graph of the MST, given an empty BaseGeoGraph
     *
     * @param geoGraph an empty GeoGraph which will be filled with edges before being returned.
     * @return the geoGraph that was passed as the parameter, but filled with the MST edges.
     */
    public Geo<V, X> getGeoMST(Geo<V, X> geoGraph) {
        // Changed "Kruskal" to "Boruvka" in comment
        EdgeGraph<V, X> mst = super.getMST(); // Boruvka's minimum spanning tree
        for (Edge<V, X> e : mst.edges())
            geoGraph.addEdge(createEdge(e));
        return geoGraph;
    }

    /**
     * Creates a new geographic edge (GeoEdge) based on the provided edge.
     *
     * @param edge the input edge from which to create a new GeoEdge, containing two vertices and an attribute.
     * @return a new GeoEdge instance representing the same relationship as the input edge but using the GeoEdge implementation.
     */
    public Edge<V, X> createEdge(Edge<V, X> edge) {
        V v = edge.get();
        return new GeoEdge<>(v, edge.getOther(v), edge.getAttribute());
    }

}