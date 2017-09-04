package com.gk.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * Id
 * ProductId -              unique identifier for the product
 * UserId -                 unqiue identifier for the user
 * ProfileName
 * HelpfulnessNumerator -   number of users who found the review helpful
 * HelpfulnessDenominator - number of users who indicated whether they found the review helpful
 * Score -                  rating between 1 and 5
 * Time -                   timestamp for the review
 * Summary -                brief summary of the review
 * Text -                   text of the review
 * <p>
 * sqlite> pragma table_info(Reviews);
 * 0|Id|INTEGER|0||1
 * 1|ProductId|TEXT|0||0
 * 2|UserId|TEXT|0||0
 * 3|ProfileName|TEXT|0||0
 * 4|HelpfulnessNumerator|INTEGER|0||0
 * 5|HelpfulnessDenominator|INTEGER|0||0
 * 6|Score|INTEGER|0||0
 * 7|Time|INTEGER|0||0
 * 8|Summary|TEXT|0||0
 * 9|Text|TEXT|0||0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Review {
    private Long id;
    private String productId;
    private String userId;
    private String profileName;
    private int helpfulnessNumerator;
    private int helpfulnessDenominator;
    private int score;
    private Long time;
    private String summary;
    private String text;
    private Map<Integer, String> idToSentence;
    private int firstSentenceId;
    private int notTranslatedSentences;

    public void decrementNotTranslatedSentances() {
        notTranslatedSentences--;
    }
}