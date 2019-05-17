package toorla.utilities.graph;

import java.util.*;

public class Graph <Node> {
    private Map<Node, Set<Node>> parentShipRelation;

    public Graph()
    {
        parentShipRelation = new HashMap<>();
    }
    public void addNodeAsParentOf( Node desired , Node parentNode ) throws GraphDoesNotContainNodeException {
        if( !parentShipRelation.containsKey( desired ) )
            throw new GraphDoesNotContainNodeException();
        else
            parentShipRelation.get(desired).add(parentNode);
    }

    public Collection<Node> getParentsOfNode(Node desired ) throws GraphDoesNotContainNodeException
    {
        Node firstDesire = desired;
        if( !parentShipRelation.containsKey( desired ) )
            throw new GraphDoesNotContainNodeException();

        Collection<Node> c = parentShipRelation.get( desired );
        while(parentShipRelation.get( desired ).contains("Any") == false && parentShipRelation.get( desired ).contains(firstDesire) == false ) {
            c.addAll(parentShipRelation.get( desired ));
            desired = parentShipRelation.get(desired).iterator().next();
        }
        if( parentShipRelation.get( desired ).contains(firstDesire) ){
            c.add(firstDesire);
        }
        c.add((Node) "Any");
        return c;
    }
    public void addNode( Node desired ) throws NodeAlreadyExists
    {
        if( parentShipRelation.containsKey( desired ) )
            throw new NodeAlreadyExists();
        parentShipRelation.put( desired , new HashSet<>() );
    }
    public boolean doesGraphContainNode(Node desired )
    {
        return parentShipRelation.containsKey( desired );
    }
}
