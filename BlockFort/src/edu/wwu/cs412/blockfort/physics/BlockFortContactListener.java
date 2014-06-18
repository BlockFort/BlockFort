/**
 * Class that helps us activate specific events
 * when two physics objects contact each other.
 * Created by Dana Vold on April 26, 2014
 * for the Block Fort game being created in
 * cs412 spring 2014, wwu.
 * Project by Dana Vold, Max Hampton, and Alex Freedman
 * 
 * This class is for tracking when physics objects
 * touch one another. 
 * 
 * NOTE:  whenever the comments in this class 
 * say "touching" they mean touching in the 
 * simulated physics world, NOT that someone 
 * is touching a tablet screen.
 * 
 */

package edu.wwu.cs412.blockfort.physics;

import java.util.ArrayList;

import org.jbox2d.collision.broadphase.BroadPhaseStrategy;
import org.jbox2d.dynamics.TimeStep;
import org.jbox2d.dynamics.ContactManager;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

public class BlockFortContactListener extends ContactManager {
	
	
	public BlockFortContactListener(World argPool, BroadPhaseStrategy strategy) {
		super(argPool, strategy);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Two shapes are now touching
	 */
	public void impact(Contact contact) {
		// new contact has begun.
		//TODO call appropriate impact events
		contact.getFixtureA().getBody().getUserData();
		contact.getFixtureB().getBody().getUserData();
	}
	
}
