package com.example.myjavaapplication.controllers;

import com.example.myjavaapplication.model.Card;

import java.util.ArrayList;

public interface CardFetchCallback {
    void onCardsFetched(ArrayList<Card> cards);
    void onError(String errorMessage);
}

