package imsglobal.toolProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class to represent a group set.
 * <p>
 * A group set is a collection of related groups, normally a user would only be
 * a member of one of the groups.  Users are not members of the group set itself.
 */
  public class GroupSet {

/**
 * Title of group set.
 */
    private String title = null;
/**
 * List of groups which form part of the group set.
 */
    private List<String> groups = null;
/**
 * Total number of users within all the groups forming part of the set.
 */
    private int numMembers = 0;
/**
 * Total number of staff users within all the groups forming part of the set.
 */
    private int numStaff = 0;
/**
 * Total number of learners within all the groups forming part of the set.
 */
    private int numLearners = 0;

/**
 * Construct a group set with the specified title.
 */
    public GroupSet(String title) {
      this.title = title;
      this.groups = new ArrayList<String>();
    }

/**
 *  Returns the title of the group set.
 *
 * @return title
 */
    public String getTitle() {
      return title;
    }

/**
 * Returns a list of the group IDs for the group set.
 *
 * @return list of group IDs
 */
    public List<String> getGroups() {
      return Collections.unmodifiableList(groups);
    }

/**
 * Add a group ID to the group set.
 *
 * @param id Group ID
 */
    public void addGroup(String id) {
      if (!this.groups.contains(id)) {
        this.groups.add(id);
      }
    }

/**
 * Returns the number of users belonging to the groups within the group set.
 *
 * @return number of users
 */
    public int getNumMembers() {
      return this.numMembers;
    }

/**
 * Increments the number of users belonging to the groups within the group set.
 */
    public void incNumMembers() {
      this.numMembers++;
    }

/**
 * Returns the number of staff users belonging to the groups within the group set.
 *
 * @return number of staff users
 */
    public int getNumStaff() {
      return this.numStaff;
    }

/**
 * Increments the number of staff users belonging to the groups within the group set.
 */
    public void incNumStaff() {
      this.numStaff++;
    }

/**
 * Returns the number of learners belonging to the groups within the group set.
 *
 * @return number of learners
 */
    public int getNumLearners() {
      return this.numLearners;
    }

/**
 * Increments the number of users belonging to the groups within the group set.
 */
    public void incNumLearners() {
      this.numLearners++;
    }

  }
