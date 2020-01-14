package com.rank;

import lombok.Getter;

/**
 * Score范围区间
 */
@Getter
public class Range<S> {

    @Getter
    public enum RangeOpt {
        OPEN_CLOSE(false, true),
        OPEN_OPEN(false, false),
        CLOSE_OPEN(true, false),
        CLOSE_CLOSE(true, true),
        ;

        private boolean leftClose;
        private boolean rightClose;

        RangeOpt(boolean leftClose, boolean rightClose) {
            this.leftClose = leftClose;
            this.rightClose = rightClose;
        }
    }


    private S start;
    private S end;
    private RangeOpt rangeOpt;

    public Range(S start, S end) {
        this.start = start;
        this.end = end;
        this.rangeOpt = RangeOpt.CLOSE_CLOSE;
    }

    public Range(S start, S end, RangeOpt rangeOpt) {
        this.start = start;
        this.end = end;
        this.rangeOpt = rangeOpt;
    }


}
