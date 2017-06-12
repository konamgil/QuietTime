package quiettimev1.konamgil.com.quiettime.Util;

import java.util.Calendar;

/**
 * Created by konamgil on 2017-06-08.
 */

public class GetTime {
    /**
     * 설정된 시간 구하기
     * @param hour
     * @param minute
     * @return
     */
    Calendar calNow;

    public long getSelectTimeTypeLong(int hour, int minute){
        calNow = Calendar.getInstance();   // 현재 시간을 위한 Calendar 객체를 구한다.
        Calendar calSet = (Calendar)calNow.clone();   // 바로 위에서 구한 객체를 복제 한다.

        calSet.set(Calendar.HOUR_OF_DAY, hour);   // 타임피커에서 받아온 시간으로 시간 설정
        calSet.set(Calendar.MINUTE, minute);        // 타임피커에서 받아온 시간으로 분 설정
        calSet.set(Calendar.SECOND, 0);               // 초는 '0'으로 설정
        calSet.set(Calendar.MILLISECOND, 0);       // 밀리 초도  '0' 으로 설정

        if(calSet.compareTo(calNow) <= 0){            // 설정한 시간과 현재 시간 비교
            // 만약 설정한 시간이 현재 시간보다 이전이면
            calSet.add(Calendar.DATE, 1);  // 설정 시간에 하루를 더한다.
        }

        return calSet.getTimeInMillis();
    }

    public int getCurrentTime(){
        calNow = Calendar.getInstance();
        int hour = calNow.get(Calendar.HOUR);
        return hour;
    }

    public int getCurrentMinute(){
        calNow = Calendar.getInstance();
        int minute = calNow.get(Calendar.MINUTE);
        return minute;
    }
}
