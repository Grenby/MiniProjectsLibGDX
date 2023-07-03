package voronoi.event;

import lombok.val;
import voronoi.beachline.Beachline;
import voronoi.graph.Edge;
import voronoi.graph.Graph;
import voronoi.graph.Point;

import java.util.Collection;

public class SiteEvent extends Event {

    public SiteEvent(Point point) {
        super(point);
    }

    @Override
    public void handle(Collection<Event> eventQueue, Beachline beachline, Graph graph) {
        val result = beachline.insertArc(getPoint());
        result.splitLeaf.ifPresent(l -> graph.addEdge(new Edge(l.getSite(), getPoint())));
        result.splitLeaf.ifPresent(l -> l.getSubscribers().forEach(eventQueue::remove));
        result.newLeaf.addCircleEvents(eventQueue::add, getPoint().y);
    }
}
