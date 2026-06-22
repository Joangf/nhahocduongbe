package vn.viettel.bvrhm.nhahocduong.api.user.internal.validator;

import java.util.regex.Pattern;

/**
 * @author: longlb1
 * @since: 16-Oct-23
 */

public class UserValidator {
  public static boolean validatePasswordStrength(String password) {
    Pattern pattern = Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}");
    return pattern.matcher(password).find();
  }
}
