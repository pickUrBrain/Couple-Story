
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * PersonTracker maintains a HashMap of current tracking ID and body pose data.
 * It also maintains sets of new tracking IDs (enters) and IDs not longer being
 * tracked (exits). PersionTracker update should be called every frame with new
 * tracknig data.
 * 
 * @author eitan
 *
 */
public class PersonTracker {
	protected HashSet<Long> enters = new HashSet<Long>();
	protected HashSet<Long> exits = new HashSet<Long>();
	private HashMap<Long, Body> peopleOld = new HashMap<Long, Body>(); // keep this for reuse
	protected HashMap<Long, Body> people = new HashMap<Long, Body>();

	public PersonTracker() {

	}

	/**
	 * @return returns a Set of Track IDs of people who entered the space during the
	 *         most recent call to update. Any ID in enter set will also be in
	 *         HashMap returned by getPeople.
	 */
	public HashSet<Long> getEnters() {
		return enters;
	}

	/**
	 * @return returns a Set of Track IDs of people who exited the space during the
	 *         most recent call to update. Any ID in exit set will not be in HashMap
	 *         returned by getPeople.
	 */
	public HashSet<Long> getExits() {
		return exits;
	}

	/**
	 * @return returns a Set of Track IDs of people currently in the space.
	 */
	public Set<Long> getIds() {
		return people.keySet();
	}

	/**
	 * @return returns a HashMap of Track IDs to Bodys (tracking information) for
	 *         people of people currently in the space.
	 */
	public HashMap<Long, Body> getPeople() {
		return people;
	}

	/**
	 * Swaps current people hashmap with oldPeople HashMap for re-use.
	 */
	private void swapActiveSet() {
		HashMap<Long, Body> tmp = people;
		people = peopleOld;
		peopleOld = tmp;
	}

	/**
	 * update should be called every frame. It compares current tracking information
	 * to tracking information from the last frame and generates enters, exits, and
	 * people.
	 * 
	 * @param data
	 *            - current tracking data
	 */
	public void update(KinectBodyData data) {
		swapActiveSet();
		people.clear();
		for (int i = 0; i < data.getPersonCount(); i++) {
			Body body = data.getPerson(i);
			people.put(body.id, body);
		}
		enters.clear();
		enters.addAll(people.keySet());
		enters.removeAll(peopleOld.keySet());

		exits.clear();
		exits.addAll(peopleOld.keySet());
		exits.removeAll(people.keySet());

		for (Long l : enters) {
			System.out.println("enter:" + l);
		}
		for (Long l : exits) {
			System.out.println(" exit:" + l);
		}
	}

}
