/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.core.quiz;

/**
 * @author wjohnson000
 *
 */
public class QuizModel {

    protected int    id;
    protected String question;
    protected String language;
    protected String region;
    protected String quizId;

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("lang=").append(language);
        buff.append(";  region=").append(region);
        buff.append(";  question=").append(question);
        buff.append(";  quizId=").append(quizId);
        return buff.toString();
    }
}
