package com.example.myjavaapplication.controllers;

public class links {

    //public static final String LINK_SERVER_NAME = "http://192.168.1.10/myproject";
    public static final String LINK_SERVER_NAME = "http://192.168.1.29/myproject";
    public static final String LINK_SIGNUP = LINK_SERVER_NAME + "/users/createUser.php";
    public static final String LINK_AUTHUSER = LINK_SERVER_NAME + "/users/authUser.php";
    public static final String LINK_EMAIL_SUGGESTION = LINK_SERVER_NAME + "/users/getEmailSuggestion.php";
    public static final String LINK_GET_USER_BY_EMAIL = LINK_SERVER_NAME + "/users/getUserbyEmail.php";
    public static final String LINK_CREATE_BOARD = LINK_SERVER_NAME + "/boards/createBoard.php";
    public static final String LINK_READ_BOARD = LINK_SERVER_NAME + "/boards/readBoardById.php";
    public static final String LINK_DELETE_BOARD = LINK_SERVER_NAME + "/boards/deleteBoard.php";
    public static final String LINK_VIEW_BOARD = LINK_SERVER_NAME + "/boards/readBoard.php";
    public static final String LINK_CREATE_TASK= LINK_SERVER_NAME + "/tasks/createTask.php";
    public static final String LINK_READ_TASK= LINK_SERVER_NAME + "/tasks/readTask.php";
    public static final String LINK_DELETE_TASK= LINK_SERVER_NAME + "/tasks/deleteTaskById.php";
    public static final String LINK_UPDATE_TASK= LINK_SERVER_NAME + "/tasks/updateTaskName.php";

    public static final String LINK_CREATE_CARD= LINK_SERVER_NAME + "/cards/createCard.php";
    public static final String LINK_READ_CARD= LINK_SERVER_NAME + "/cards/readCard.php";
    public static final String LINK_READ_CARD_BY_ID= LINK_SERVER_NAME + "/cards/readCardById.php";
    public static final String LINK_DELETE_CARD_BY_TASK_ID= LINK_SERVER_NAME + "/cards/deleteCardByTaskId.php";
    public static final String LINK_UPDATE_CARD_ORDER= LINK_SERVER_NAME + "/cards/updateCardOrder.php";
    public static final String LINK_UPDATE_CARD_DETAILS= LINK_SERVER_NAME + "/cards/updateCardDetails.php";
    public static final String LINK_SET_CARD_ORDER_ZERO= LINK_SERVER_NAME + "/cards/setCardOrderZero.php";
    public static final String LINK_DELETE_CARD= LINK_SERVER_NAME + "/cards/deleteCard.php";
    public static final String LINK_CREATE_MEMBER= LINK_SERVER_NAME + "/members/createMember.php";
    public static final String LINK_READ_MEMBER= LINK_SERVER_NAME + "/members/readMember.php";


    // Constructor to prevent instantiation of this class
    private links() {
        throw new AssertionError();
    }
}
