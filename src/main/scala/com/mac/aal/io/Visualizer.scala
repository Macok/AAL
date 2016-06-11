package com.mac.aal.io

import java.awt.Dimension
import javax.swing.JFrame

import com.mac.aal.graph.{RoadNetwork, Road}
import edu.uci.ics.jung.algorithms.layout.FRLayout
import edu.uci.ics.jung.graph.{Graph, UndirectedSparseGraph}
import edu.uci.ics.jung.visualization.BasicVisualizationServer
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller
import edu.uci.ics.jung.visualization.picking.MultiPickedState
import edu.uci.ics.jung.visualization.renderers.{DefaultEdgeLabelRenderer, DefaultVertexLabelRenderer}

/**
  * Created by mac on 05.06.16.
  */
case class Visualizer(roadNetwork: RoadNetwork) {

  case class Edge(city1: String, city2: String, duration: Int, price: Int) {
    override def toString: String = "[d:" + duration + "/p:" + price + "]"
  }

  def visualize() = {
    val g: Graph[String, Edge] = new UndirectedSparseGraph[String, Edge]

    for (city <- roadNetwork.cities) {
      val cityId: String = city._1
      g.addVertex(cityId)

      for (road <- city._2.adjacent) {
        val roadDestId: String = road._1
        val roadParams: Road = road._2
        g.addEdge(Edge(cityId, roadDestId, roadParams.duration, roadParams.price), cityId, roadDestId)
      }
    }

    // The Layout<V, E> is parameterized by the vertex and edge types
    val layout = new FRLayout(g)

    layout.setSize(new Dimension(1400, 700))
    // sets the initial size of the space
    // The BasicVisualizationServer<V,E> is parameterized by the edge types
    val vv = new BasicVisualizationServer[String, Edge](layout)
    vv.getRenderContext.setPickedVertexState(new MultiPickedState[String])
    vv.getRenderContext.setPickedEdgeState(new MultiPickedState[Edge])
    vv.getRenderContext.setVertexLabelTransformer(new ToStringLabeller())
    vv.getRenderContext.setEdgeLabelTransformer(new ToStringLabeller())
    vv.getRenderContext.setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(java.awt.Color.BLUE))
    vv.getRenderContext.setVertexLabelRenderer(new DefaultVertexLabelRenderer(java.awt.Color.YELLOW))
    vv.setPreferredSize(new Dimension(1400, 700)); //Sets the viewing area size

    val frame = new JFrame("Simple Graph View")
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.getContentPane.add(vv)
    frame.pack()
    frame.setVisible(true)
  }
}
