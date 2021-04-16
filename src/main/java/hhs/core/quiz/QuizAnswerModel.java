/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package hhs.core.quiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wjohnson000
 *
 */
public class QuizAnswerModel implements Comparable<QuizAnswerModel> {

    protected int      id;
    protected int      year;
    protected String[] options;
    protected String   answer;
    protected String   summary;
    protected String   text1;
    protected String   text2;
    protected String   text3;
    protected String   text4;
    protected String   image;
    protected String   language;
    protected String   region;
    protected String   itemId;

    public List<String> getDescription() {
        List<String> descr = new ArrayList<>();
        if (text1 != null    &&  ! text1.isEmpty())   descr.add(text1.trim());
        if (summary != null  &&  ! summary.isEmpty()) descr.add(summary.trim());
        if (text2 != null    &&  ! text2.isEmpty())   descr.add(text2.trim());
        if (text3 != null    &&  ! text3.isEmpty())   descr.add(text3.trim());
        if (text4 != null    &&  ! text4.isEmpty())   descr.add(text4.trim());
        return descr;
    }

    @Override
    public int compareTo(QuizAnswerModel that) {
        return this.year - that.year;
    }

    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("itemId=").append(itemId);
        buff.append(";  year=").append(year);
        buff.append(";  options=").append(Arrays.toString(options));
        buff.append(";  answer=").append(answer);
        buff.append(";  summary=").append(summary);
        return buff.toString();
    }

}
