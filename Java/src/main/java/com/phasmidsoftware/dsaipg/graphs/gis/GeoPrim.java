package com.phasmidsoftware.dsaipg.graphs.gis;

import com.phasmidsoftware.dsaipg.graphs.undirected.Edge;
import com.phasmidsoftware.dsaipg.graphs.undirected.EdgeGraph;

/**
 * The GeoPrim class is a specialization of the Prim algorithm implementation for graph-based
 * structures that extend geographical points and sequences. This class works on a graph of
 * vertices and edges, and it is designed to compute the Minimum Spanning Tree (MST) of a given
 * graph structure.
 * <p>
 * The class takes generic types V and X:
 * - V extends GeoPoint: Represents the vertices of the graph, which are geographical points.
 * - X extends Comparable and Sequenced: Represents the attributes of the edges, which must be
 * comparable for ordering and implement a sequenced interface for identification.
 * <p>
 * GeoPrim extends the functionality of the Prim class for use with geographical graph data.
 * The algorithm computes the MST and generates a GeoGraph representation of it.
 */
public class GeoPrim<V extends GeoPoint, X extends Comparable<X> & Sequenced> extends Prim<V, X> implements GeoMST<V, X> {

    /**
     * Method to generate a graph of the MST, given an empty BaseGeoGraph
     *
     * @param geoGraph an empty GeoGraph which will be filled with edges before being returned.
     * @return the geoGraph that was passed as the parameter, but filled with the MST edges.
     */
    public Geo<V, X> getGeoMST(Geo<V, X> geoGraph) {
        EdgeGraph<V, X> mst = super.getMST();
        for (Edge<V, X> e : mst.edges())
            geoGraph.addEdge(createEdge(e));
        return geoGraph;
    }

    /**
     * Creates a new GeoEdge instance from the given Edge.
     * The new GeoEdge is constructed using the vertices and attribute of the original Edge.
     *
     * @param edge the input edge, which contains two vertices and an attribute. The vertices
     *             must be of a type that extends GeoPoint, representing geographic points,
     *             while the attribute represents properties of the connection between the vertices.
     * @return a new GeoEdge instance that connects the same vertices as the input edge and carries
     *         the same attribute.
     */
    public Edge<V, X> createEdge(Edge<V, X> edge) {
        V v = edge.get();
        return new GeoEdge<>(v, edge.getOther(v), edge.getAttribute());
    }

    /**
     * Constructs a GeoPrim object for computing the Minimum Spanning Tree (MST) of the given graph.
     * Utilizes Prim's algorithm specialized for geographical graphs with vertices and edges encapsulating spatial information.
     *
     * @param graph the edge-weighted graph to be processed. This graph must provide vertices and edges,
     *              where vertices represent geographic points, and edges have attributes that are
     *              comparable and sequenced.
     */
    public GeoPrim(EdgeGraph<V, X> graph) {
        super(graph);
    }
}