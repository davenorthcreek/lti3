package org.imsglobal.lti3.toolProvider;

import java.util.Collection;

public interface Hierarchical {
	
	/**
	 * if object has more than one parent, return one at random
	 * otherwise return the one parent, or null
	 * @return the parent of this object, or null
	 */
	public Hierarchical getParent();
	
	/**
	 * if object has more than one child, return one child at random
	 * otherwise return the one child, or null
	 * @return the child of this object, or null
	 */
	public Hierarchical getChild();
	
	/**
	 * get the collection of all parents of this object
	 * (only one generation, not all ancestors).
	 * @return the Collection of parents of this object (may be empty)
	 */
	public Collection<Hierarchical> getParents();
	
	/**
	 * get the collection of all children of this object
	 * (only children, not grandchildren or other descendants).
	 * @return the Collection of children of this object (may be empty)
	 */
	public Collection<Hierarchical> getChildren();

}
