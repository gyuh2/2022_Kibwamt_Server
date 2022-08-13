package baekkoji.smarthomeserver.dto;

import lombok.Data;

@Data
public class ControlData {

    private String device;
    private int angle;
    private boolean control;

    public int controlHome(){
        int result = 0;
        //DB에 저장하고, D 아두이노에 제어 요청을 하고 정상처리 되면 응답을 받아야함.
        System.out.println(device + ", " +  angle + ", " + control);
        result = 1;
        return result;
    }
}
