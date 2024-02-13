package com.qooke.levelrunproject.model;

public class TranslateRes {
    public Message message;

    public class Message {
        public Result result;

        public class Result {
            public String translatedText;
        }
    }
}
