package imsglobal.toolProvider;


/**
 * Class to represent a group.
 */
  public class Group {

/**
 * Title of group.
 */
    private String title = null;
/**
 * Id for the set which the group belongs to, may be null.
 */
    private String setId = null;

/**
 * Construct a group with the specified title and set ID.
 */
    public Group(String title, String setId) {
      this.title = title;
      this.setId = setId;
    }

/**
 * Returns the title of the group.
 *
 * @return title
 */
    public String getTitle() {
      return title;
    }

/**
 * Returns the set ID of the group.
 *
 * @return set ID
 */
    public String getSetId() {
      return setId;
    }

  }
