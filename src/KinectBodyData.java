
import processing.data.JSONArray;

public class KinectBodyData {
	JSONArray jarray;

	public int getPersonCount() {
		return jarray.size();
	}

	/**
	 * returns null if the body number is out of range
	 * 
	 * @param i
	 * @return
	 */
	public Body getPerson(int i) {
		try {
			return new Body(jarray.getJSONObject(i));
		} catch (Exception e) {
			return null;
		}
	}

	public KinectBodyData(String jsonStr) {
		jarray = JSONArray.parse(jsonStr);
	}

}
