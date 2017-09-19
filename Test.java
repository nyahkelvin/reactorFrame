/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.boleka.reactor;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphFactory;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerModule;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kelvinashu
 */
public class Test {

    private final static Logger LOGGER = Logger.getLogger(Test.class.getName());

    public static void main(String[] args) {
        Test t = new Test();
        t.test();
    }

    public void test() {
        OrientGraphFactory factory = new OrientGraphFactory("remote:localhost/xxxxx", "xxxx", "xxxx");
        FramedGraph<OrientGraph> framedGraph = null;
        try {

            OrientGraph graph = factory.getTx();
            framedGraph = new FramedGraphFactory(new JavaHandlerModule()).create(graph);


            for (Vertex v : graph.getVerticesOfClass("Loan")) {
                Loan person = framedGraph.frame(v, Loan.class);

                GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<>();
                pipe.start(person.asVertex()).outE("HasBid").inV();

                Iterable<Bid> bids = framedGraph.frameVertices(pipe, Bid.class);

                //System.out.println("Marshalled: ");
                for (Bid bid : bids) {
                    System.out.println(person.asVertex().getId() + " -> " + bid.getBidRating());
                }

                
            }

            framedGraph.shutdown();
        } catch (Exception e) {
            if (framedGraph != null) {
                framedGraph.shutdown();
            }

            System.out.println("exception " + e);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            factory.close();
        }
    }
}
