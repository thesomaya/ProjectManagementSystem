package com.example.myjavaapplication.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

public class Constants {

    // Firebase Constants
    // This is used for the collection name for USERS.
    public static final String USERS = "users";

    // This is used for the collection name for BOARDS.
    public static final String BOARDS = "boards";

    // Firebase database field names
    public static final String IMAGE = "image";
    public static final String NAME = "name";
    public static final String MOBILE = "mobile";
    public static final String ASSIGNED_TO = "assignedTo";
    public static final String DOCUMENT_ID = "documentId";
    public static final String TASK_LIST = "taskList";
    public static final String ID = "id";
    public static final String EMAIL = "email";

    public static final String BOARD_DETAIL = "board_detail";

    public static final String TASK_LIST_ITEM_POSITION = "task_list_item_position";
    public static final String CARD_LIST_ITEM_POSITION = "card_list_item_position";

    public static final String BOARD_MEMBERS_LIST = "board_members_list";

    public static final String SELECT = "Select";
    public static final String UN_SELECT = "UnSelect";

    // A unique code for asking the Read Storage Permission using this we will be check and identify in the method onRequestPermissionsResult
    public static final int READ_STORAGE_PERMISSION_CODE = 1;
    // A unique code of image selection from Phone Storage.
    public static final int PICK_IMAGE_REQUEST_CODE = 2;

    public static final String PROGEMANAG_PREFERENCES = "ProjemanagPrefs";
    public static final String FCM_TOKEN = "fcmToken";
    public static final String FCM_TOKEN_UPDATED = "fcmTokenUpdated";

    // TODO (Step 1: Add the base url and key params for sending firebase notification.)
    // START
    public static final String FCM_BASE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String FCM_AUTHORIZATION = "authorization";
    public static final String FCM_KEY = "key";
    public static final String FCM_SERVER_KEY = "AAAA-_vvGNI:APA91bF9xfSzbacs2j9RKkmEg7aYqY4pmRr89vYoy8pOfr0Ds2yHyVlhkhDiryrPndNYbXHUYyCdzZakvrlxxDLjsyQv5Ybtom5dFr7VWaMzDOL6YcSF-09GAOoxHU7SAisyZ222PW3w";
    public static final String FCM_KEY_TITLE = "title";
    public static final String FCM_KEY_MESSAGE = "message";
    public static final String FCM_KEY_DATA = "data";
    public static final String FCM_KEY_TO = "to";
    // END

    /**
     * A function for user profile image selection from phone storage.
     */
    public static void showImageChooser(Activity activity) {
        // An intent for launching the image selection of phone storage.
        Intent galleryIntent = new Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE);
    }

    /**
     * A function to get the extension of selected image.
     */
    public static String getFileExtension(Activity activity, Uri uri) {
        /*
         * MimeTypeMap: Two-way map that maps MIME-types to file extensions and vice versa.
         *
         * getSingleton(): Get the singleton instance of MimeTypeMap.
         *
         * getExtensionFromMimeType: Return the registered extension for the given MIME type.
         *
         * contentResolver.getType: Return the MIME type of the given content URL.
         */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.getContentResolver().getType(uri));
    }
}
