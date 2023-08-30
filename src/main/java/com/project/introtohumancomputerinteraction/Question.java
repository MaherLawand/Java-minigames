package com.project.introtohumancomputerinteraction;

import javafx.scene.image.Image;

import java.util.List;

public class Question {
    private String questionText;
    private Image questionImage; // If the question is presented as an image
    private List<String> choices;
    private int correctAnswerIndex;
    private int score;
    private int attempts;

    public Question(String questionText, Image questionImage, List<String> choices, int correctAnswerIndex) {
        this.questionText = questionText;
        this.questionImage = questionImage; // Can be null if the question type is text
        this.choices = choices;
        this.correctAnswerIndex = correctAnswerIndex;
        this.score = 0;
        this.attempts = 0;
    }
    public Question(String questionText, List<String> choices, int correctAnswerIndex) {
        this.questionText = questionText;
        this.choices = choices;
        this.correctAnswerIndex = correctAnswerIndex;
        this.score = 0;
        this.attempts = 0;
    }
    public Question(Image questionImage, List<String> choices, int correctAnswerIndex) {
        this.questionImage = questionImage; // Can be null if the question type is text
        this.choices = choices;
        this.correctAnswerIndex = correctAnswerIndex;
        this.score = 0;
        this.attempts = 0;
    }
    public boolean answer(int chosenIndex) {
        attempts++;
        if (chosenIndex == correctAnswerIndex) {
            score = attempts == 1 ? 10 : (attempts == 2 ? 7 : 4); // decrement score by 3 after each wrong attempt
            return true;
        }
        return false;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Image getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(Image questionImage) {
        this.questionImage = questionImage;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

}
