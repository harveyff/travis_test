package com.bytetrade.pro.bytemodule.chain;

import com.bytetrade.pro.bytemodule.chain.interfaces.ByteSerializable;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeSpec implements ByteSerializable, Serializable{

    private static final long serialVersionUID = 1L;

    public int value;

    public TimeSpec(int v) {
       value = v;
    }

    public static TimeSpec now() {
        /*return new TimeSpec( (int)((new Date()).getTime() / 1000l) );*/
        return new TimeSpec((int)(System.currentTimeMillis() / 1000l));
    }

    public TimeSpec() {
        value = 0;
    }

    public List<Byte> toBytes(){
        ByteBuffer b = ByteBuffer.allocate(4);

        b.putInt(Integer.reverseBytes(value));

        return Util.convertBytes(b.array());
    }

    @Override
    public String toString() {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Util.TIME_DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return (simpleDateFormat.format(new Date((long)value * 1000l)).toString());
    }

}
