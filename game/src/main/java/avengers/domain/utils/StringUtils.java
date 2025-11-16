package avengers.domain.utils;

/** Example utility class. Replace with actual utilities needed by the domain layer. */
public class StringUtils {
  /**
   * Checks if a string is null or empty after trimming.
   *
   * @param str the string to check
   * @return true if the string is null or empty after trimming
   */
  public static boolean isNullOrEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

  /**
   * Capitalizes the first letter of a string and makes the rest lowercase.
   *
   * @param str the string to capitalize
   * @return the capitalized string, or the original string if null or empty
   */
  public static String capitalize(String str) {
    if (isNullOrEmpty(str)) {
      return str;
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
  }
}
