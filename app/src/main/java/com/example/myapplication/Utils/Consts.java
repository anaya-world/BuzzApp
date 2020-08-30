package com.example.myapplication.Utils;

public interface Consts {
    public static final String ALREADY_LOGGED_IN = "You have already logged in chat";
    public static final int CALL_ACTIVITY_CLOSE = 1000;
    public static final int CALL_ACTIVITY_CLOSE_WIFI_DISABLED = 1001;
    public static final int COMMAND_LOGIN = 1;
    public static final int COMMAND_LOGOUT = 2;
    public static final int COMMAND_NOT_FOUND = 0;
    public static final String CONFERENCE_TYPE = "conference_type";
    public static final String DEFAULT_USER_PASSWORD = "quickblox";
    public static final int ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS = 422;
    public static final int ERR_MSG_DELETING_HTTP_STATUS = 401;
    public static final String EXTRA_COMMAND_TO_SERVICE = "command_for_service";
    public static final String EXTRA_CONFERENCE_TYPE = "conference_type";
    public static final String EXTRA_CONTEXT = "context";
    public static final String EXTRA_IS_INCOMING_CALL = "conversation_reason";
    public static final String EXTRA_IS_STARTED_FOR_CALL = "isRunForCall";
    public static final String EXTRA_LOGIN_ERROR_MESSAGE = "login_error_message";
    public static final String EXTRA_LOGIN_RESULT = "login_result";
    public static final int EXTRA_LOGIN_RESULT_CODE = 1002;
    public static final String EXTRA_OPPONENTS_LIST = "opponents_list";
    public static final String EXTRA_PENDING_INTENT = "pending_Intent";
    public static final String EXTRA_QB_USER = "qb_user";
    public static final String EXTRA_TAG = "currentRoomName";
    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_USER_LOGIN = "user_login";
    public static final String EXTRA_USER_PASSWORD = "user_password";
    public static final int MAX_OPPONENTS_COUNT = 3;
    public static final String OPPONENTS = "opponents";
    public static final String[] PERMISSIONS =
            {"android.permission.CAMERA", "android.permission.RECORD_AUDIO"};
    public static final String PREF_CURRENT_TOKEN = "current_token";
    public static final String PREF_CURREN_ROOM_NAME = "current_room_name";
    public static final String PREF_TOKEN_EXPIRATION_DATE = "token_expiration_date";
    public static final String QB_CONFIG_FILE_NAME = "qb_config.json";
    public static final String VERSION_NUMBER = "1.0";
    public static final String WIFI_DISABLED = "wifi_disabled";

    public enum StartConversationReason {
        INCOME_CALL_FOR_ACCEPTION,
        OUTCOME_CALL_MADE
    }
}
