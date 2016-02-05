package com.tr.muhurath.app.muhurat.utils;

/**
 * Holder for AppMessages
 * @author Balaji Katika (balaji.katika@gmail.com) on 2/5/16.
 */
public interface AppMessages {
    String MSG_PERMISSION_DLG_TITLE = "Allow Permissions";
    String MSG_IGNORE = "Ignore";
    String MSG_ENABLE_PERMISSION = "Enable Permission";
    String MSG_PERMISSION_DLG_MSG = "Location Permissions are not given for Muhurat\n\nSelect '"
            + MSG_ENABLE_PERMISSION
            + "'to modify permissions. Else '" + MSG_IGNORE + "'";
    String MSG_LOC_ENABLE = "Enable Location";
    String MSG_LOC_LAST_KNOWN = "Use Last Known";
    String MSG_LOC_DLG_MSG = "Your Locations Settings is set to 'Off'.\n\nSelect '" + MSG_LOC_LAST_KNOWN
            + "' to use last known settings.\nSelect '" + MSG_LOC_ENABLE + "' to enable Location Settings";
}
