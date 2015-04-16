package org.hibernate.util;

/**
 * Created by wizzardo on 07.04.15.
 */
public class CustomCharSequence implements CharSequence {
    private int offset;
    private int length;
    private char[] chars;

    public CustomCharSequence(char[] chars) {
        this(chars, 0, chars.length);
    }

    public CustomCharSequence(char[] chars, int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > chars.length)
            throw new IllegalArgumentException();

        this.chars = chars;
        this.offset = offset;
        this.length = length;
    }

    public CustomCharSequence(String s) {
        this(s, 0, s.length());
    }

    public CustomCharSequence(String value, int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > value.length())
            throw new IllegalArgumentException();

        char[] chars = StringReflection.chars(value);
        if (chars.length != value.length()) {
            offset += StringReflection.offset(value);
        }

        this.chars = chars;
        this.offset = offset;
        this.length = length;
    }

    public int length() {
        return length;
    }

    public int indexOf(CustomCharSequence sequence) {
        return indexOf(chars, offset, length, sequence.chars, sequence.offset, sequence.length, 0);
    }

    static int indexOf(char[] source, int sourceOffset, int sourceCount,
                       char[] target, int targetOffset, int targetCount,
                       int fromIndex) {
        if (fromIndex >= sourceCount) {
            return (targetCount == 0 ? sourceCount : -1);
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (targetCount == 0) {
            return fromIndex;
        }

        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);

        for (int i = sourceOffset + fromIndex; i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first) ;
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j] ==
                        target[k]; j++, k++)
                    ;

                if (j == end) {
                    /* Found whole string. */
                    return i - sourceOffset;
                }
            }
        }
        return -1;
    }

    public char charAt(int index) {
        return chars[offset + index];
    }

    public CustomCharSequence subSequence(int start, int end) {
        return new CustomCharSequence(chars, start + offset, end - start);
    }

    public CustomCharSequence substring(int start, int end) {
        return new CustomCharSequence(chars, start + offset, end - start);
    }

    public CustomCharSequence substring(int start) {
        return new CustomCharSequence(chars, start + offset, length - start);
    }

    public void appendTo(StringBuilder sb) {
        sb.append(chars, offset, length);
    }

    public void appendTo(StringBuffer sb) {
        sb.append(chars, offset, length);
    }

    public String toString(){
        return new String(chars, offset, length);
    }
}
